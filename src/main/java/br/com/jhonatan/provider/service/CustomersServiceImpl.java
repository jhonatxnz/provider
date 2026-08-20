package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.model.Users;
import br.com.jhonatan.provider.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements CustomersService {

    private final UsersRepository usersRepository;

    @Override
    public CustomerResponse getByUsername(String username) {

        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UnsupportedOperationException("User not found"));

        return CustomerResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .build();

    }

    @Override
    public StatusResponse create(CustomerRequest request) {
        try{
            Users user = usersRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UnsupportedOperationException("User not found"));


            Users updateUser = Users.builder()
                    .username(request.getUsername())
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .document(request.getDocument())
                    .build();

            usersRepository.save(updateUser);

            return StatusResponse.builder()
                    .status("success")
                    .message("User updated successfully")
                    .statusCode("201")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StatusResponse update(CustomerRequest request) {
        try{
            Optional<Users> user = usersRepository.findByUsername(request.getUsername());

            if(user.isPresent()){
                return StatusResponse.builder()
                        .status("error")
                        .message("User already exists")
                        .statusCode("409")
                        .build();
            }

            Users newUser = Users.builder()
                    .username(request.getUsername())
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .document(request.getDocument())
                    .build();

            usersRepository.save(newUser);

            return StatusResponse.builder()
                    .status("success")
                    .message("User update successfully")
                    .statusCode("201")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
