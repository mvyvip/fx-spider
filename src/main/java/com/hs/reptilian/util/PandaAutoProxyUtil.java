package com.hs.reptilian.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@SuppressWarnings("all")
public class PandaAutoProxyUtil {

    private volatile AtomicInteger index = new AtomicInteger(0);


    public static final int port = 8089;//这里以正式服务器端口地址为准
    public static final String orderNo = "DT20181104035950cfr1ts57";
    public static final String secret = "DT20181104035950cfr1ts57";

    private static String url = "http://www.xiongmaodaili.com/xiongmao-web/api/glip?secret=09a87d5f626d223dbfa709260bd79a95&orderNo=GL20180927201535rll7z0xZ&count=" + 10 + "&isTxt=0&proxyType=1";

    private static List<ProxyEntity> ips = new ArrayList<>();



    //change 参数: false-换ip ，true-不换ip
    public static String authHeader(){
        final int port = 8089;//这里以正式服务器端口地址为准
        final String ip = "dynamic.xiongmaodaili.com";//这里以正式服务器ip地址为准

        //以下订单号，secret参数 须自行改动；最后一个参数: true-换ip ,false-不换ip

        int timestamp = (int) (new Date().getTime()/1000);
        //拼装签名字符串
        String planText = String.format("orderno=%s,secret=%s,timestamp=%d", "DT20181104035950cfr1ts57", "09a87d5f626d223dbfa709260bd79a95", timestamp);

        //计算签名
        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(planText).toUpperCase();

        //拼装请求头Proxy-Authorization的值;change 参数: false-换ip ,true-不换ip
        String authHeader = String.format("sign=%s&orderno=%s&timestamp=%d&change=%s", sign, orderNo, timestamp, "true");
        return authHeader;
    }
    public static final String ip = "dynamic.xiongmaodaili.com";//这里以正式服务器ip地址为准

    public static void main(String[] args) {
//        final String url = "http://test.abuyun.com/proxy.php";
        final String url = "https://mall.phicomm.com/";

        //以下订单号，secret参数 须自行改动；最后一个参数: true-换ip ,false-不换ip
        final String authHeader = authHeader();
        System.out.println(authHeader);
        ExecutorService thread = Executors.newFixedThreadPool(10);
        for (int i=0;i<10;i++) {
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    Document doc = null ;
                    try {
                        long a = System.currentTimeMillis();
                        doc = Jsoup.connect(url)
                                .proxy(PandaAutoProxyUtil.ip, PandaAutoProxyUtil.port).header("Proxy-Authorization", PandaAutoProxyUtil.authHeader()).validateTLSCertificates(false)
                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                                .timeout(10000)
                                .get();
                        System.out.println("访问结果"+doc.text()+" 访问成功所用时间："+ (System.currentTimeMillis() - a) );
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        thread.shutdown();
    }


    public synchronized Proxy getProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("dynamic.xiongmaodaili.com", 8089));
    }


}
