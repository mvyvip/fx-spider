package com;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

/**
 * Created by lt on 2018/11/6 0006.
 */
public class Jstest {

    public static void main(String[] args) throws Exception {

        Response response = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
            .header("Host", "mall.phicomm.com")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Referer", "https://mall.phicomm.com/passport-login.html")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .execute();

        Map<String, String> cookies = response.cookies();
        String body = response.body();
        System.out.println(getck(body));
        cookies.put("__jsl_clearance", getck(body).split("=")[1]);
        System.out.println(cookies);
//        Thread.sleep(1000000);

            Connection.Response execute = Jsoup.connect("https://mall.phicomm.com/passport-login.html")
                    .timeout(100000)
                    .header("Host", "mall.phicomm.com")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Referer", "https://mall.phicomm.com/passport-login.html")
                    .header("Connection", "keep-alive")
                    .header("Cookie", "__jsluid=645f1b172126b60b1d605448f6deb527; _VMC_UID=f00ba4e0989a9f9c56f03219e245bd9d; Hm_lvt_c8bb97be004001570e447aa3e00ff0ad=1541508503; _SID=05c92aaf38794dcfed5658bd914e10ce; UNAME=18585816873; MEMBER_IDENT=6460242; MEMBER_LEVEL_ID=1; CACHE_VARY=6e7992825c47521a61bd95efc8698abd-0f063e018c840f8a56946ecec41a6c18; c_peisong=1; c_zhifu=alipay; c_dizhi=769474; __jsl_clearance=1541511406.119|0|kV04cM9Whs20gJD87%2BSPOQzm%2F3c%3D; Hm_lpvt_c8bb97be004001570e447aa3e00ff0ad=1541511402")
//                    .cookies(cookies)
                    .header("Upgrade-Insecure-Requests", "1")
                    .execute();
            System.out.println(execute.cookies());
            System.out.println(execute.body());

        String s="<script>var x=\"28@onreadystatechange@JgSe0upZ@@@@@DOMContentLoaded@@JUpq@addEventListener@@challenge@@@@split@chars@@false@@String@36@@while@615@substr@@27@@createElement@pathname@reverse@18@join@7@toLowerCase@g@4@1@try@@attachEvent@w@https@eval@RegExp@firstChild@@@x@D@function@1500@d@@T@fromCharCode@@@toString@06@@Expires@0xFF@@@location@div@@replace@@1541471248@else@GMT@__jsl_clearance@@@href@@@@document@if@Tue@parseInt@Q@new@@f@@@@return@PBEgCz@innerHTML@2@0@captcha@window@length@Array@var@for@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@@03@a@@@match@search@@charCodeAt@cookie@charAt@Path@@setTimeout@@@0xEDB88320@Nov@@e@catch@8\".replace(/@*$/,\"\").split(\"@\"),y=\"1J 25=R(){24('1a.1l=1a.w+1a.1T.1d(/[\\\\?|&]1F-d/,\\\\'\\\\')',S);1p.20='1i=1f.q|1E|'+(R(){1A [[![]+[]+[[]][1E]][1E].21(-~[(-~~~''<<-~~~'')]),'1t',[![]+[]+[[]][1E]][1E].21(-~[(-~~~''<<-~~~'')]),'V',[[][[]]+[]][1E].21(D),'a',[-~[A]]+[{}+[]+[]][1E].21(-~[]-~-~![]),'P',(((1D^-~![]))/(+[])+[[]][1E]).21((-~{}|-~-~![])-~((-~-~![]^-~![])))+[(+[])]+[-~(-~((-~-~![]^-~![]))-~((-~-~![]^-~![])))]+[D],'1B',((-~~~''<<-~~~'')+[[]][1E])+[-~~~''+[-~-~![]]*(((-~~~''<<-~~~'')^-~![]))]+(!~~{}+[]+[[]][1E]).21(-~![]-~![])+[{}+[]+[]][1E].21(-~[]-~-~![]),'I%',[(1D^-~![])],'Q'].z('')})()+';16=1r, 14-28-y 1O:t:1 1h;22=/;'};1q((R(){F{1A !!1G.b;}2b(2a){1A k;}})()){1p.b('8',25,k)}1g{1p.H('2',25)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";
//    s = "<script>var x=\"f@chars@captcha@split@function@try@36@@fromCharCode@@String@onreadystatechange@@53@0@@@7@@@href@@d@8@for@08@createElement@match@06@document@@attachEvent@@join@pathname@@if@@Path@a@@setTimeout@@length@cookie@while@@reverse@false@vVJh@firstChild@replace@https@@18@parseInt@RegExp@ZHx@@@@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@@Ugm@1541488553@substr@@challenge@@@div@GMT@addEventListener@@Nov@@catch@wE@charCodeAt@innerHTML@@new@15@eval@@toLowerCase@@@JgSe0upZ@@@var@@@return@charAt@__jsl_clearance@search@@window@@Tue@location@else@Array@toString@@D@0xFF@@g@e@Expires@@DOMContentLoaded@@@1500@@0xEDB88320@4@797@EVY@@@2@@1\".replace(/@*$/,\"\").split(\"@\"),y=\"1J 10=5(){G('29.l=29.z+29.24.15(/[\\\\?|&]3-1l/,\\\\'\\\\')',2o);u.J='23=1i.2s|f|'+(5(){21 ['1v',[[][[]]+[]+[[]][f]][f].22(-~~~'')+[{}+[]+[]][f].22(-~-~![]+(-~![]+[(-~~~''<<-~~~'')])/[(-~~~''<<-~~~'')]),'13',[(2w^-~![])],'2t',[{}+[]+[]][f].22(-~(-~((-~-~![]^-~![]))-~((-~-~![]^-~![])))),'1b',({}+[]).22(-~[])+({}+[]).22(-~[])+[!-{}+[]+[[]][f]][f].22((+!{}))+[+[(+!{}), (+!{})]+[]+[[]][f]][f].22(~~'')+[{}+[]+[]][f].22(-~-~![]+(-~![]+[(-~~~''<<-~~~'')])/[(-~~~''<<-~~~'')])+[-~[i]],'1h',[2r]+[2r],'%',[(2w^-~![])],'2e'].y('')})()+';2j=28, t-1s-18 q:1A:e 1p;D=/;'};B((5(){6{21 !!26.1q;}1u(2i){21 12;}})()){u.1q('2l',10,12)}2a{u.w('c',10)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script> ";
        s = "<script>var x=\"function@d@try@JgSe0upZ@@innerHTML@rOm9XFMtA3QKV7nYsPGT4lifyWwkq5vcjH2IdxUoCbhERLaz81DNB6@captcha@@Szm@2@attachEvent@a@@18@challenge@36@createElement@@@reverse@@D@@GMT@match@@@document@@Array@@eval@405@8@firstChild@@search@@@4@@e@https@@for@else@setTimeout@@@href@@window@p@Path@Tue@1500@replace@div@cookie@Fk@@3@chars@@RegExp@@06@location@g@headless@m@23@10@substr@toString@var@split@@@while@@catch@String@@0@@fromCharCode@join@@@Nov@1541496200@@@@0xEDB88320@charAt@@@charCodeAt@pathname@@length@parseInt@toLowerCase@new@@__jsl_clearance@onreadystatechange@f@X@@1@@false@return@M@0xFF@DOMContentLoaded@EMwEJS@Expires@@@addEventListener@if@@20\".replace(/@*$/,\"\").split(\"@\"),y=\"4d 41=1(){30('45.33=45.66+45.26.3a(/[\\\\?|&]8-10/,\\\\'\\\\')',39);1d.3c='6d=5d.22|56|'+(1(){75 ['76',[-~-~{}]+[{}+[]+[]][56].62((+!'')),'a',[{}+[[]][56]][56].62([-~[]]+[-~-~{}])+[-~-~{}]+(~~!{}/~~!{}+[[]][56]).62(~~'')+(![]+[[]][56]).62(b),'48',[{}+[]][56].62(-~{}+(-~{}<<(-~!![][[]]+[(-~{}<<-~{})]>>(-~{}<<-~{})))),'36',(35.47+[]+[[]][56]).62(-~{}+(+!'')+29),'%',[-~-~{}],'3d',((-~{}<<-~-~{})+(-~{}<<-~-~{})+[]+[[]][56])+[~~{}],'79',[{}+[[]][56]][56].62(3f),'70',[(-~~~{}-~~~{})*[-~~~{}-~~~{}]],'%',(((-~{}<<-~{})^(+!''))+[]+[]),'17'].59('')})()+';7a=38, 44-5c-f 4a:49:80 19;37=/;'};7e((1(){3{75 !!35.7d;}53(2b){75 74;}})()){1d.7d('78',41,74)}2f{1d.c('6e',41)}\",f=function(x,y){var a=0,b=0,c=0;x=x.split(\"\");y=y||99;while((a=x.shift())&&(b=a.charCodeAt(0)-77.5))c=(Math.abs(b)<13?(b+48.5):parseInt(a,36))+y*c;return c},z=f(y.match(/\\w/g).sort(function(x,y){return f(x)-f(y)}).pop());while(z++)try{eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]||(\"_\"+y)}));break}catch(_){}</script>";
        String resHtml = "function getClearance(){" + s+"};";
        resHtml = resHtml.replace("</script>", "");
        resHtml = resHtml.replace("eval", "return");
        resHtml = resHtml.replace("<script>", "");
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.eval(resHtml);
        Invocable invocable = (Invocable) engine;
        String resJs = (String) invocable.invokeFunction("getClearance");
        //一级解密结果
        System.err.println(resJs);

        String overJs="function getClearance2(){ var a" + resJs.split("document.cookie")[1].split("Path=/;'")[0]+"Path=/;';return a;};";
        overJs=overJs.replace("window.headless", "'undefined'");
        System.out.println(overJs);
        engine.eval(overJs);
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");
        System.out.println(over);
    }

    public static String getck(String s) throws Exception {
        StringBuilder sb = new StringBuilder()
                .append("function getClearance(){")
                .append(s.split("</script>")[0].replace("<script>", "").replaceAll("try\\{eval", "try{return"))
                .append("};");
        String resHtml = sb.toString().replace("</script>", "").replace("eval", "return").replace("<script>", "");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        resHtml = new String(resHtml);
        engine.eval(resHtml);
        Invocable invocable = (Invocable) engine;
        //一级解密结果
        String resJs = (String) invocable.invokeFunction("getClearance");
        String overJs="function getClearance2(){ var a" + resJs.split("document.cookie")[1].split("Path=/;'")[0]+"Path=/;';return a;};";
        overJs=overJs.replace("window.headless", "'undefined'");
        engine.eval(overJs);
        Invocable invocable2 = (Invocable) engine;
        String over = (String) invocable2.invokeFunction("getClearance2");
        return over;
    }

}
