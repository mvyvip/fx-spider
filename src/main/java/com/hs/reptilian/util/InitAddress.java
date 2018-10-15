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
//                + "18925448237----hao112233----张博----410183200205109552\n"
//                + "18925470971----hao112233----柴铭涛----142703199908073738\n"
//                + "18944795159----hao112233----杨帆----130521200105240524\n"
//                + "18929268774----hao112233----王乾省----532627200205280523\n"
//                + "18925486771----hao112233----杨会----420325200111193326\n"
//                + "18925747061----hao112233----徐艳----220122198805082244\n"
//                + "18944709165----hao112233----吴妮妮----450881199606125040\n"
//                + "18929267495----hao112233----陈蝶----440825200110261463\n"
//                + "18922541662----hao112233----杨生政----320925200001135810\n"
//                + "18925573942----hao112233----周雯君----511304200002280421\n"
//                + "18924340412----hao112233----任荣青----130533199709045543\n"
//                + "18925804437----hao112233----李政----130681200110282210\n"
//                + "18926895450----hao112233----罗延兴----372922199905107878\n"
//                + "18925404485----hao112233----林丽伟----452626199601063860\n"
//                + "18926846649----hao112233----孙学美----110106198506190944\n"
//                + "18929204697----hao112233----陈静----420682199311100057\n"
//                + "18929201049----hao112233----蒲桂北----513002199909273550\n"
//                + "18925745339----hao112233----葛彦佟----32072219990121392X\n"
//                + "18925514159----hao112233----张亚涛----411221199912183518\n"
//                + "18929420845----hao112233----武月月----411422199811080621\n"
//                + "18929410519----hao112233----吕心语----41010519920427018X\n"
//                + "18924332470----hao112233----王会----510525200001238100\n"
//                + "18938216174----hao112233----张雨晴----370521200104252042\n"
//                + "18929413830----hao112233----刘雅瑞----412724200207067506\n"
//                + "18925709420----hao112233----华春----411523199202030428\n"
//                + "18928224721----hao112233----郭魏岩----410122200007210016\n"
//                + "18929279494----hao112233----邵苗苗----142703200110160627\n"
//                + "18938254853----hao112233----崔雅雅----410322199908201825\n"
//                + "18922545315----hao112233----陈银梅----330483200008160929\n"
//                + "18929201405----hao112233----孟悦----320322200106274428\n"
//                + "18926804847----hao112233----丽娜----15252420001012092X\n"
//                + "18902694971----hao112233----岳宏霞----130726198810233365\n"
//                + "18938195467----hao112233----隋冉冉----211381200010191826\n"
//                + "18938236940----hao112233----郑荣蝶----522526200101191848\n"
//                + "18929214312----hao112233----董涵----410724200205011515\n"
//                + "18925426462----hao112233----李文静----210522200012194422\n"
//                + "18926814619----hao112233----古馨茹----142333200208081421\n"
//                + "18929108814----hao112233----李金斗----410221199911061315\n"
//                + "18929410196----hao112233----潘国浩----410381200007081016\n"
//                + "18929246191----hao112233----金万凤----150430200202264127\n"
//                + "18925734489----hao112233----张梦楠----411424199610102882\n"
//                + "18924344852----hao112233----张立刚----372901200011064358\n"
//                + "18925405932----hao112233----白雪峰----612727200108205711\n"
//                + "18128595509----hao112233----计其财----412723199806139012\n"
//                + "18128590921----hao112233----赵晓----612324199808117222\n"
//                + "18163450712----hao112233----陈景瑞----410105199706270104\n"
//                + "18128595736----hao112233----赵晓----612324199808117222\n"
//                + "18163451713----hao112233----黄彪----429006199207205798\n"
//                + "18163459603----hao112233----王巧哲----411325199804013520\n"
//                + "18128593306----hao112233----马浩----610528199909057817\n"
//                + "18198822630----hao112233----王生禄----411522199912146912\n"
//                + "17368234049----hao112233----孙晓倩----370784199402250521\n"
//                + "18126606441----hao112233----刘癸兰----52242620010711563X\n"
//                + "17368239614----hao112233----况微----50010220000906870X\n"
//                + "17329506476----hao112233----范艳侠----370827199801142825\n"
//                + "17368238914----hao112233----蔡雅丽----440825200108150561\n"
                + "18127141646----hao112233----朱珍莲----62272419730929152X\n"
                + "17329514436----hao112233----刘秀鸿----460003198910202084\n"
                + "18007578649----hao112233----张志贤----130625198602281301\n";
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
    }

}
