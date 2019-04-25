package com.cmdjojo.util.dialog;

public enum Input {
    BYTE(Byte::parseByte),
    SHORT(Short::parseShort),
    INT(Integer::parseInt),
    LONG(Long::parseLong),
    DOUBLE(Double::parseDouble),
    FLOAT(Float::parseFloat),
    STRING(String::toString);

    private final Verificator verificator;

    Input(Verificator verificator) {
        this.verificator = verificator;
    }

    public boolean verify(String s) {
        return verificator.accept(s);
    }

    public Object convert(String s) {
        if (s == null) return null;
        if (!verify(s)) throw new IllegalArgumentException("Cannot convert " + s + " to " + name());
        try {
            return verificator.convert(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private interface Verificator {
        default boolean accept(String s) {
            try {
                convert(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        Object convert(String s) throws Exception;
    }
}
