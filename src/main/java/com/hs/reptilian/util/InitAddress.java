package com.hs.reptilian.util;

import com.hs.reptilian.constant.SystemConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

/**
 * Created by lt on 2018/10/19 0019.
 */
@SuppressWarnings("all")
public class InitAddress {

    private static volatile String rsbody = null;

    private static List<Account> accounts = new ArrayList<>();

    static {
        accounts.add(new Account("15519049461", "xy666888"));
        accounts.add(new Account("13017458404", "xy666888"));
        accounts.add(new Account("13195209974", "xy666888"));
        accounts.add(new Account("15599139010", "xy666888"));
        accounts.add(new Account("15585240389", "xy666888"));
        accounts.add(new Account("15585241072", "xy666888"));
        accounts.add(new Account("13078556681", "xy666888"));
        accounts.add(new Account("13195104691", "xy666888"));
        accounts.add(new Account("13158050482", "xy666888"));
        accounts.add(new Account("15585161713", "xy666888"));
        accounts.add(new Account("15519136140", "xy666888"));
//        accounts.add(new Account("", "xy666888"));
//        accounts.add(new Account("", "xy666888"));
//        accounts.add(new Account("", "xy666888"));
    }

    public static void main(String[] args) throws Exception {

        for (Account account : accounts) {
            Map<String, String> cookies = getCookies(account.getPhone(), account.getPassword());

            Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
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
            System.out.println(account.getPhone() + "---ok");
        }}

/*
		String s = "15873850419 \n" +
				"15719614003 \n" +
				"13404331351 \n" +
				"18419371937 \n" +
				"13282098402 \n" +
				"13438145037 \n" +
				"18200499178 \n" +
				"13550642234 \n" +
				"15246077242 \n" +
				"15904355145 \n" +
				"15943502249 \n" +
				"13332799704 \n" +
				"15730973733 \n" +
				"15884284860 \n" +
				"18482388203 \n" +
				"13059724348 \n" +
				"15769395500 \n" +
				"13694354587 \n" +
				"18402859692 \n" +
				"18281050433 \n" +
				"15883644344 \n" +
				"18280548247 \n" +
				"13944599642\n" +
				"18224021075  \n" +
				"18246840227  \n" +
				"18298403964 \n" +
				"13894514057 \n" +
				"15834564293\n" +
				"18780140771\n" +
				"18482360620\n" +
				"13438148305\n" +
				"13295705740\n" +
				"13282094326\n" +
				"15164524467\n" +
				"13946995405\n" +
				"18474674185\n" +
				"13045704286\n" +
				"13647465473\n" +
				"18843559043\n" +
				"18343241379\n" +
				"18708166911\n" +
				"18328535164";
		List<String> strings = Arrays.asList(s.split("\n"));
		for (String string : strings) {
			Map<String, String> cookies = getCookies(string.trim(), "li5201314");
			Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
					.timeout(SystemConstant.TIME_OUT)
					.ignoreContentType(true)
					//.header("X-Requested-With", "XMLHttpRequest")
					//.header("Content-Type", "application/x-www-form-urlencoded")
					//.header("Upgrade-Insecure-Requests", "1")
					.data("maddr[name]", "张雷")
					.data("maddr[mobile]", "15950690342")
					.data("maddr[area]", "mainland:江苏省/徐州市/铜山县:922")
					.data("maddr[addr]", "江苏省徐州市铜山区城区铜山新区北京北路41-8号圆通快递")
					.data("maddr[is_default]", "true")
					.execute();
			System.out.println(execute.body());
		}
*/
        private static synchronized void updateRsBody(String body) {
            if(rsbody == null) {
                rsbody = body;
                System.out.println("==============设置成功==============");
            }
        }


        private static Map<String, String> getCookies(String username, String password) throws Exception {
           try {
               Response pageResponse = Jsoup.connect("https://mall.phicomm.com/passport-login.html").method(Connection.Method.GET).timeout(SystemConstant.TIME_OUT).execute();
               Map<String, String> pageCookies = pageResponse.cookies();

               Response loginResponse = Jsoup.connect("https://mall.phicomm.com/passport-post_login.html").method(Connection.Method.POST)
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

