package S05T2N1RoyoTerolMarina.controllers.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String name;

    private String email;

    private String token; //token that will be sent back to the user
    private String message;


}
