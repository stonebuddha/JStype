package analysis.init;

import analysis.Domains;
import analysis.Utils;
import fj.F;
import fj.F2;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by Hwhitetooth on 15/12/4.
 */
public class InitBoolean {
    public static Domains.Object Boolean_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks, trace)-> {
                assert argArrayAddr.defAddr() : "Boolean: Arguments array refers to non-addresses";
                assert argArrayAddr.as.size() == 1 : "Boolean: Arguments array refers to multiple addresses";
                Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                Domains.BValue input = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                Domains.BValue in_bool = input.toBool();
                Boolean calledAsConstr = (Boolean)argsObj.intern.get(Utils.Fields.constructor).orSome(false);
                if (calledAsConstr) {
                    F<Domains.BValue, Void> check = bv-> {
                        assert ((Domains.BValue)bv).defBool() : "Boolean: in_bool should be a boolean; refactor valueObjConstructor";
                        return null;
                    };
                    return FHashSet.build(InitUtils.valueObjConstructor("Boolean", check).f(List.list(selfAddr, in_bool), x, env, store, pad, ks, trace));
                } else {
                    return FHashSet.build(InitUtils.makeState(in_bool, x, env, store, pad, ks, trace));
                }
            }), FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.Boolean_prototype_Addr)
            ));

    public static Domains.Object Boolean_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.Boolean_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Boolean_prototype_toString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Boolean_prototype_valueOf_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CBoolean,
                    Utils.Fields.value, Domains.Bool.inject(Domains.BFalse)
            )
    );

    public static Domains.Object Boolean_prototype_toString_Obj = InitUtils.usualToPrim(any-> any.equals(JSClass.CBoolean), any-> any.b.toStr(), Domains.Str.Bot, any-> Domains.Str.inject((Domains.Str)any), p-> ((Domains.Str)p._1()).merge((Domains.Str)p._2()));

    public static Domains.Object Boolean_prototype_valueOf_Obj = InitUtils.usualToPrim(any-> any.equals(JSClass.CBoolean), any-> any.b, Domains.Bool.Bot, any-> Domains.Bool.inject((Domains.Bool)any), p-> ((Domains.Bool)p._1()).merge((Domains.Bool)p._2()));
}
