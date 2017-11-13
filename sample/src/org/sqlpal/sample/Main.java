package org.sqlpal.sample;

import org.sqlpal.SqlPal;
import org.sqlpal.sample.crud.SaveTest;

public class Main {

    public static void main(String[] args) {
        // 初始化
        SqlPal.init("sample/sqlpal.xml");

        SaveTest.save();

        // 销毁
        SqlPal.destroy();
    }
}
