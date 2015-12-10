package analysis.init;

import analysis.Domains;
import immutable.FHashMap;

/**
 * Created by wayne on 15/12/7.
 */
public class InitJSON {
    public static final Domains.Object JSON_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "parse", Domains.AddressSpace.Address.inject(Init.JSON_parse_Addr),
                    "stringify", Domains.AddressSpace.Address.inject(Init.JSON_stringify_Addr)
            )
    );

    public static final Domains.Object JSON_parse_Obj = InitUtils.unimplemented("JSON.parse");
    public static final Domains.Object JSON_stringify_Obj = InitUtils.unimplemented("JSON.stringify");
}
