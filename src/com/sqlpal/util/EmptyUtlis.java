package com.sqlpal.util;

import com.sun.istack.internal.Nullable;

import java.util.List;

public class EmptyUtlis {

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(@Nullable String[] strs) {
        return strs == null || strs.length == 0;
    }
}
