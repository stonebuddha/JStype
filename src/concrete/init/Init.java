package concrete.init;

import concrete.Domains;
import concrete.Interpreter;
import fj.Ord;
import fj.data.List;
import fj.data.Set;
import fj.data.TreeMap;
import ir.IRPVar;
import ir.IRStmt;
import ir.JSClass;

/**
 * Created by wayne on 15/10/29.
 */
public class Init {

    public static final IRPVar window_Variable = new IRPVar(0);

    public static final Domains.Address Array_Addr = Domains.Address.generate();
    public static final Domains.Address String_Addr = Domains.Address.generate();
    public static final Domains.Address Boolean_Addr = Domains.Address.generate();
    public static final Domains.Address Number_Addr = Domains.Address.generate();
    public static final Domains.Address Date_Addr = Domains.Address.generate();
    public static final Domains.Address Error_Addr = Domains.Address.generate();
    public static final Domains.Address RegExp_Addr = Domains.Address.generate();
    public static final Domains.Address Arguments_Addr = Domains.Address.generate();
    public static final Domains.Address Function_Addr = Domains.Address.generate();
    public static final Domains.Address Function_prototype_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_Addr = Domains.Address.generate();
    // TODO

    public static Interpreter.State initState(IRStmt s) {
        Domains.Env env = new Domains.Env(TreeMap.empty(Ord.hashEqualsOrd()));
        Domains.Store store = new Domains.Store(
                TreeMap.empty(Ord.hashEqualsOrd()),
                TreeMap.empty(Ord.hashEqualsOrd()));
        return new Interpreter.State(
                new Domains.StmtTerm(s),
                env,
                store,
                Domains.Scratchpad.apply(0),
                new Domains.KontStack(List.list(Domains.HaltKont)));
    }

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = null; // TODO

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = null; // TODO

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = null; // TODO

    public static final JSClass classFromAddress(Domains.Address a) {
        if (a == Function_Addr) {
            return JSClass.CFunction;
        }
        else if (a == Array_Addr) {
            return JSClass.CArray;
        }
        else if (a == String_Addr) {
            return JSClass.CString;
        }
        else if (a == Boolean_Addr) {
            return JSClass.CBoolean;
        }
        else if (a == Number_Addr) {
            return JSClass.CNumber;
        }
        else if (a == Date_Addr) {
            return JSClass.CDate;
        }
        else if (a == Error_Addr) {
            return JSClass.CError;
        }
        else if (a == RegExp_Addr) {
            return JSClass.CRegExp;
        }
        else if (a == Arguments_Addr) {
            return JSClass.CArguments;
        }
        else {
            return JSClass.CObject;
        }
    }
}
