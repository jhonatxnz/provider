package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    Optional<Subscriptions> findByCode(String code);
}
