package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

    List<UserSubscriptions> findByUserId(Long userId);
}
