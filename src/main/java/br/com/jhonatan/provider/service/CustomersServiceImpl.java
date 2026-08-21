package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.exception.UserAlreadyExistsException;
import br.com.jhonatan.provider.exception.UserNotFoundException;
import br.com.jhonatan.provider.model.Users;
import br.com.jhonatan.provider.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements CustomersService {

    private final UsersRepository usersRepository;

    @Override
    public CustomerResponse getByUsername(String username) {

        Users user = usersRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return CustomerResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .document(user.getDocument())
                .build();

    }

    @Override
    public ResponseEntity<StatusResponse> create(CustomerRequest customerRequest) {
        try{
            Optional<Users> user = usersRepository.findByUsername(customerRequest.getUsername());

            if (user.isPresent())
                throw new UserAlreadyExistsException();

            Users newUser = Users.builder()
                    .username(customerRequest.getUsername())
                    .name(customerRequest.getName())
                    .email(customerRequest.getEmail())
                    .phone(customerRequest.getPhone())
                    .document(customerRequest.getDocument())
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            usersRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    StatusResponse.builder()
                            .status("success")
                            .message("user saved successfully")
                            .statusCode("201")
                            .build()
            );


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<StatusResponse> update(CustomerRequest customerRequest) {
        try{
            Users user = usersRepository.findByUsername(customerRequest.getUsername())
                    .orElseThrow(UserNotFoundException::new);

            user.setName(customerRequest.getName());
            user.setEmail(customerRequest.getEmail());
            user.setPhone(customerRequest.getPhone());
            user.setDocument(customerRequest.getDocument());

            usersRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(
                    StatusResponse.builder()
                            .status("success")
                            .message("user updated successfully")
                            .statusCode("200")
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
