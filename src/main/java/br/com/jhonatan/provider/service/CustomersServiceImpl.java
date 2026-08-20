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
    public ResponseEntity<StatusResponse> create(CustomerRequest request) {
        try{
            Optional<Users> user = usersRepository.findByUsername(request.getUsername());

            if (user.isPresent())
                throw new UserAlreadyExistsException();

            Users newUser = Users.builder()
                    .username(request.getUsername())
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .document(request.getDocument())
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            usersRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    StatusResponse.builder()
                            .status("success")
                            .message("User saved successfully")
                            .statusCode("201")
                            .build()
            );


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<StatusResponse> update(CustomerRequest request) {
        try{
            Users user = usersRepository.findByUsername(request.getUsername())
                    .orElseThrow(UserNotFoundException::new);

            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setDocument(request.getDocument());

            usersRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(
                    StatusResponse.builder()
                            .status("success")
                            .message("User updated successfully")
                            .statusCode("200")
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
