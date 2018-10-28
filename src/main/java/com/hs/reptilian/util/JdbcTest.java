package com.hs.reptilian.util;

import com.hs.reptilian.constant.SystemConstant;

import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

@SuppressWarnings("all")
public class JdbcTest { //定义一个类

    private static List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) throws Exception { //主方法
        try {
            String driver = "com.mysql.jdbc.Driver";              //1.定义驱动程序名为driver内容为com.mysql.jdbc.Driver
            String url = "jdbc:mysql://118.24.153.209:3306/fx"; //防止乱码；useUnicode=true表示使用Unicode字符集；characterEncoding=UTF8表示使用UTF-8来编辑的。
            String user = "root";                                   //3.定义用户名，写你想要连接到的用户。
            String pass = "admin";                                  //4.用户密码。
            String querySql = "select phone, password from fx_order_account";          //5.你想要查找的表名。
            Class.forName(driver);                              //6.注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以  Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);//7.获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成
            Statement stmt = conn.createStatement();   //8.构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
            ResultSet rs = stmt.executeQuery(querySql);//9.执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。
            while (rs.next()) {
               // accounts.add(new Account(rs.getString("phone"), rs.getString("password")));
            }

            String s = "" +
                    "13593158942----zhao1357\n" +
                    "15364960594----gh19960910\n" +
                    "13546415189----gh19960910\n" +
                    "15364930965----zhao1357\n" +
                    "18920763066----zhao1357\n" +
                    "13007824983----fc123789\n" +
                    "15519039525----fc123789\n" +
                    "15519037963----fc123789\n" +
                    "18585803970----fc123789\n" +
                    "18586982725----fc123789\n" +
                    "15519072613----fc123789\n" +
                    "18685153501----fc123789\n" +
                    "18584401319----fc123789\n" +
                    "18586984783----fc123789\n" +
                    "18586812052----fc123789";

            String remark = "592504929";

            for (String s1 : s.split("\n")) {
                accounts.add(new Account(s1.split("----")[0], s1.split("----")[1]));
            }

            for (Account account : accounts) {
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {

                           String sql = "INSERT INTO fx_order_account(phone,password,remark) VALUES(?,?,?)";
                           // 获取PrepareStatement对象
                           PreparedStatement preparedStatement = conn.prepareStatement(sql);
                           // 填充占位符
                           preparedStatement.setString(1, account.getPhone());
                           preparedStatement.setString(2, account.getPassword());
                           preparedStatement.setString(3, remark);
                           // 执行sql
                           int num = preparedStatement.executeUpdate();// 返回影响到的行数

                           System.out.println("一共影响到" + num + "行");

                          /* Map<String, String> cookies = getCookies(account.getPhone(), account.getPassword());

                           Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(org.jsoup.Connection.Method.POST).cookies(cookies)
                               .timeout(SystemConstant.TIME_OUT)
                               .ignoreContentType(true)
                               //.header("X-Requested-With", "XMLHttpRequest")
                               //.header("Content-Type", "application/x-www-form-urlencoded")
                               //.header("Upgrade-Insecure-Requests", "1")
                               .data("maddr[name]", "刘正周")
                               .data("maddr[mobile]", "13863749494")
                               .data("maddr[area]", "mainland:山东省/济宁市/微山县:1583")
                               .data("maddr[addr]", "山东省济宁市微山县苏园一村32号楼1单元888号-诗人")
                               .data("maddr[is_default]", "true")
                               .execute();
                           System.out.println(account.getPhone() + "-------ok");*/


                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               }).start();
                Thread.sleep(1000);
            }



            /*if (rs != null) {//11.关闭记录集
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {//12.关闭声明的对象
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {//13.关闭连接 （记住一定要先关闭前面的11.12.然后在关闭连接，就像关门一样，先关里面的，最后关最外面的）
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getCookies(String username, String password) throws Exception {
        try {
            Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(org.jsoup.Connection.Method.GET).timeout(SystemConstant.TIME_OUT).execute();
            Map<String, String> pageCookies = pageResponse.cookies();

            Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(org.jsoup.Connection.Method.POST)
                .cookies(pageCookies)
                .timeout(SystemConstant.TIME_OUT)
                .ignoreContentType(true)
                .header("X-Requested-With", "XMLHttpRequest")
                .data("forward", "")
                .data("uname", username)
                .data("password", password)
                .execute();


            if(loginResponse.body().contains("error")) {
                throw new RuntimeException("帐号或密码不正确");
            }

            Map<String, String> cks = new HashMap<>();
            cks.putAll(pageCookies);
            cks.putAll(loginResponse.cookies());
            return cks;
        }catch (Exception e){
            return getCookies(username, password);
        }
    }
}