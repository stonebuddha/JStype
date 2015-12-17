package analysis.init;

import analysis.Domains;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;
import immutable.FHashSet;

/**
 * Created by Hwhitetooth on 15/12/9.
 */
//TODO
public class StringHelpers {
    public static Domains.Object newArray(Domains.Num length, List<Domains.BValue> exactEntries, Option<Domains.BValue> summaryVal, Domains.Object origArray, Boolean update) {
        FHashMap<Domains.Str, Domains.BValue> exactnum =
        exactEntries.zipIndex().foldLeft((acc, cur) -> {
            return acc.set(Domains.Str.alpha(cur._2().toString()), cur._1());
        }, FHashMap.empty());

        if (update) {
            FHashMap<Domains.Str, Domains.BValue> exactnotnum = origArray.extern.exactnotnum.set(Domains.Str.alpha("length"), Domains.Num.inject(length));
            return new Domains.Object(
                    new Domains.ExternMap(
                            origArray.extern.top,
                            origArray.extern.notnum,
                            summaryVal,
                            exactnotnum,
                            exactnum
                    ),
                    origArray.intern,
                    FHashSet.build(exactnotnum.keys().append(exactnum.keys()))
            );
        } else {
            FHashMap<Domains.Str, Domains.BValue> exactnotnum = FHashMap.build(Domains.Str.alpha("length"), Domains.Num.inject(length));
            return new Domains.Object(
                    new Domains.ExternMap(
                            Option.none(),
                            Option.none(),
                            summaryVal,
                            exactnotnum,
                            exactnum
                    ),
                    origArray.intern,
                    FHashSet.build(exactnotnum.keys().append(exactnum.keys()))
            );
        }
    }

    public static Domains.Object newArrayBuffer(Domains.BValue length_bv, Domains.Object oldObj) {
        return new Domains.Object(
                new Domains.ExternMap(Option.none(), Option.none(), Option.some(Domains.Num.inject(Domains.Num.Zero).merge(Domains.Undef.BV)), FHashMap.build(Domains.Str.alpha("byteLength"), length_bv), FHashMap.empty()),
                oldObj.intern,
                oldObj.present.insert(Domains.Str.alpha("byteLength"))
        );
    }
}
