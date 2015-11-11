package concrete.init;

import concrete.Domains;
import fj.Ord;
import fj.P;
import fj.data.TreeMap;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitArguments {
    public static Domains.Object Arguments_Obj = InitUtils.createFunctionObject(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                return InitUtils.makeState(selfAddr, x, env, store, pad, ks);
            }), TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.Object_prototype_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(0.0))));
}
