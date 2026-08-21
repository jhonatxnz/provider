package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.Subscriptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for SubscriptionsRepository")
class SubscriptionsRepositoryTest {
    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Test
    @DisplayName("Save creates subscription when successful")
    void save_PersistSubscription_WhenSuccessful() {

        Subscriptions subscriptionToBeSaved = createSubscription();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        Assertions.assertThat(savedSubscription).isNotNull();

        Assertions.assertThat(savedSubscription.getId()).isNotNull();

        Assertions.assertThat(savedSubscription.getName()).isEqualTo(subscriptionToBeSaved.getName());
    }

    @Test
    @DisplayName("Save updates subscription when successful")
    void save_UpdatesSubscription_WhenSuccessful() {

        Subscriptions subscriptionToBeSaved = createSubscription();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        savedSubscription.setName("Updated Subscription");

        Subscriptions updatedSubscription = this.subscriptionsRepository.save(savedSubscription);

        Assertions.assertThat(updatedSubscription).isNotNull();

        Assertions.assertThat(updatedSubscription.getId()).isNotNull();

        Assertions.assertThat(updatedSubscription.getName()).isEqualTo(savedSubscription.getName());

    }

    @Test
    @DisplayName("Delete removes subscription when successful")
    void delete_RemovesSubscription_WhenSuccessful() {

        Subscriptions subscriptionToBeSaved = createSubscription();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        this.subscriptionsRepository.delete(savedSubscription);

        Optional<Subscriptions> subscriptionsOptional = this.subscriptionsRepository.findById(savedSubscription.getId());

        Assertions.assertThat(subscriptionsOptional).isEmpty();

    }

    @Test
    @DisplayName("Find by code returns subscription when successful")
    void findByCode_ReturnsSubscription_WhenSuccessful() {

        Subscriptions subscriptionToBeSaved = createSubscription();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        String code = savedSubscription.getCode();

        Optional<Subscriptions> subscriptions = this.subscriptionsRepository.findByCode(code);

        Assertions.assertThat(subscriptions).isNotEmpty();

        Assertions.assertThat(subscriptions).contains(savedSubscription);

    }

    @Test
    @DisplayName("Find by code returns empty when no subscription is found")
    void findByCode_ReturnsEmpty_WhenSuccessful() {

        Optional<Subscriptions> subscriptions = this.subscriptionsRepository.findByCode("NotExistingCode");

        Assertions.assertThat(subscriptions).isEmpty();

    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when code is empty")
    void save_ThrowDataIntegrityViolationException_WhenCodeIsEmpty() {

        Subscriptions subscription = new Subscriptions();

        Assertions.assertThatThrownBy(() -> this.subscriptionsRepository.save(subscription))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private Subscriptions createSubscription() {

        return Subscriptions.builder()
                .name("Subscription Test")
                .code("SUBSCRIPTION_TEST")
                .status("1")
                .build();
    }
}