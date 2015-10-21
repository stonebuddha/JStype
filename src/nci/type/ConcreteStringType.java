package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class ConcreteStringType extends StringType {
    String value;
    public ConcreteStringType(String value) {
        this.value = value;
    }
    public StringType upType() {
        try {
            long lv = Long.parseUnsignedLong(this.value);
            if (lv <= 4294967295l) {
                return StringType.jsUIntString;
            } else {
                return StringType.jsNotUIntString;
            }
        } catch (Exception e) {
            return StringType.jsNotUIntString;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof ConcreteStringType) && (((ConcreteStringType)obj).value.equals(this.value)));
    }
}
