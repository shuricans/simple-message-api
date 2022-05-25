package com.example.persist.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @SequenceGenerator(
            name = "messages_message_id_seq",
            sequenceName = "messages_message_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "messages_message_id_seq",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "message_id")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return id != null ? id.equals(message.id) : message.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
