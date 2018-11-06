package com;

import com.eclipsesource.v8.V8;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by lt on 2018/11/6 0006.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        //设置代理
//        HttpHost proxy = new HttpHost("118.114.77.47", 8080, "http");
        RequestConfig config = RequestConfig.custom()
//            .setProxy(proxy)
            .build();
        HttpGet get=new HttpGet("https://mall.phicomm.com/passport-login.html");
        System.out.println(config);
        //模拟浏览器
        get.setConfig(config);
        get.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Host", "mall.phicomm.com");
        get.setHeader("referer", "http://www.cnvd.org.cn/");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        get.setHeader("Upgrade-Insecure-Requests", "1");

        CloseableHttpResponse response = client.execute(get);
        //拿到第一次请求返回的JS
        if(response.getStatusLine().getStatusCode()==521){
            HttpEntity entity = response.getEntity();
            String html=EntityUtils.toString(entity,"utf-8");
            System.out.println(html);
            //处理从服务器返回的JS，并执行
            String js=html.trim().replace("<script>", "").replace("</script>", "").replace("eval(y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]}))","y.replace(/\\b\\w+\\b/g, function(y){return x[f(y,z)-1]})");
            V8 runtime = V8.createV8Runtime();
            String result=runtime.executeStringScript(js);
            System.out.println(result);
            //第二次处理JS并执行  var cd,dc  var l=function(){
            result=result.substring(result.indexOf("var cd"),result.indexOf("dc+=cd;")+7);
            //result="var l=function(){ "+result+"return dc;}";
            System.out.println(result);

            result = result.replaceAll("document*.*toLowerCase\\(\\)", "'x'");
            String __jsl_clearance=runtime.executeStringScript(result);

            System.err.println(">>>>>" + __jsl_clearance);
            runtime.release();
            org.apache.http.Header[] Cookies=response.getHeaders("Set-Cookie");
            System.out.println(Cookies[0].getValue().split(";")[0]);
            get.setHeader("Cookie",Cookies[0].getValue().split(";")[0]+";"+__jsl_clearance);
            //get.setHeader("Cookie",__jsl_clearance);

        }
        response=client.execute(get);
        //拿到最终想要的页面
        HttpEntity entity = response.getEntity();
        String res= EntityUtils.toString(entity,"utf-8");
        System.err.println(res);

        //return res;

    }

}
