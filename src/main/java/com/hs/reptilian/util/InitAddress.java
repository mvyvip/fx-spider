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
 * Created by lt on 2018/10/15 0015.
 */
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.util.*;


/**
 * 		王顺福	qq666888
 黄恒	qq666888
 郑海斌	qq666888
 熊金华	qq666888
 姚进	qq666888
 颜小米	qq666888
 王维斌	qq666888
 邓玉妹	qq666888
 胡虎	qq666888
 黄华全	qq666888
 */
@SuppressWarnings("all")
public class InitAddress {

    private static volatile String rsbody = null;

    private static List<Account> accounts = new ArrayList<>();

    static {
//            accounts.add(new Account("13027804273", "xy666888"));
//            accounts.add(new Account("13078549478", "xy666888"));
//            accounts.add(new Account("13017488417", "xy666888"));
//            accounts.add(new Account("13017488634", "xy666888"));
    }

    public static void main(String[] args) throws Exception {

        String s = ""
//                +"15585178794----xy666888----王永远----522425200107026112\n"
//            + "13195107814----xy666888----翁恒先----522132197704207328\n"
//            + "13078576283----xy666888----杨德先----522421196407094024\n"
//            + "13017482710----xy666888----黄伟----522423198410050032\n"
//            + "13007865953----xy666888----赵金益----522422197409104039\n"
//            + "13017469257----xy666888----袁忠红----522225199205168126\n"
//            + "13007805714----xy666888----班婷----52232719841030002X\n"
//            + "18585027178----xy666888----陈阳元----522422199803143414\n"
//            + "13195104346----xy666888----陈兴勇----522729197802032116\n"
//            + "15519044426----xy666888----彭武江----522502198107214819\n"
//            + "13037887563----xy666888----罗振分----522322197712051026\n"
//            + "13098511425----xy666888----候雪英----411223194502017525\n"
//            + "15585249847----xy666888----张明富----520203197103203235\n"
//            + "15519136227----xy666888----李素英----512921196805104588\n"
//            + "13007863541----xy666888----许登远----520201196410050470\n"
//            + "15585167664----xy666888----龙银----522425198704033919\n"
//            + "13078575058----xy666888----唐征仁----520222199309190036\n"
//            + "15585160375----xy666888----朱顺勇----520112198501152512\n"
//            + "13158034660----xy666888----安得江----522423197304188316\n"
//            + "13017468974----xy666888----蓝兴武----352122197106083519\n"
//            + "15599136491----xy666888----朱厢厢----522424198307282210\n"
//            + "13158014058----xy666888----胡双富----520181198210092137\n"
//            + "13195217431----xy666888----陈仕刚----520181198209034810\n"
//            + "15585163967----xy666888----魏纪苹----520111198301193620\n"
//            + "13195113004----xy666888----王守琴----522423199212072622\n"
//            + "15519021849----xy666888----欧莎----522524198707263022\n"
//            + "13027810389----xy666888----陈洪----520122199911190017\n"
//            + "13027820950----xy666888----黄利娥----421023198611057146\n"
//            + "13007840618----xy666888----葛辉----52212619740401157X\n"
//            + "13027883445----xy666888----郭礼木----510230197203265174\n"
//            + "13027829604----xy666888----赵孝福----522324198002029817\n"
//            + "13007807245----xy666888----唐静----522425199406126089\n"
//            + "13078513424----xy666888----熊英----522422198507133241\n"
//            + "13027827683----xy666888----石军----520113197408232018\n"
//            + "13007869301----xy666888----邓广梅----52242719921115324X\n"
//            + "13027818869----xy666888----田春----520113197501240813\n"
//            + "13007868465----xy666888----邓珍飞----522426198910183682\n"
//            + "13098519062----xy666888----高加贵----522426198009193634\n"
//            + "13158064854----xy666888----郑朝芬----522524197012191208\n"
//            + "15519049461----xy666888----蔡远航----522428199505184814\n"
//            + "13017458404----xy666888----张才华----520221199505051870\n"
//            + "13195209974----xy666888----赵琼----522424197209281826\n"
//            + "15599139010----xy666888----卢兴兰----520203199110095024\n"
//            + "15585240389----xy666888----杨新厂----412729196903011410\n"
//            + "15585241072----xy666888----叶华锋----332522198410105156\t\n"
            + "13078556681----xy666888----张和平----512921196504302759\t\n"
            + "13195104691----xy666888----杨艳珍----52022119850821460X\t\n"
            + "13158050482----xy666888----贺青飞----52242619701113716X\t\n"
            + "15585161713----xy666888----赵庆林----520201198511304811\t\n"
            + "15519136140----xy666888----赵菊----522422199005171824";
        String[] split = s.split("\n");
        for (String s1 : split) {
            accounts.add(new Account(s1.split("----")[0], s1.split("----")[1]));
        }

        for (Account account : accounts) {
            Map<String, String> cookies = getCookies(account.getPhone(), account.getPassword());

            Response execute = Jsoup.connect("https://mall.phicomm.com/my-receiver-save.html").method(Connection.Method.POST).cookies(cookies)
                    .timeout(SystemConstant.TIME_OUT)
                    .ignoreContentType(true)
                    //.header("X-Requested-With", "XMLHttpRequest")
                    //.header("Content-Type", "application/x-www-form-urlencoded")
                    //.header("Upgrade-Insecure-Requests", "1")
                    .data("maddr[name]", "加成")
                    .data("maddr[mobile]", "18034436664")
                    .data("maddr[area]", "mainland:河北省/秦皇岛市/昌黎县:91")
                    .data("maddr[addr]", "河北省秦皇岛市昌黎县国际城小区--诗人")
                    .data("maddr[is_default]", "true")
                    .execute();
            System.out.println(account.getPhone() + "---ok");
        }
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


    }





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
               System.out.println(username + "----" + password + "----帐号或密码不正确");
               throw new RuntimeException("帐号或密码不正确");
           }

           Map<String, String> cks = new HashMap<>();
           cks.putAll(pageCookies);
           cks.putAll(loginResponse.cookies());
           return cks;
       } catch (Exception e) {
           return getCookies(username, password);
       }
    }

}
