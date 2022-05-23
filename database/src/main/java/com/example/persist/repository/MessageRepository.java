package com.example.persist.repository;

import com.example.persist.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    @Query("select m from Message m " +
            "inner join fetch m.user u " +
            "where u.username like :username")
    List<Message> findMessagesByUsername(String username);
}
