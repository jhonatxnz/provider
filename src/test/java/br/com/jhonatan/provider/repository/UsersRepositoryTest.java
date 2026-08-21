package br.com.jhonatan.provider.repository;
import br.com.jhonatan.provider.model.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for UsersRepository")
class UsersRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Save creates user when successful")
    void save_PersistUser_WhenSuccessful() {
        Users userToBeSaved = createUser();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        Assertions.assertThat(savedUser).isNotNull();

        Assertions.assertThat(savedUser.getId()).isNotNull();

        Assertions.assertThat(savedUser.getUsername()).isEqualTo(userToBeSaved.getUsername());

    }

    @Test
    @DisplayName("Save updates user when successful")
    void save_UpdatesUser_WhenSuccessful() {

        Users userToBeSaved = createUser();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        savedUser.setName("Updated user");

        Users updatedUser = this.usersRepository.save(savedUser);

        Assertions.assertThat(updatedUser).isNotNull();

        Assertions.assertThat(updatedUser.getId()).isNotNull();

        Assertions.assertThat(updatedUser.getName()).isEqualTo(updatedUser.getName());

    }

    @Test
    @DisplayName("Delete removes user when successful")
    void delete_RemovesUser_WhenSuccessful() {

        Users userToBeSaved = createUser();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        this.usersRepository.delete(savedUser);

        Optional<Users> userOptional = this.usersRepository.findById(savedUser.getId());

        Assertions.assertThat(userOptional).isEmpty();

    }

    @Test
    @DisplayName("Find by username returns user when successful")
    void findByUsername_ReturnsUser_WhenSuccessful() {
        Users userToBeSaved = createUser();

        Users savedUser = this.usersRepository.save(userToBeSaved);

        String username = savedUser.getUsername();

        Optional<Users> user = this.usersRepository.findByUsername(username);

        Assertions.assertThat(user).isPresent();

        Assertions.assertThat(user).contains(savedUser);
    }

    @Test
    @DisplayName("Find by username returns empty when no subscription is found")
    void findByUsername_ReturnsEmpty_WhenSuccessful() {

        Optional<Users> users = this.usersRepository.findByUsername("NotExistingUsername");

        Assertions.assertThat(users).isEmpty();

    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowDataIntegrityViolationException_WhenNameIsEmpty() {

        Users user = new Users();

        Assertions.assertThatThrownBy(() -> this.usersRepository.save(user))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private Users createUser() {
        return Users.builder()
                .name("Test User")
                .username("testuser")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}