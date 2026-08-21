package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.CustomerSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerSubscriptionsRepository extends JpaRepository<CustomerSubscriptions, Long> {

   List<CustomerSubscriptions> findByCustomerId(Long customerId);
}
