/**
 * Created by wayne on 10/14/15.
 */

import ast.*;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.ir.Block;
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
        JsonObject object = element.getAsJsonObject();
        JsonArray array = object.get("body").getAsJsonArray();
        ArrayList<Statement> body = new ArrayList<>();
        for (JsonElement ele : array) {
            body.add(parseStatement(ele));
        }
        return new Program(body);
    }

    private static Statement parseStatement(JsonElement element) {
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
            String label;
            if (ele.isJsonNull()) {
                label = null;
            } else {
                label = parseIdentifier(ele);
            }
            return new BreakStatement(label);
        } else if (type.equals("ContinueStatement")) {
            JsonElement ele = object.get("label");
            String label;
            if (ele.isJsonNull()) {
                label = null;
            } else {
                label = parseIdentifier(ele);
            }
            return new ContinueStatement(label);
        } else if (type.equals("WithStatement")) {
            JsonElement ele1 = object.get("object");
            JsonElement ele2 = object.get("body");
            Expression obj = parseExpression(ele1);
            Statement body = parseStatement(ele2);
            return new WithStatement(obj, body);
        } else if (type.equals("SwitchStatement")) {
            JsonElement ele1 = object.get("discriminant");
            JsonArray array = object.get("cases").getAsJsonArray();
            Expression discriminant = parseExpression(ele1);
            ArrayList<SwitchCase> cases = new ArrayList<>();
            for (JsonElement ele : array) {
                cases.add(parseSwitchCase(ele));
            }
            return new SwitchStatement(discriminant, cases);
        } else if (type.equals("ReturnStatement")) {
            JsonElement ele = object.get("argument");
            Expression argument;
            if (ele.isJsonNull()) {
                argument = null;
            } else {
                argument = parseExpression(ele);
            }
            return new ReturnStatement(argument);
        } else if (type.equals("ThrowStatement")) {
            JsonElement ele = object.get("argument");
            Expression argument = parseExpression(ele);
            return new ThrowStatement(argument);
        } else if (type.equals("TryStatement")) {
            JsonElement ele1 = object.get("block");
            JsonElement ele2 = object.get("handler");
            JsonArray array = object.get("guardedHandlers").getAsJsonArray();
            JsonElement ele4 = object.get("finalizer");
            BlockStatement block = (BlockStatement)parseStatement(ele1);
            CatchClause handler;
            if (ele2.isJsonNull()) {
                handler = null;
            } else {
                handler = parseCatchClause(ele2);
            }
            ArrayList<CatchClause> guardedHandler = new ArrayList<>();
            for (JsonElement ele : array) {
                guardedHandler.add(parseCatchClause(ele));
            }
            BlockStatement finalizer;
            if (ele4.isJsonNull()) {
                finalizer = null;
            } else {
                finalizer = (BlockStatement)parseStatement(ele4);
            }
            return new TryStatement(block, handler, guardedHandler, finalizer);
        } else if (type.equals("WhileStatement")) {
            JsonElement ele1 = object.get("test");
            JsonElement ele2 = object.get("body");
            Expression test = parseExpression(ele1);
            Statement body = parseStatement(ele2);
            return new WhileStatement(test, body);
        } else if (type.equals("DoWhileStatement")) {
            JsonElement ele1 = object.get("body");
            JsonElement ele2 = object.get("test");
            Statement body = parseStatement(ele1);
            Expression test = parseExpression(ele2);
            return new DoWhileStatement(body, test);
        } else if (type.equals("ForStatement")) {
            JsonElement ele1 = object.get("init");
            Object init;
            if (ele1.isJsonNull()) {
                init = null;
            } else {
                String type1 = ele1.getAsJsonObject().get("type").getAsString();
                if (type1.equals("VariableDeclaration")) {
                    init = parseDeclaration(ele1);
                } else {
                    init = parseExpression(ele1);
                }
            }
            JsonElement ele2 = object.get("test");
            Expression test;
            if (ele2.isJsonNull()) {
                test = null;
            } else {
                test = parseExpression(ele2);
            }
            JsonElement ele3 = object.get("update");
            Expression update;
            if (ele3.isJsonNull()) {
                update = null;
            } else {
                update = parseExpression(ele3);
            }
            JsonElement ele4 = object.get("body");
            Statement body = parseStatement(ele4);
            return new ForStatement(init, test, update, body);
        } else if (type.equals("ForInStatement")) {
            JsonElement ele1 = object.get("left");
            String type1 = ele1.getAsJsonObject().get("type").getAsString();
            Object left;
            if (type1.equals("VariableDeclaration")) {
                left = parseDeclaration(ele1);
            } else {
                left = parseExpression(ele1);
            }
            JsonElement ele2 = object.get("right");
            Expression right = parseExpression(ele2);
            JsonElement ele3 = object.get("body");
            Statement body = parseStatement(ele3);
            return new ForInStatement(left, right, body);
        } else if (type.equals("DebuggerStatement")) {
            return new DebuggerStatement();
        } else {
            return parseDeclaration(element);
        }
    }

    private static Expression parseExpression(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String type = object.get("type").getAsString();
        if (type.equals("ThisExpression")) {

        } else if (type.equals("ArrayExpression")) {

        } else if (type.equals("ObjectExpression")) {

        } else if (type.equals("FunctionExpression")) {

        } else if (type.equals("SequenceExpression")) {

        } else if (type.equals("UnaryExpression")) {

        } else if (type.equals("BinaryExpression")) {

        } else if (type.equals("AssignmentExpression")) {

        } else if (type.equals("UpdateExpression")) {

        } else if (type.equals("LogicalExpression")) {

        } else if (type.equals("ConditionalExpression")) {

        } else if (type.equals("CallExpression")) {

        } else if (type.equals("NewExpression")) {

        } else if (type.equals("MemberExpression")) {

        } else if (type.equals("Identifier")) {
            String name = parseIdentifier(element);
            return new IdentifierExpression(name);
        } else {
            throw new RuntimeException("cannot parse Expression");
        }
    }

    private static Declaration parseDeclaration(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String type = object.get("type").getAsString();
        if (type.equals("FunctionDeclaration")) {
            JsonElement ele1 = object.get("id");
            String id = parseIdentifier(ele1);
            ArrayList<String> params = new ArrayList<>();
            JsonElement ele2 = object.get("params");
            for (JsonElement ele: ele2.getAsJsonArray()) {
                params.add(parseIdentifier(ele));
            }
            JsonElement ele3 = object.get("body");
            BlockStatement body = (BlockStatement)parseStatement(ele3);
            return new FunctionDeclaration(id, params, body);
        } else if (type.equals("VariableDeclaration")) {
            ArrayList<VariableDeclarator> declarations = new ArrayList<>();
            for (JsonElement ele : object.get("declarations").getAsJsonArray()) {
                declarations.add(parseVariableDeclarator(ele));
            }
            return new VariableDeclaration(declarations);
        } else {
            throw new RuntimeException("cannot parse Declaration");
        }
    }

    private static SwitchCase parseSwitchCase(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonElement ele1 = object.get("test");
        Expression test;
        if (ele1.isJsonNull()) {
            test = null;
        } else {
            test = parseExpression(ele1);
        }
        ArrayList<Statement> consequent = new ArrayList<>();
        JsonElement ele2 = object.get("consequent");
        for (JsonElement ele : ele2.getAsJsonArray()) {
            consequent.add(parseStatement(ele));
        }
        return new SwitchCase(test, consequent);
    }

    private static String parseIdentifier(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        return object.get("name").getAsString();
    }

    private static CatchClause parseCatchClause(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonElement ele1 = object.get("param");
        String param = parseIdentifier(ele1);
        JsonElement ele2 = object.get("body");
        BlockStatement body = (BlockStatement)parseStatement(ele2);
        return new CatchClause(param, body);
    }

    private static VariableDeclarator parseVariableDeclarator(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String id = parseIdentifier(object.get("id"));
        Expression init;
        JsonElement ele = object.get("init");
        if (ele.isJsonNull()) {
            init = null;
        } else {
            init = parseExpression(ele);
        }
        return new VariableDeclarator(id, init);
    }
}
