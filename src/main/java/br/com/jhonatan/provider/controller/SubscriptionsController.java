package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.service.SubscriptionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(RestControllerUrlBase.BASE_URL + "/customers/{username}/subscriptions")
@Tag(name = "Subscriptions", description = "Subscriptions linked to customers")
public class SubscriptionsController {

    private final SubscriptionsService subscriptionsService;

    @Operation(
            summary = "List the customer's subscriptions",
            description = "Returns all subscriptions linked to the customer identified by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of the customer's subscriptions",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubscriptionResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping
    public List<SubscriptionResponse> listSubscriptions(
            @Parameter(description = "Customer's username", example = "jhonatxnz")
            @PathVariable @NotBlank String username) {
        return subscriptionsService.list(username);
    }

    @Operation(
            summary = "Create a new subscription for the customer",
            description = "Links a new subscription to the customer identified by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Customer or subscription not found")
    })
    @PostMapping
    public ResponseEntity<StatusResponse> createSubscription(
            @Parameter(description = "Customer's username", example = "jhonatxnz")
            @PathVariable @NotBlank String username,
            @RequestBody @Valid SubscriptionRequest subscriptionRequest) {
        return subscriptionsService.subscribe(username, subscriptionRequest.getCode());
    }

    @Operation(
            summary = "Remove the customer's subscription",
            description = "Cancels/removes the subscription of the customer identified by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription removed successfully",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer or subscription not found")
    })
    @DeleteMapping("/{subscription}")
    public ResponseEntity<StatusResponse> deleteSubscription(
            @Parameter(description = "Customer's username", example = "jhonxtnz")
            @PathVariable @NotBlank String username,
            @Parameter(description = "Subscription code/identifier", example = "PLANO-PREMIUM")
            @PathVariable @NotBlank String code) {
        return subscriptionsService.cancel(username, code);
    }
}
