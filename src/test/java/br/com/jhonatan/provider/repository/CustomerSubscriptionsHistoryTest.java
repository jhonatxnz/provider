package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.CustomerSubscriptionsHistory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for CustomerSubscriptionsHistoryRepository")
class CustomerSubscriptionsHistoryTest {

    @Autowired
    private CustomerSubscriptionsHistoryRepository customerSubscriptionsHistoryRepository;

    private CustomerSubscriptionsHistory historyToBeSaved() {
        return CustomerSubscriptionsHistory.builder()
                .subscriptionId(1L)
                .userId(1L)
                .action("ACTIVATE")
                .date(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Save creates customer subscription history when successful")
    void save_CreatesCustomerSubscriptionHistory_WhenSuccessful() {
        CustomerSubscriptionsHistory historyToBeSaved = historyToBeSaved();

        CustomerSubscriptionsHistory savedHistory = customerSubscriptionsHistoryRepository.save(historyToBeSaved);

        Assertions.assertThat(savedHistory).isNotNull();

        Assertions.assertThat(savedHistory.getId()).isNotNull();

        Assertions.assertThat(savedHistory.getAction()).isEqualTo(historyToBeSaved.getAction());

        Assertions.assertThat(savedHistory.getSubscriptionId()).isEqualTo(historyToBeSaved.getSubscriptionId());

        Assertions.assertThat(savedHistory.getUserId()).isEqualTo(historyToBeSaved.getUserId());
    }

    @Test
    @DisplayName("Save updates customer subscription history when successful")
    void save_UpdatesCustomerSubscriptionHistory_WhenSuccessful() {
        CustomerSubscriptionsHistory savedHistory = customerSubscriptionsHistoryRepository.save(historyToBeSaved());

        savedHistory.setAction("CANCEL");

        CustomerSubscriptionsHistory updatedHistory = customerSubscriptionsHistoryRepository.save(savedHistory);

        Assertions.assertThat(updatedHistory).isNotNull();

        Assertions.assertThat(updatedHistory.getId()).isEqualTo(savedHistory.getId());

        Assertions.assertThat(updatedHistory.getAction()).isEqualTo("CANCEL");
    }

    @Test
    @DisplayName("Delete removes customer subscription history when successful")
    void delete_RemovesCustomerSubscriptionHistory_WhenSuccessful() {
        CustomerSubscriptionsHistory savedHistory = customerSubscriptionsHistoryRepository.save(historyToBeSaved());

        customerSubscriptionsHistoryRepository.delete(savedHistory);

        Optional<CustomerSubscriptionsHistory> historyOptional =
                customerSubscriptionsHistoryRepository.findById(savedHistory.getId());

        Assertions.assertThat(historyOptional).isEmpty();
    }
}