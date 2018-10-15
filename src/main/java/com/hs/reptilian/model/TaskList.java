package com.hs.reptilian.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@Table(name = "fx_task_list")
public class TaskList {

    @Id
    @GeneratedValue
    private Integer id;

    private String mobile;

    private String password;

    @Column(name = "vc_code_url")
    private String vcCodeUrl;

    private String addrId;

    private String vc;

    @Column(name = "cart_md5")
    private String cartMd5;

    private String cookies;

    @Column(name = "create_date")
    private Date createDate;

}
