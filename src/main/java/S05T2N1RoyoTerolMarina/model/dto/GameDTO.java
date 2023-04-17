package S05T2N1RoyoTerolMarina.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {

    private Long id;

    private int dice1;

    private int dice2;

    private String status;

    private Long idPlayer;

}
