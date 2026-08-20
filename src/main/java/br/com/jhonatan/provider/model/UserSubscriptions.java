package br.com.jhonatan.provider.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users_subscriptions")
public class UserSubscriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, foreignKey = @ForeignKey(name = "fk_provider_users_subscriptions_subscription"))
    private Subscriptions subscriptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_provider_users_subscriptions_user"))
    private Users user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    // '1' - active, '0' - inactive, '2' - block (see the original column COMMENT in the DDL)
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    // user's email at the time the subscription was activated
    @Column(name = "email")
    private String email;

    // user's phone at the time the subscription was activated
    @Column(name = "phone", length = 30)
    private String phone;
}
