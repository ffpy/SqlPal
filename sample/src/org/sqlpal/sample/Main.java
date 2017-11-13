package org.sqlpal.sample;

import org.sqlpal.SqlPal;
import org.sqlpal.sample.crud.*;

public class Main {

    public static void main(String[] args) {
        // 初始化
        SqlPal.init("sample/sqlpal.xml");

        QueryTest.findAll();

        // 销毁
        SqlPal.destroy();
    }
}
