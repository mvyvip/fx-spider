package com.hs.reptilian.repository;

import com.hs.reptilian.model.OrderAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderAccountRepository extends JpaRepository<OrderAccount, Integer> {

    List<OrderAccount> findByStatus(String status);

}
