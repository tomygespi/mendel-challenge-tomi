package com.challenge.java.tomi.domain.transaction;

import java.util.HashMap;
import java.util.Map;

public enum TypeEnum {
    ARS("ARS"),
    MXN("MXN");

    private static class Holder {
        static Map<String, TypeEnum> TYPE_MAP = new HashMap<>();
    }

    TypeEnum(String value) {
        Holder.TYPE_MAP.put(value, this);
    }

    public static TypeEnum find(String value) {
        return Holder.TYPE_MAP.get(value);
    }
}
