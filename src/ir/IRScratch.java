package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRScratch extends IRVar {
    public Integer n;

    public IRScratch(Integer n) {
        this.n = n;
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forScratch(this);
    }

    static Integer counter = 0;
    public static IRScratch generate() {
        Integer res = counter;
        counter += 1;
        return new IRScratch(res);
    }
}
