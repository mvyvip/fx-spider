package com.hs.reptilian.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "fx_task_list")
@Builder
public class TaskList {

    @Id
    @GeneratedValue
    private Integer id;

    private String mobile;

    private String password;


    @Column(name = "vc_code_url")
    private String vc_code_url;

    @Column(name = "addr_id")
    private String addr_id;

    private String vc;

    @Column(name = "cart_md5")
    private String cart_md5;

    private String cookies;

    @Column(name = "create_date")
    private Date createDate;

}
