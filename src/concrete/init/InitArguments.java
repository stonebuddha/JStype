package concrete.init;

import concrete.Domains;
import concrete.Utils;
import immutable.FHashMap;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitArguments {
    public static Domains.Object Arguments_Obj = InitUtils.createFunctionObject(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                return InitUtils.makeState(selfAddr, x, env, store, pad, ks);
            }), FHashMap.map(
                    Utils.Fields.prototype, Init.Object_prototype_Addr,
                    Utils.Fields.length, new Domains.Num(0.0)));
}
