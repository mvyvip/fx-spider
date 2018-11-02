package com.hs.reptilian;


import com.hs.reptilian.constant.SystemConstant;
import com.hs.reptilian.util.Account;
import com.hs.reptilian.util.UserAgentUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("all")
public class TestCacheCk {

	private static volatile String rsbody = null;

	private static List<com.hs.reptilian.model.Account> accounts = new ArrayList<>();

	public static void main(String[] args) throws Exception {

		String driver = "com.mysql.jdbc.Driver";              //1.定义驱动程序名为driver内容为com.mysql.jdbc.Driver
		String url = "jdbc:mysql://118.24.153.209:3306/fx"; //防止乱码；useUnicode=true表示使用Unicode字符集；characterEncoding=UTF8表示使用UTF-8来编辑的。
		String user = "root";                                   //3.定义用户名，写你想要连接到的用户。
		String pass = "admin";                                  //4.用户密码。
		String querySql = "select * from fx_account ";          //5.你想要查找的表名。
		Class.forName(driver);                              //6.注册驱动程序，用java.lang包下面的class类里面的Class.froName();方法 此处的driver就是1里面定义的driver，也可以  Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(url, user, pass);//7.获取数据库连接,使用java.sql里面的DriverManager的getConnectin(String url , String username ,String password )来完成
		Statement stmt = conn.createStatement();   //8.构造一个statement对象来执行sql语句：主要有Statement，PreparedStatement，CallableStatement三种实例来实现
		ResultSet rs = stmt.executeQuery(querySql);//9.执行sql并返还结束 ；ResultSet executeQuery(String sqlString)：用于返还一个结果集（ResultSet）对象。
		while (rs.next()) {
			com.hs.reptilian.model.Account account = new com.hs.reptilian.model.Account();
			account.setStatus(1);
			account.setSid(rs.getString("sid"));
			account.setRemark(rs.getString("remark"));
			account.setPassword(rs.getString("password"));
			account.setMobile(rs.getString("mobile"));
			account.setMemberIdent(rs.getString("member_ident"));
			account.setAddrId(rs.getString("addr_id"));
			accounts.add(account);
		}

        if (rs != null) {//11.关闭记录集
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
        }

		System.out.println(accounts.size());
		accounts.stream().forEach(account -> {
			System.out.println(account);
			try {
                Thread.sleep(1000);
            } catch (Exception e) {
			    e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, String> cookies = new HashMap<>();
                    String[] split = account.getSid().replace("{", "").replace("}", "").split(",");
                    for (String s : split) {
                        cookies.put(s.split("=")[0], s.split("=")[1]);
                    }
                    while (true) {
                        try {
                            Element body = Jsoup.connect("https://mall.phicomm.com/index.html")
                                .timeout(SystemConstant.TIME_OUT)
                                .cookies(cookies)
                                .userAgent(UserAgentUtil.get())
                                .execute().parse().body();
                            System.out.println(body.text());
                            Thread.sleep(3 * 60 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();


		});




	}

	private static synchronized void updateRsBody(String body) {
		if(rsbody == null) {
			rsbody = body;
			System.out.println("==============设置成功==============");
		}
	}

	private static String initBody(Map<String, String> cookies) throws Exception {
		while(rsbody == null) {
			Thread.sleep(SystemConstant.THREAD_WAIT_TIME);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String body = Jsoup.connect(SystemConstant.W3_URL).method(Method.GET)
							.timeout(SystemConstant.TIME_OUT)
							.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6776.400 QQBrowser/10.3.2577.400")
							.cookies(cookies).followRedirects(true).execute().body();
						if(body.contains("库存不足,当前最多可售数量")) {
							System.err.println("库存不足 - " + new Date().toLocaleString());
						} else if(body.contains("返回商品详情") || body.contains("cart_md5")) {
				        	updateRsBody(body);
				        }
					} catch (Exception e) {
						System.out.println(e.getMessage());
						//						e.printStackTrace();
					}
				}
			}).start();
		}
		System.out.println("成功");
		return rsbody;
	}

	private static String getAddrId(Document document) {
		try {
            String addr_id = document.select("input[name=addr_id]").get(0).val();
            if(addr_id != null && addr_id.length() > 0) {
//                System.err.println("addr_id获取成功："  + addr_id);
            	return addr_id;
            }
        } catch (Exception e) {
            for (Element element : document.getElementsByTag("input")) {
                if(element.attr("name").equals("addr_id")) {
                	String addr_id = element.attr("value");
//                    System.err.println("addr_id获取成功："  + addr_id);
                    return addr_id;
                }
            }
        }
		throw new RuntimeException("addr_id获取失败");
	}

	private static String getCartMd5(Document document) {
		for (Element element : document.getElementsByTag("input")) {
            if(element.attr("name").equals("cart_md5")) {
                System.err.println("cart_md5获取成功："  + element.attr("value"));
                return element.attr("value");
            }
        }
		throw new RuntimeException("cart_md5获取失败");
	}

	private static boolean doCheck(String body) {
        if(body.contains("购物车为空")) {
        	System.out.println("购物车为空 - " + new Date().toLocaleString());
        	return true;
        }
        if(body.indexOf("库存不足") != -1) {
        	System.out.println("库存不足 - " + new Date().toLocaleString());
        	return true;
        }
        return false;
	}

    private static Map<String, String> getCookies(String username, String password) throws Exception {
        try{
			Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Method.GET)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Language", "zh-CN,zh;q=0.9")
				.header("Cache-Control", "max-age=0")
				.header("Connection", "keep-alive")
				.header("Host", "mall.phicomm.com")
				.header("Upgrade-Insecure-Requests", "1")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
				.ignoreContentType(true)
				.timeout(SystemConstant.TIME_OUT).execute();
			Map<String, String> pageCookies = pageResponse.cookies();

			Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Method.POST)
				.cookies(pageCookies)
				.timeout(SystemConstant.TIME_OUT)
				.ignoreContentType(true)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Language", "zh-CN,zh;q=0.9")
				.header("Cache-Control", "max-age=0")
				.header("Connection", "keep-alive")
				.header("Host", "mall.phicomm.com")
				.header("Upgrade-Insecure-Requests", "1")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
				.header("X-Requested-With", "XMLHttpRequest")
				.data("forward", "")
				.data("uname", username)
				.data("password", password)
				.execute();


			if(loginResponse.body().contains("error")) {
				return getCookies(username, password);
			}

			Map<String, String> cks = new HashMap<>();
			cks.putAll(pageCookies);
			cks.putAll(loginResponse.cookies());
			return cks;
		} catch (Exception e){
			System.out.println(e.getMessage());
			return getCookies(username, password);
		}
    }

	public static String getRandomIp() {
		// ip范围
		int[][] range = {{607649792, 608174079}, // 36.56.0.0-36.63.255.255
			{1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
			{1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
			{2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
			{2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
			{-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
			{-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
			{-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
			{-770113536, -768606209}, // 210.25.0.0-210.47.255.255
			{-569376768, -564133889}, // 222.16.0.0-222.95.255.255
		};

		Random rdint = new Random();
		int index = rdint.nextInt(10);
		String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
		return ip;
	}

	/*
     * 将十进制转换成IP地址
     */
	public static String num2ip(int ip) {
		int[] b = new int[4];
		String x = "";
		b[0] = (int) ((ip >> 24) & 0xff);
		b[1] = (int) ((ip >> 16) & 0xff);
		b[2] = (int) ((ip >> 8) & 0xff);
		b[3] = (int) (ip & 0xff);
		x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);

		return x;
	}

}
