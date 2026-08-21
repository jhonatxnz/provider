package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.UserSubscriptions;
import br.com.jhonatan.provider.model.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;
import java.util.Optional;


@DataJpaTest
@DisplayName("Tests for UserSubscriptionsRepository")
class UserSubscriptionsRepositoryTest {

    @Autowired
    UserSubscriptionsRepository userSubscriptionsRepository;

    @Test
    @DisplayName("Insert association when successful")
    void insert_Association_WhenSuccessful() {
        Users user = createUser();

        Subscriptions subscription = createSubscription();

        UserSubscriptions userSubscriptionToBeSaved = createUserSubscription(user, subscription);

        UserSubscriptions savedUserSubscriptions = this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(savedUserSubscriptions).isNotNull();

        Assertions.assertThat(savedUserSubscriptions.getId()).isNotNull();

        Assertions.assertThat(savedUserSubscriptions.getUserId()).isEqualTo(user.getId());

        Assertions.assertThat(savedUserSubscriptions.getSubscriptionId()).isEqualTo(subscription.getId());
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when user is null")
    void save_ThrowsDataIntegrityViolationException_WhenUserIsNull() {
        Subscriptions subscription = createSubscription();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(null)
                .subscriptionId(subscription.getId())
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when subscription is null")
    void save_ThrowsDataIntegrityViolationException_WhenSubscriptionIsNull() {
        Users user = createUser();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(null)
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when user not exists")
    void save_ThrowsConstraintViolationException_WhenUserNotExists() {
        Subscriptions subscription = createSubscription();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(2L)
                .subscriptionId(subscription.getId())
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when subscription not exists")
    void save_ThrowsConstraintViolationException_WhenSubscriptionNotExists() {
        Users user = createUser();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(2L)
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Update association when successful")
    void update_Association_WhenSuccessful() {
        Users user = createUser();

        Subscriptions subscriptions = createSubscription();

        UserSubscriptions userSubscriptionToBeSaved = createUserSubscription(user, subscriptions);

        UserSubscriptions savedUserSubscriptions = this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        savedUserSubscriptions.setStatus("0");

        UserSubscriptions UserSubscriptions = this.userSubscriptionsRepository.save(savedUserSubscriptions);

        Assertions.assertThat(UserSubscriptions).isNotNull();

        Assertions.assertThat(UserSubscriptions.getId()).isNotNull();

        Assertions.assertThat(UserSubscriptions.getStatus()).isEqualTo(savedUserSubscriptions.getStatus());

    }

    @Test
    @DisplayName("Delete removes association when successful")
    void delete_RemovesAssociation_WhenSuccessful() {
        Users user = createUser();

        Subscriptions subscriptions = createSubscription();

        UserSubscriptions userSubscriptionToBeSaved = createUserSubscription(user, subscriptions);

        UserSubscriptions savedUserSubscriptions = this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        this.userSubscriptionsRepository.delete(savedUserSubscriptions);

        Optional<UserSubscriptions> userSubscriptionOptional = this.userSubscriptionsRepository.findById(savedUserSubscriptions.getId());

        Assertions.assertThat(userSubscriptionOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by user id when successful")
    void findByUserId_WhenSuccessful() {
        Users user = createUser();

        Subscriptions subscriptions = createSubscription();

        UserSubscriptions userSubscriptionToBeSaved = createUserSubscription(user, subscriptions);

        this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserId(user.getId())).isNotEmpty();
    }

    private Users createUser() {
        return Users.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .phone("1234567890")
                .email("testuser.example.com")
                .document("123456789")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    private Subscriptions createSubscription() {
        return Subscriptions.builder()
                .id(1L)
                .name("Subscription Test")
                .code("SUBSCRIPTION_TEST")
                .category("Category Test")
                .description("Description Test")
                .price(BigDecimal.valueOf(10.0))
                .status("1")
                .build();
    }

    private UserSubscriptions createUserSubscription(Users user, Subscriptions subscription) {
        return UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .status("1")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

}