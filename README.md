## SqlPal
SqlPal是一款封装了JDBC API的对象关系映射(ORM)框架，
参考了[LitePal](https://github.com/LitePalFramework/LitePal)的API设计,
目的是追求简洁的数据库操作

## 使用示例
```
// 初始化
SqlPal.init("sqlpal.xml", new SimpleConnectionFactory());
// 创建一条记录
User user = new User("admin", "123");
try {
    // 保存到数据库
    user.save();
} catch (SQLException e) {
    e.printStackTrace();
}
// 销毁
SqlPal.destroy();
```

## 最新版本
- [sqlpal-0.0.1.jar](https://raw.githubusercontent.com/ffpy/SqlPal/master/downloads/sqlpal-0.0.1.jar)

## 原理说明
通过运行时注解和反射来封装JDBC API

## 使用方法
### 添加依赖包
- sqlpal.jar
- mysql-connector-java.jar(数据库驱动程序，这里以Mysql数据库为例)

### 配置sqlpal.xml
在项目的根目录下新建一个sqlpal.xml文件，将以下内容复制进去
```
<?xml version="1.0" encoding="utf-8" ?>
<sqlpal>
    <!-- 驱动程序名 -->
    <driver>com.mysql.jdbc.Driver</driver>
    <!-- 连接数据库的URL -->
    <url>jdbc:mysql://localhost:3306/test?&amp;useUnicode=true&amp;characterEncoding=utf-8</url>
    <!-- 数据库用户名 -->
    <username>root</username>
    <!-- 数据库密码 -->
    <password>123456</password>
    <!-- 最大批处理数量 -->
    <maxBatch>10000</maxBatch>
    <!-- 映射列表 -->
    <list>
        <!-- 用户表 -->
        <mapping>model.User</mapping>
        <!-- 新闻表 -->
        <mapping>model.News</mapping>
    </list>
</sqlpal>
```

### 建立测试表
```
CREATE TABLE `user` (
  `username` varchar(100) NOT NULL,
  `password` varchar(64) NOT NULL,
  `age` int(11) NOT NULL,
  PRIMARY KEY (`username`)
);
```
```
CREATE TABLE `news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
);
```

### 建立模型类
```
@Table(name = "user")
public class User extends DataSupport {
    @PrimaryKey
    private String username;
    private String password;
    private Integer age;

    // get、set方法
}
```
```
@Table(name = "news")
public class News extends DataSupport {
    @PrimaryKey
    @AutoIncrement
    private Integer id;
    private String title;
    private String content;

    // get、set方法
}
```
- 字段类型要使用包装类，例如不能用int，要用Integer
- @TableName注解用于指定模型类所映射的表名
- @PrimaryKey注解用于指定主键
- @AutoIncrement注解表示主键是自增的

### 初始化
```
SqlPal.init("sqlpal.xml", new SimpleConnectionFactory());
```
- 放在程序的初始化的位置，只能执行一次
- sqlpal.xml是配置文件的路径
- SimpleConnectionFactory是内置的连接工厂，可以实现ConnectionFactory接口来
定制自己的连接工厂，比如使用连接池

### 销毁
```
SqlPal.destroy();
```
- 放在程序结束的位置
- 释放占用的资源

### 存储数据
#### 单个存储
```
// 建立模型对象
User user = new User();
user.setUsername("admin");
user.setPassword("123");
user.setAge(20);
try {
    // 存储
    user.save();
} catch (SQLException e) {
    e.printStackTrace();
}
```
如果主键是自增主键，并且添加了@AutoIncrement注解，那么在执行存储操作后会自动填充自增主键的值  
例如执行以下语句
```
News news = new News();
news.setTitle("title1");
news.setContent("content1");
try {
    news.save();
    System.out.println(news.getId());
} catch (SQLException e) {
    e.printStackTrace();
}
```
news对象在执行save操作后会自动填入id值
### 批量存储
使用了批处理，效率更高
```
// 建立模型对象1
User user1 = new User();
user1.setUsername("user1");
user1.setPassword("pwd1");
user1.setAge(18);
// 建立模型对象2
User user2 = new User();
user2.setUsername("user2");
user2.setPassword("pwd2");
user2.setAge(20);
// 建立模型对象列表
List<User> users = new ArrayList<>();
users.add(user1);
users.add(user2);
try {
    // 批量存储
    DataSupport.saveAll(users);
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 修改数据
#### 单个修改
```
User user = new User();
// 要修改的行的主键
user.setUsername("admin");
// 修改的列和值
user.setAge(30);
try {
    // 修改
    user.update();
} catch (SQLException e) {
    e.printStackTrace();
}
```
### 批量修改
类似于批量存储
#### 约束修改
```
// 将age大于10的所有行的密码修改为aaa
User user = new User();
user.setPassword("aaa");
try {
    user.updateAll("age > ?", "10");
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 删除数据
#### 单个删除
```
User user = new User();
// 要删除的行的主键
user.setUsername("admin");
try {
    // 删除
    user.delete();
} catch (SQLException e) {
    e.printStackTrace();
}
```
#### 批量删除
类似于批量存储
#### 约束删除
```
// 删除age大于10的行
try {
    DataSupport.deleteAll(User.class, "age > ?", "10");
} catch (SQLException e) {
    e.printStackTrace();
}
```

### 查询数据
#### 查询user表的所有数据
```
List<User> users = DataSupport.findAll(User.class);
```
#### 查询user表的第一条数据
```
User user = DataSupport.findFirst(User.class);
```
#### 查询user表的最后一条数据
```
User user = DataSupport.findLast(User.class);
```
#### 连缀查询
如果你要查找age大于18的用户，添加where连缀
```
List<User> users = DataSupport.where("age > ?", "18").find(User.class);
```
如果你只需要username和password这两列，添加select连缀
```
List<User> users = DataSupport.select("username, password").where("age > ?", "18").find(User.class);
```
如果你想按照age倒序排序，添加order连缀
```
List<User> users = DataSupport.select("username, password").where("age > ?", "18").order("age desc").find(User.class);
```
如果你只要取出前5条数据，添加limit连缀
```
List<User> users = DataSupport.select("username, password").where("age > ?", "18").order("age desc").limit(5).find(User.class);
```
如果你要分页展示，需要添加偏移量，添加offset连缀
```
List<User> users = DataSupport.select("username, password").where("age > ?", "18").order("age desc").limit(5).offset(5).find(User.class);
```
### 执行原生语句
#### 原生查询
```
try {
    SqlPal.begin();
    try {
        Statement stmt = DataSupport.executeQuery("select * from user");
        ResultSet rs = stmt.getResultSet();
        while (rs.next()) {
            System.out.print(rs.getString("username") + " ");
            System.out.print(rs.getString("password") + " ");
            System.out.println(rs.getInt("age"));
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        SqlPal.end();
    }

} catch (SQLException e) {
    e.printStackTrace();
}
```
#### 原生更新
```
try {
    SqlPal.begin();
    try {
        int row = DataSupport.executeUpdate("delete from user where age > ?", "30");
        System.out.println(row);
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        SqlPal.end();
    }

} catch (SQLException e) {
    e.printStackTrace();
}
```

### 聚合函数
所有的聚合函数都是支持连缀的
#### 统计user表的行数
```
int count = DataSupport.count(User.class);
```
#### 统计user表中age列的和
```
int sum = DataSupport.sum(User.class, "age", int.class);
```
因为age是整型，所以传入int.class表示返回整型数据，支持的数据类型有short.class, int.class, long.class, float.class, double.class
#### 统计user表中age列的平均值
```
double average = DataSupport.average(User.class, "age");
```
#### 统计user表中age列的最大值
```
int max = DataSupport.max(User.class, "age", int.class);
```
#### 统计user表中age列的最小值
```
int min = DataSupport.min(User.class, "age", int.class);
``` 

### 事务操作
```
try {
    SqlPal.begin();

    try {
        // 关闭自动提交
        SqlPal.setAutoCommit(false);

        // CRUD操作

        // 提交事务
        SqlPal.commit();
    } catch (SQLException e) {
        // 回滚事务
        SqlPal.rollback();
        e.printStackTrace();
    } finally {
        SqlPal.end();
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```
把CRUD操作放在SqlPal.begin()和SqlPal.end()之间，因为begin方法会请求连接，而end方法会释放连接。
这样的话就让其中的CRUD操作使用同一个Connection，省去的请求连接的时间，
所以如果要同时执行多个CRUD操作的话建议把它们放在SqlPal.begin()和SqlPal.end()之间

## License
SqlPal is licensed under the Apache License, Version 2.0 