package analysis.init;

import analysis.Domains;
import analysis.Traces;
import analysis.Interpreter;
import fj.F7;
import fj.data.List;
import ir.IRVar;

/**
 * Created by Hwhitetooth on 15/12/4.
 */
public class InitDate {
    public static F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, Interpreter.State> Internal_Date_constructor_afterToNumber = InitUtils.genValueObjConstructor("Date", any-> Domains.Num.inject(Domains.NTop));

    public static Domains.Object Date_Obj = InitUtils.createInitFunctionObj();
}
