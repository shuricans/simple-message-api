package com.example.persist.specification;

import com.example.persist.model.Message;
import com.example.persist.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class MessageSpecification {

    public static Specification<Message> usernameLike(String username) {
        return (root, query, criteriaBuilder) -> {
            Join<Message, User> user = root.join("user");
            return criteriaBuilder.equal(user.get("username"), username);
        };
    }
}
