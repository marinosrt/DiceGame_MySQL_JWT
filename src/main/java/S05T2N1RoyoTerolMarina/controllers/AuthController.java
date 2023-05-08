package S05T2N1RoyoTerolMarina.controllers;

import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationRequest;
import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationResponse;
import S05T2N1RoyoTerolMarina.controllers.auth.RegisterRequest;
import S05T2N1RoyoTerolMarina.model.exception.UnexpectedErrorException;
import S05T2N1RoyoTerolMarina.model.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("players/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(
            operationId = "Create a player with user and password.",
            summary = "Fill up the information to sign up this user to be able play dice games.",
            description = "Enter the players' name.",
            responses = {
                    @ApiResponse(
                            responseCode = "201 - CREATED",
                            description = "Player correctly created into the Database Response",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {@ExampleObject(name = "Player Creation", value = "Player")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Error creating player Response Response."
                    )
            }
    )
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            return ResponseEntity.ok(service.register(request));
        } catch (UnexpectedErrorException e) {
            return new ResponseEntity<>("Internal server error while creating a player", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticationRequest(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authentication(request));
    }

}

