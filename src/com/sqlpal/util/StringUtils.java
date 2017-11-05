package com.sqlpal.util;

import com.sun.istack.internal.Nullable;

public class StringUtils {

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(@Nullable String[] strs) {
        return strs == null || strs.length == 0;
    }
}
