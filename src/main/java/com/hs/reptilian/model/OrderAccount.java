package com.hs.reptilian.model;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "fx_order_account")
public class OrderAccount {

    public OrderAccount() {}

    public OrderAccount(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    @Id
    @GeneratedValue
    private Integer id;

    private String phone;

    private String password;

    private String username;

    @Transient
    private String goodsName;

    /**
     * 身份证
     */
    private String vcCard;

    @Transient
    private String address;

    @Transient
    private String defaultAddress;

    @Transient
    private String vc;

    @Transient
    private String vc2;

    @Transient
    private String renzheng;

    /** 物流号 */
    @Transient
    private String logisticsNum;

    @Transient
    private String logisticsInfo;

    private String orderNo;

    private String status;

    @Transient
    private String status2;

    @Column(name = "create_date")
    private Date createDate;

    @Transient
    private String orderCreateDate;

    private String remark;

    @Transient
    private String cookie;

    @Transient
    private String payBase64;


}
