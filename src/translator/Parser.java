/**
 * Created by wayne on 10/14/15.
 */

package translator;

import ast.*;
import fj.data.List;
import fj.data.Seq;
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
        Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        Context.setGlobal(context.createGlobal());
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
        return new Program(List.list(array).map(Parser::parseStatement));
    }

    private static Statement parseStatement(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String type = object.get("type").getAsString();
        if (type.equals("EmptyStatement")) {
            return new EmptyStatement();
        } else if (type.equals("BlockStatement")) {
            JsonArray array = object.get("body").getAsJsonArray();
            return new BlockStatement(List.list(array).map(Parser::parseStatement));
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
            JsonElement ele1 = object.get("label");
            IdentifierExpression label = (IdentifierExpression)parseExpression(ele1);
            JsonElement ele2 = object.get("body");
            Statement body = parseStatement(ele2);
            return new LabeledStatement(label, body);
        } else if (type.equals("BreakStatement")) {
            JsonElement ele = object.get("label");
            IdentifierExpression label;
            if (ele.isJsonNull()) {
                label = null;
            } else {
                label = (IdentifierExpression)parseExpression(ele);
            }
            return new BreakStatement(label);
        } else if (type.equals("ContinueStatement")) {
            JsonElement ele = object.get("label");
            IdentifierExpression label;
            if (ele.isJsonNull()) {
                label = null;
            } else {
                label = (IdentifierExpression)parseExpression(ele);
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
            return new SwitchStatement(discriminant, List.list(array).map(Parser::parseSwitchCase));
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
            JsonElement ele4 = object.get("finalizer");
            BlockStatement block = (BlockStatement)parseStatement(ele1);
            CatchClause handler;
            if (ele2.isJsonNull()) {
                handler = null;
            } else {
                handler = parseCatchClause(ele2);
            }
            BlockStatement finalizer;
            if (ele4.isJsonNull()) {
                finalizer = null;
            } else {
                finalizer = (BlockStatement)parseStatement(ele4);
            }
            return new TryStatement(block, handler, finalizer);
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
            Node init;
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
            Node left;
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
            return new ThisExpression();
        } else if (type.equals("ArrayExpression")) {
            JsonElement ele1 = object.get("elements");
            return new ArrayExpression(Seq.seq(List.list(ele1.getAsJsonArray()).map(ele -> {
                if (ele.isJsonNull()) {
                    return null;
                } else {
                    return parseExpression(ele);
                }
            })));
        } else if (type.equals("ObjectExpression")) {
            JsonElement ele1 = object.get("properties");
            return new ObjectExpression(Seq.seq(List.list(ele1.getAsJsonArray()).map(Parser::parseProperty)));
        } else if (type.equals("FunctionExpression")) {
            JsonElement ele1 = object.get("id");
            IdentifierExpression id;
            if (ele1.isJsonNull()) {
                id = null;
            } else {
                id = (IdentifierExpression)parseExpression(ele1);
            }
            JsonElement ele2 = object.get("params");
            JsonElement ele3 = object.get("body");
            BlockStatement body = (BlockStatement)parseStatement(ele3);
            return new FunctionExpression(
                    id,
                    Seq.seq(List.list(ele2.getAsJsonArray()).map(ele -> (IdentifierExpression)parseExpression(ele))),
                    body);
        } else if (type.equals("SequenceExpression")) {
            JsonElement ele1 = object.get("expressions");
            return new SequenceExpression(List.list(ele1.getAsJsonArray()).map(Parser::parseExpression));
        } else if (type.equals("UnaryExpression")) {
            JsonElement ele1 = object.get("operator");
            String operator = ele1.getAsString();
            JsonElement ele2 = object.get("prefix");
            boolean prefix = ele2.getAsBoolean();
            JsonElement ele3 = object.get("argument");
            Expression argument = parseExpression(ele3);
            return new UnaryExpression(operator, prefix, argument);
        } else if (type.equals("BinaryExpression")) {
            JsonElement ele1 = object.get("operator");
            String operator = ele1.getAsString();
            JsonElement ele2 = object.get("left");
            Expression left = parseExpression(ele2);
            JsonElement ele3 = object.get("right");
            Expression right = parseExpression(ele3);
            return new BinaryExpression(operator, left, right);
        } else if (type.equals("AssignmentExpression")) {
            JsonElement ele1 = object.get("operator");
            String operator = ele1.getAsString();
            JsonElement ele2 = object.get("left");
            Expression left = parseExpression(ele2);
            JsonElement ele3 = object.get("right");
            Expression right = parseExpression(ele3);
            return new AssignmentExpression(operator, left, right);
        } else if (type.equals("UpdateExpression")) {
            JsonElement ele1 = object.get("operator");
            String operator = ele1.getAsString();
            JsonElement ele2 = object.get("argument");
            Expression argument = parseExpression(ele2);
            JsonElement ele3 = object.get("prefix");
            boolean prefix = ele3.getAsBoolean();
            return new UpdateExpression(operator, argument, prefix);
        } else if (type.equals("LogicalExpression")) {
            JsonElement ele1 = object.get("operator");
            String operator = ele1.getAsString();
            JsonElement ele2 = object.get("left");
            Expression left = parseExpression(ele2);
            JsonElement ele3 = object.get("right");
            Expression right = parseExpression(ele3);
            return new LogicalExpression(operator, left, right);
        } else if (type.equals("ConditionalExpression")) {
            JsonElement ele1 = object.get("test");
            Expression test = parseExpression(ele1);
            JsonElement ele2 = object.get("alternate");
            Expression alternate = parseExpression(ele2);
            JsonElement ele3 = object.get("consequent");
            Expression consequent = parseExpression(ele3);
            return new ConditionalExpression(test, alternate, consequent);
        } else if (type.equals("CallExpression")) {
            JsonElement ele1 = object.get("callee");
            Expression callee = parseExpression(ele1);
            JsonElement ele2 = object.get("arguments");
            return new CallExpression(callee, Seq.seq(List.list(ele2.getAsJsonArray()).map(Parser::parseExpression)));
        } else if (type.equals("NewExpression")) {
            JsonElement ele1 = object.get("callee");
            Expression callee = parseExpression(ele1);
            JsonElement ele2 = object.get("arguments");
            return new NewExpression(callee, Seq.seq(List.list(ele2.getAsJsonArray()).map(Parser::parseExpression)));
        } else if (type.equals("MemberExpression")) {
            JsonElement ele1 = object.get("object");
            Expression obj = parseExpression(ele1);
            JsonElement ele2 = object.get("property");
            Expression property = parseExpression(ele2);
            JsonElement ele3 = object.get("computed");
            boolean computed = ele3.getAsBoolean();
            return new MemberExpression(obj, property, computed);
        } else if (type.equals("Identifier")) {
            JsonElement ele1 = object.get("name");
            String name = ele1.getAsString();
            return new IdentifierExpression(name);
        } else if (type.equals("Literal")) {
            return new LiteralExpression(parseLiteral(element));
        } else {
            throw new RuntimeException("Cannot Parse Expression");
        }
    }

    private static Declaration parseDeclaration(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String type = object.get("type").getAsString();
        if (type.equals("FunctionDeclaration")) {
            JsonElement ele1 = object.get("id");
            IdentifierExpression id = (IdentifierExpression)parseExpression(ele1);
            JsonElement ele2 = object.get("params");
            JsonElement ele3 = object.get("body");
            BlockStatement body = (BlockStatement)parseStatement(ele3);
            return new FunctionDeclaration(
                    id,
                    Seq.seq(List.list(ele2.getAsJsonArray()).map(ele -> (IdentifierExpression)parseExpression(ele))),
                    body);
        } else if (type.equals("VariableDeclaration")) {
            return new VariableDeclaration(List.list(object.get("declarations").getAsJsonArray()).map(Parser::parseVariableDeclarator));
        } else {
            throw new RuntimeException("Cannot Parse Declaration");
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
        JsonElement ele2 = object.get("consequent");
        return new SwitchCase(test, List.list(ele2.getAsJsonArray()).map(Parser::parseStatement));
    }

    private static CatchClause parseCatchClause(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonElement ele1 = object.get("param");
        IdentifierExpression param = (IdentifierExpression)parseExpression(ele1);
        JsonElement ele2 = object.get("body");
        BlockStatement body = (BlockStatement)parseStatement(ele2);
        return new CatchClause(param, body);
    }

    private static VariableDeclarator parseVariableDeclarator(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        IdentifierExpression id = (IdentifierExpression)parseExpression(object.get("id"));
        Expression init;
        JsonElement ele = object.get("init");
        if (ele.isJsonNull()) {
            init = null;
        } else {
            init = parseExpression(ele);
        }
        return new VariableDeclarator(id, init);
    }

    private static Property parseProperty(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonElement ele1 = object.get("key");
        Node key;
        if (ele1.getAsJsonObject().get("type").getAsString().equals("Identifier")) {
            key = parseExpression(ele1);
        } else {
            key = parseLiteral(ele1);
        }
        JsonElement ele2 = object.get("value");
        Expression value = parseExpression(ele2);
        JsonElement ele3 = object.get("kind");
        String kind = ele3.getAsString();
        return new Property(key, value, kind);
    }

    private static Literal parseLiteral(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        JsonElement ele = object.get("value");
        if (ele.isJsonPrimitive()) {
            JsonPrimitive prim = ele.getAsJsonPrimitive();
            if (prim.isBoolean()) {
                return new BooleanLiteral(prim.getAsBoolean());
            } else if (prim.isNumber()) {
                return new NumberLiteral(prim.getAsNumber());
            } else if (prim.isString()) {
                return new StringLiteral(prim.getAsString());
            } else {
                throw new RuntimeException("Cannot Parse Literal");
            }
        } else if (ele.isJsonNull()) {
            return new NullLiteral();
        } else if (ele.isJsonObject()) {
            JsonObject obj = ele.getAsJsonObject();
            String pattern = obj.get("pattern").getAsString();
            String flags = obj.get("flags").getAsString();
            return new RegExpLiteral(pattern, flags);
        } else {
            throw new RuntimeException("Cannot Parse Literal");
        }
    }
}
