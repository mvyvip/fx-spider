package com.hs.reptilian.model;

import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "fx_order_account")
public class OrderAccount {

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
    private String vc;

    /** 物流号 */
    @Transient
    private String logisticsNum;

    @Transient
    private String logisticsInfo;

    private String orderNo;

    @Transient
    private String status;

    @Column(name = "create_date")
    private Date createDate;

    @Transient
    private String orderCreateDate;

}
