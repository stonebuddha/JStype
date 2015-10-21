package nci.type;

import java.util.Date;

/**
 * Created by wayne on 15/10/21.
 */
public class ConcreteNumberType extends NumberType {
    Number value;
    public ConcreteNumberType(Number value) {
        this.value = value;
    }
    public NumberType upType() {
        double dv = this.value.doubleValue();
        long lv = this.value.longValue();
        if (dv == lv) {
            if (lv >= 0 && lv <= 4294967295l) {
                return NumberType.jsUInt;
            } else {
                return NumberType.jsNotUInt;
            }
        } else {
            if (Double.isNaN(dv)) {
                return NumberType.jsNaN;
            } else if (Double.isInfinite(dv)) {
                if (dv > 0) {
                    return NumberType.jsPINF;
                } else {
                    return NumberType.jsNINF;
                }
            } else {
                return NumberType.jsNotUInt;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof ConcreteNumberType) && (((ConcreteNumberType)obj).value.equals(this.value)));
    }
}
