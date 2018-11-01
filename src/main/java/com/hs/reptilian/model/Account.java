package com.hs.reptilian.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "fx_account")
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    private String mobile;

    private String password;

    private Integer status;

    private String remark;

    /**
     * 验证码路径
     */
    @Column(name = "member_ident")
    private String memberIdent;

    private String sid;

    @Column(name = "create_date")
    private Date createDate;

    private String addrId;

}
