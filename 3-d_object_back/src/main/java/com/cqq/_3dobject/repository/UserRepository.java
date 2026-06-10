package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 自动拥有了 save(), findById(), delete() 等方法

    // 你还可以自定义查询，例如根据邮箱找用户
    User findByEmail(String email);

    List<User> findByUidIn(Collection<Long> uids);
}
