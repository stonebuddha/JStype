package analysis.init;

import analysis.Domains;
import fj.Ord;
import fj.P;
import fj.data.Set;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by wayne on 15/11/5.
 */
public class Init {

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = null;

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = null;

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = null;

    public static Set<Domains.AddressSpace.Address> keepInStore; // TODO

    public static final Domains.AddressSpace.Address Function_prototype_Addr = null; //TODO

    public static final Domains.AddressSpace.Address Object_prototype_Addr = null;

    public static final TreeMap<Domains.AddressSpace.Address, JSClass> classFromAddress = null;/*TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(Function_Addr, JSClass.CFunction),
            P.p(Array_Addr, JSClass.CArray),
            P.p(String_Addr, JSClass.CString),
            P.p(Boolean_Addr, JSClass.CBoolean),
            P.p(Number_Addr, JSClass.CNumber),
            P.p(Date_Addr, JSClass.CDate),
            P.p(Error_Addr, JSClass.CError),
            P.p(RegExp_Addr, JSClass.CRegexp),
            P.p(Arguments_Addr, JSClass.CArguments));*/
}
