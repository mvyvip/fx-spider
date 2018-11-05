package com.hs.reptilian.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@SuppressWarnings("all")
public class PandaProxyUtil {

    @Value("${count}")
    private Integer count;

    private volatile AtomicInteger index = new AtomicInteger(0);

    private static String url = "http://www.xiongmaodaili.com/xiongmao-web/api/glip?secret=09a87d5f626d223dbfa709260bd79a95&orderNo=GL20180927201535rll7z0xZ&count=" + 5 + "&isTxt=0&proxyType=1";

    private static List<ProxyEntity> ips = new ArrayList<>();

    @PostConstruct
    public synchronized void init() throws Exception {
        initIps();
        Thread.sleep(1500);
        task();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ips.clear();
                        initIps();
                        Thread.sleep(4 * 60 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void task() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean falg = true;
                while (falg) {
                    try {
                        Thread.sleep(1000);
                        if(new Date().getMinutes() == 59 && new Date().getSeconds() >= 11) {
                            falg = false;
                        }
                    } catch (Exception e) {
                        log.info("同步panda代理失败+" + e.getMessage());
                    }
                }
                log.info("开始同步代理" );
                for (int i = 0; i < count / 50; i++) {
                   try {
                       Iterator<ProxyEntity> iterator = ips.iterator();
                       while (iterator.hasNext()) {
                           ProxyEntity next = iterator.next();
                           if (next.isExpire()) {
                               iterator.remove();
                           }
                       }
                       initIps();
                       Thread.sleep(2 * 1000);
                   } catch (Exception e){
                        e.printStackTrace();
                   }
                }


            }
        }).start();
    }

    private void initIps() {
        try {
            String body = Jsoup.connect(url)
                    .timeout(10000)
                    .execute().body();
            JSONArray obj = JSONObject.parseObject(body).getJSONArray("obj");
            System.err.println("obj>>> " + obj + "  body: " + body);
            for (Object o : obj) {
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                ProxyEntity proxyEntity = new ProxyEntity(jsonObject.getString("ip"), jsonObject.getInteger("port"), DateUtils.addMinutes(new Date(),5));
                ips.add(proxyEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Proxy getProxy() {

        if(index.get() >= ips.size()) {
            index.set(0);
        }
        ProxyEntity proxyEntity = ips.get(index.get());
        index.incrementAndGet();

//        log.info("proxy > " + proxyEntity.getIp() + ":"+ proxyEntity.getPort());
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntity.getIp(), proxyEntity.getPort()));
    }

    public synchronized Integer getCanUsed() {
        Integer count = 0;
        if(CollectionUtils.isNotEmpty(ips)) {
            for (ProxyEntity proxyEntity : ips) {
                if(!proxyEntity.isExpire()) {
                    count++;
                }
            }
        }
        return count;
    }

}
