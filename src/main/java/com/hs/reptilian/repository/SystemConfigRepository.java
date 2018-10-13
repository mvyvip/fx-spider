package com.hs.reptilian.repository;

import com.hs.reptilian.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Integer> {

    SystemConfig findByKey (String key);

}
