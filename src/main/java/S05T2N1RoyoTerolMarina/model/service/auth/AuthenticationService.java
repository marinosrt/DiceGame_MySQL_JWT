package S05T2N1RoyoTerolMarina.model.service.auth;

import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationRequest;
import S05T2N1RoyoTerolMarina.controllers.auth.AuthenticationResponse;
import S05T2N1RoyoTerolMarina.controllers.auth.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authentication(AuthenticationRequest request);

}
