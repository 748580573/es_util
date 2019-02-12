package com.heng.enumerate;

/**
 * 基础类型枚举
 */
public enum BaseEnum {

    BOOLEAN(boolean.class,0,"boolean"),
    BYTE(byte.class,1,"binary"),
    SHORT(short.class,2,"short"),
    CHAR(char.class,3,"text"),
    INT(int.class,4,"integer"),
    FLOAT(float.class,5,"float"),
    LONG(long.class,6,"long"),
    DOUBLE(double.class,7,"double"),
    STRING(String.class,8,"text"),
    _INTEGER(Integer.class,9,"integer"),
    _FLOAT(Float.class,10,"float"),
    _DOUBLE(Double.class,10,"double"),
    _LONG(Long.class,11,"long"),


    //此处将基础类型数组和String类型的数组都当作基础类型看待
    BOOLEAN_(boolean[].class,12 ,"boolean"),
    BYTE_(byte[].class,13,"byte"),
    SHORT_(short[].class,14,"short"),
    CHAR_(char[].class,15,"char"),
    INT_(int[].class,16,"int"),
    FLOAT_(float[].class,17,"float"),
    LONG_(long[].class,18,"long"),
    DOUBLE_(double[].class,19,"double"),

    STRING_ARRAY(java.lang.String[].class, 20,"string"),
    BOOLEAN_ARRAY(java.lang.Boolean[].class,21,"boolean"),
    BYTE_ARRAY(java.lang.Byte[].class,22,"byte"),
    SHORT_ARRAY(java.lang.Byte[].class,23,"short"),
    CHAR_ARRAY(java.lang.Character[].class,24,"char"),
    INT_ARRAY(java.lang.Integer[].class,25,"int"),
    FLOAT_ARRAY(java.lang.Float[].class,26,"float"),
    LONG_ARRAY(java.lang.Long[].class,27,"long"),
    DOUBLE_ARRAY(java.lang.Double[].class,28,"double");
    private Class<?> clazz;

    private int index;

    private String type;

    BaseEnum(Class<?> clazz, int index, String type){
        this.clazz = clazz;
        this.index = index;
        this.type = type;
    }

    public static String getType(Class<?> clazz){
        for (BaseEnum baseEnum : BaseEnum.values()){
            if (baseEnum.clazz == clazz){
                return baseEnum.type;
            }
        }
        return null;
    }
}
