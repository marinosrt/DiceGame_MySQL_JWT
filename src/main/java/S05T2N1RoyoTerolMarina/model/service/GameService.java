package S05T2N1RoyoTerolMarina.model.service;

import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GameService {

    PlayerDTO createPlayer(PlayerDTO playerDTO);

    GameDTO playGame(Long id);

    List<String> getAllPlayers();

    List<GameDTO> getGamesPlayerId(Long id);

    String getAverageAllPlayers();

    PlayerDTO getBestLooser();

    PlayerDTO getBestWinner();

    PlayerDTO updatePlayerId(PlayerDTO playerDTO, Long id);

    int deleteGamesId(Long id);

    boolean playerExistById(Long id);

}
