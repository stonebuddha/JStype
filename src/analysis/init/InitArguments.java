package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/2.
 */
public class InitArguments {
    public static final Domains.Object Dummy_Arguments_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0))
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CArguments
            )
    );


    public static final Domains.Object Dummy_Obj = InitUtils.createObj();

    public static final Domains.Object Arguments_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        Boolean calledAsConstr = argsObj.intern.get(Utils.Fields.constructor).map(o -> (Boolean)o).orSome(false);
                        return InitUtils.makeState(selfAddr, x, env, store, pad, ks, tr);
                    }
            ),
            FHashMap.<String, Domains.BValue>build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0))
            ),
            FHashMap.empty(),
            JSClass.CFunction
    );
}
