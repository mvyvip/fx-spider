package com.hs.reptilian.util;

import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

@Data
public class ProxyEntity {

    public ProxyEntity(String ip, Integer port, Date expireTime) {
        this.ip = ip;
        this.port = port;
        this.expireTime = expireTime;
    }

    private String ip;

    private Integer port;

    private Date expireTime;

    public boolean isExpire() {
        return System.currentTimeMillis() > DateUtils.addSeconds(expireTime, -1).getTime();
    }

}
