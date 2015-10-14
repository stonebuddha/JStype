/**
 * Created by wayne on 10/14/15.
 */

import ast.Node;
import ast.Program;
import ast.Statement;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.options.Options;
import com.google.gson.*;

import java.util.ArrayList;

public class Parser {

    public static Program parse(String code, String name) {
        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("parse.only", true);
        options.set("scripting", true);

        ErrorManager errors = new ErrorManager();
        Context contextm = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        Context.setGlobal(contextm.createGlobal());

        String json = ScriptUtils.parse(code, name, false);
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
        throw new RuntimeException("cannot parse");
    }

    private static Statement parseStatement(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            String type = object.get("type").getAsString();
            if (type.equals("EmptyStatement")) {

            } else if (type.equals("BlockStatement")) {

            } else if (type.equals("ExpressionStatement")) {

            } else if (type.equals("IfStatement")) {

            } else if (type.equals("LabeledStatement")) {

            } else if (type.equals("BreakStatement")) {

            } else if (type.equals("ContinueStatement")) {

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
        throw new RuntimeException("cannot parse");
    }
}
