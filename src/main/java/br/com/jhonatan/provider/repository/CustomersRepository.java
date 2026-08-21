package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers, Long> {

    Optional<Customers> findByUsername(String username);
}
