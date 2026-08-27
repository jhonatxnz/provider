package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.CustomerSubscriptionsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSubscriptionsHistoryRepository extends JpaRepository<CustomerSubscriptionsHistory, Long> {
}
