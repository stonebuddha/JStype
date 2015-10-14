/**
 * Created by wayne on 10/14/15.
 */

import ast.*;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.options.Options;
import com.google.gson.*;

import java.util.ArrayList;

public class Parser {

    public static void init() {
        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("parse.only", true);
        options.set("scripting", true);

        ErrorManager errors = new ErrorManager();
        Context contextm = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        Context.setGlobal(contextm.createGlobal());
    }

    public static String rawParse(String code, String name) {
        return ScriptUtils.parse(code, name, false);
    }

    public static Program parse(String code, String name) {
        String json = rawParse(code, name);
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        return parseProgram(element);
    }

    private static Program parseProgram(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("type").getAsString().equals("Program")) {
                if (object.get("body").isJsonArray()) {
                    JsonArray array = object.get("body").getAsJsonArray();
                    ArrayList<Statement> body = new ArrayList<>();
                    for (JsonElement ele : array) {
                        body.add(parseStatement(ele));
                    }
                    return new Program(body);
                }
            }
        }
        throw new RuntimeException("cannot parse Program");
    }

    private static Statement parseStatement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            String type = object.get("type").getAsString();
            if (type.equals("EmptyStatement")) {
                return new EmptyStatement();
            } else if (type.equals("BlockStatement")) {
                JsonArray array = object.get("body").getAsJsonArray();
                ArrayList<Statement> body = new ArrayList<>();
                for (JsonElement ele : array) {
                    body.add(parseStatement(ele));
                }
                return new BlockStatement(body);
            } else if (type.equals("ExpressionStatement")) {
                JsonElement ele = object.get("expression");
                Expression expression = parseExpression(ele);
                return new ExpressionStatement(expression);
            } else if (type.equals("IfStatement")) {
                JsonElement ele1 = object.get("test");
                JsonElement ele2 = object.get("consequent");
                JsonElement ele3 = object.get("alternate");
                Expression test = parseExpression(ele1);
                Statement consequent = parseStatement(ele2);
                Statement alternate;
                if (ele3.isJsonNull()) {
                    alternate = null;
                } else {
                    alternate = parseStatement(ele3);
                }
                return new IfStatement(test, consequent, alternate);
            } else if (type.equals("LabeledStatement")) {
                JsonElement ele = object.get("label");
                String label = parseIdentifier(ele);
                return new LabeledStatement(label);
            } else if (type.equals("BreakStatement")) {
                JsonElement ele = object.get("label");
                String label = parseIdentifier(ele);
                return new BreakStatement(label);
            } else if (type.equals("ContinueStatement")) {
                JsonElement ele = object.get("label");
                String label = parseIdentifier(ele);
                return new ContinueStatement(label);
            } else if (type.equals("WithStatement")) {

            } else if (type.equals("SwitchStatement")) {

            } else if (type.equals("ReturnStatement")) {

            } else if (type.equals("ThrowStatement")) {

            } else if (type.equals("TryStatement")) {

            } else if (type.equals("WhileStatement")) {

            } else if (type.equals("DoWhileStatement")) {

            } else if (type.equals("ForStatement")) {

            } else if (type.equals("ForInStatement")) {

            } else if (type.equals("ForOfStatement")) {

            } else if (type.equals("LetStatement")) {

            } else if (type.equals("DebuggerStatement")) {

            }
        }
        throw new RuntimeException("cannot parse Statement");
    }

    private static Expression parseExpression(JsonElement element) {
        throw new RuntimeException("cannot parse Expression");
    }

    private static String parseIdentifier(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.get("type").getAsString().equals("Identifier")) {
                return object.get("name").getAsString();
            }
        }
        throw new RuntimeException("cannot parse Identifier");
    }
}
