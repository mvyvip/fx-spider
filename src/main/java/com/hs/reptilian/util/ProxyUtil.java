package com.hs.reptilian.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.reptilian.constant.SystemConstant;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

@Slf4j
//@Component
public class ProxyUtil {

    @Value("${key}")
    private String key;

    @Value("${count}")
    private Integer count;

    private List<ProxyEntity> proxyEntities = new ArrayList<>();

    private volatile AtomicInteger index = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        task();
    }

    public void task() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("自动同步ip启动");
                while (true) {
                    try {
                        Iterator<ProxyEntity> iterator = proxyEntities.iterator();
                        while (iterator.hasNext()) {
                            ProxyEntity next = iterator.next();
                            if (next.isExpire()) {
                                iterator.remove();
                            }
                        }
                        if(getCanUsed() < count) {
                            initIps();
                        }
                        Thread.sleep(6 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public synchronized List<ProxyEntity> initIps() {
      try {
          Connection.Response response = Jsoup.connect(SystemConstant.IP_URL + key)
                  .timeout(SystemConstant.TIME_OUT)
                  .ignoreContentType(true)
                  .header("Content-Type", "application/json; charset=UTF-8")
                  .execute();
          JSONArray datas = JSONObject.parseObject(response.body()).getJSONArray("data");
          for (Object data : datas) {
              JSONObject jsonObject = JSONObject.parseObject(data.toString());
              proxyEntities.add(new ProxyEntity(jsonObject.getString("ip"), jsonObject.getInteger("port"), jsonObject.getDate("expire_time")));
          }
          log.info("初始化完毕" + new Date().toLocaleString() + "---size: " + proxyEntities.size());
      } catch (Exception e) {
          e.printStackTrace();
      }
        return null;
    }

    public synchronized Proxy getProxy() {
//        if(getCanUsed() < SystemConstant.IP_COUNT) {
//            initIps();
//        }
//        Iterator<ProxyEntity> iterator = proxyEntities.iterator();
//        while (iterator.hasNext()) {
//            ProxyEntity next = iterator.next();
//            if (next.isExpire()) {
//                iterator.remove();
//            }
//        }

   /*     Collections.shuffle(proxyEntities);
        ProxyEntity proxyEntity = proxyEntities.get(0);*/

        if(index.get() >= proxyEntities.size()) {
            index.set(0);
        }
        ProxyEntity proxyEntity = proxyEntities.get(index.get());
        index.incrementAndGet();

//        log.info("proxy > " + proxyEntity.getIp() + ":"+ proxyEntity.getPort());
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyEntity.getIp(), proxyEntity.getPort()));
    }

    public synchronized Integer getCanUsed() {
        Integer count = 0;
        if(CollectionUtils.isNotEmpty(proxyEntities)) {
            for (ProxyEntity proxyEntity : proxyEntities) {
                if(!proxyEntity.isExpire()) {
                    count++;
                }
            }
        }
        return count;
    }

}
