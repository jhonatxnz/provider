package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.CustomerUpdateRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.service.CustomersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(RestControllerUrlBase.BASE_URL + "/customers")
@Tag(name = "Customers", description = "Customer registration and lookup")
public class CustomersController {

    private final CustomersService customersService;

    @Operation(
            summary = "Get customer information",
            description = "Returns the registration data of the customer identified by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{username}")
    public CustomerResponse getCustomer(
            @Parameter(description = "Customer's username", example = "jhonatan.silva")
            @PathVariable @NotBlank String username) {
        return customersService.getByUsername(username);
    }

    @Operation(
            summary = "Create a new customer",
            description = "Registers a new customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<StatusResponse> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customersService.create(customerRequest);
    }

    @Operation(
            summary = "Update customer data",
            description = "Updates the registration information of an existing customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping
    public ResponseEntity<StatusResponse> updateCustomer(@Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        return customersService.update(customerUpdateRequest); //Alterar para pathvariable
    }
}
