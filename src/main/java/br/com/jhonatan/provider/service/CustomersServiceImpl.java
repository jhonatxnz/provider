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
        //bad request config
        return CustomerResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .document(user.getDocument())
                .build();

    }

    @Override
    public StatusResponse create(CustomerRequest request) {
        try{
            Optional<Users> user = usersRepository.findByUsername(request.getUsername());

            if (!user.isEmpty()){
                return StatusResponse.builder()
                        .status("error")
                        .message("User already exists")
                        .statusCode("409")
                        .build();
            }

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
                    .message("User saved successfully")
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

            if (user.isEmpty()){
                return StatusResponse.builder()
                        .status("error")
                        .message("User does not exist")
                        .statusCode("404")
                        .build();
            }

            Users existing = user.get();

            existing.setName(request.getName());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            existing.setDocument(request.getDocument());

            usersRepository.save(existing);

            return StatusResponse.builder()
                    .status("success")
                    .message("User updated successfully")
                    .statusCode("200")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
