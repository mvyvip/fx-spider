package com.hs.reptilian.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "fx_system_config")
public class SystemConfig {

    @Id
    @GeneratedValue
    private Integer id;

    private String key;

    private String value;

    private String remark;

}
