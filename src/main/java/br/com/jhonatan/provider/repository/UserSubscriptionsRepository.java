package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

   @Query("SELECT us FROM UserSubscriptions us JOIN FETCH us.subscriptions WHERE us.user.id = :userId")
    List<UserSubscriptions> findByUserId(@Param("userId") Long userId);
}
