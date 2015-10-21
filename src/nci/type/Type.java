package nci.type;

/**
 * Created by wayne on 15/10/20.
 */
public class Type {
    UndefinedType undefinedType;
    NullType nullType;
    BooleanType booleanType;
    NumberType numberType;
    StringType stringType;
    ObjectType objectType;

    public Type(UndefinedType undefinedType, NullType nullType, BooleanType booleanType, NumberType numberType, StringType stringType, ObjectType objectType) {
        this.undefinedType = undefinedType;
        this.nullType = nullType;
        this.booleanType = booleanType;
        this.numberType = numberType;
        this.stringType = stringType;
        this.objectType = objectType;
    }

    static Type merge(Type a, Type b) {
        return new Type(
                UndefinedType.merge(a.undefinedType, b.undefinedType),
                NullType.merge(a.nullType, b.nullType),
                BooleanType.merge(a.booleanType, b.booleanType),
                NumberType.merge(a.numberType, b.numberType),
                StringType.merge(a.stringType, b.stringType),
                ObjectType.merge(a.objectType, b.objectType)
        );
    }

    public Type mergeBinary(String operator, Type aType) {
        return null;
    }

    public Type mergeLogical(String operator, Type aType) {
        return null;
    }
}
