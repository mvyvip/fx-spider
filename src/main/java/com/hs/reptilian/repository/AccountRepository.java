package com.hs.reptilian.repository;

import com.hs.reptilian.model.Account;
import com.hs.reptilian.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
