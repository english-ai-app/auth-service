package com.EnglishApp.auth_service.common;

import java.util.Collection;
import java.util.List;

public class DataUtil {
    public static boolean nullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(CharSequence cs) {
        return nullOrEmpty(cs);
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean hasText(CharSequence cs) {
        return !isNullOrEmpty(cs);
    }

    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public static String emptyToDefault(String value, String defaultValue) {
        return isNullOrEmpty(value) ? defaultValue : value;
    }

    public static boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        if (obj1 instanceof List) {
            return ((List) obj1).isEmpty();
        }
        if (obj1.toString().trim().equals("")) {
            return true;
        }
        return false;
    }
}
