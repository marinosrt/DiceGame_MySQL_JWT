package S05T2N1RoyoTerolMarina.model.service.utils;

import S05T2N1RoyoTerolMarina.model.domain.Game;
import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GameUtils {

    Player getOptionalPlayer(Long id);

    PlayerDTO playerConvertDTO(Player player);

    Player playerConvertEntity(PlayerDTO playerDTO);

    GameDTO gameConvertDTO(Game game);

    List<PlayerDTO> getSuccessRatePlayers();

    String calculateRate(PlayerDTO playerDTO);

    String getRateFromList(List<Game> gameList);

    String setPlayersName(Player player);

    boolean checkString(String name);

}
