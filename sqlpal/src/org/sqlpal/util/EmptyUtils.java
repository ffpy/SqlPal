package org.sqlpal.util;

import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * 空检查工具类
 */
public class EmptyUtils {

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
