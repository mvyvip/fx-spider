package com.hs.reptilian.repository;

import com.hs.reptilian.model.OrderAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAccountRepository extends JpaRepository<OrderAccount, Integer> {
}
