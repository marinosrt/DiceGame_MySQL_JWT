package S05T2N1RoyoTerolMarina.model.service.auth;

import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationRequest;
import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationResponse;
import S05T2N1RoyoTerolMarina.controllers.auth.RegisterRequest;
import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.domain.Role;
import S05T2N1RoyoTerolMarina.model.exception.PlayerNotFoundException;
import S05T2N1RoyoTerolMarina.model.repository.PlayersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final PlayersRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        if (repository.findAll().stream().anyMatch(player -> player.getName().equalsIgnoreCase(request.getName()))) {
            throw new IllegalArgumentException("This name already exist");
        } else {
            if (request.getName().equalsIgnoreCase("") || request.getName().isEmpty()) {
                request.setName("ANONYMOUS");
            }
            var user = Player.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();

            repository.save(user);

            //to return the AuthResponse that contains the new token created with the user
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .token(jwtToken)
                    .message("Player created!")
                    .build();
        }
    }

    @Override
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        //if I get here, means user and pw are correct
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new PlayerNotFoundException("This player name " + request.getEmail() + " has no permission"));

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}

