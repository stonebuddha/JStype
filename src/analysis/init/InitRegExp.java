package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitRegExp {
    public static final Domains.Object RegExp_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        throw new RuntimeException("unimplemented"); // TODO
                    }
            ),
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(2.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_Addr)
            )
    );

    public static final Domains.Object RegExp_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.RegExp_Addr),
                    "exec", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_exec_Addr),
                    "test", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_test_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_toString_Addr),
                    "source", Domains.Str.inject(Domains.Str.alpha("")),
                    "global", Domains.Bool.inject(Domains.Bool.False),
                    "ignoreCase", Domains.Bool.inject(Domains.Bool.False),
                    "multiline", Domains.Bool.inject(Domains.Bool.False),
                    "lastIndex", Domains.Num.inject(Domains.Num.alpha(0.0))
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CRegexp
            )
    );

    public static final Domains.Object RegExp_prototype_exec_Obj = InitUtils.unimplemented("RegExp.prototype.exec");
    public static final Domains.Object RegExp_prototype_test_Obj = InitUtils.unimplemented("RegExp.prototype.test");
    public static final Domains.Object RegExp_prototype_toString_Obj = InitUtils.unimplemented("RegExp.prototype.toString");
}
