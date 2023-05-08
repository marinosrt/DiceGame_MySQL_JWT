package S05T2N1RoyoTerolMarina.model.service;

import S05T2N1RoyoTerolMarina.model.domain.Game;
import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import S05T2N1RoyoTerolMarina.model.exception.NoContentFoundException;
import S05T2N1RoyoTerolMarina.model.exception.PlayerNotFoundException;
import S05T2N1RoyoTerolMarina.model.exception.UnexpectedErrorException;
import S05T2N1RoyoTerolMarina.model.repository.GamesRepository;
import S05T2N1RoyoTerolMarina.model.repository.PlayersRepository;
import S05T2N1RoyoTerolMarina.model.service.utils.GameUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImplImpl implements GameService, GameUtils {

    private final ModelMapper modelMapper;

    private final PlayersRepository playersRepository;
    private final GamesRepository gamesRepository;

    //region SERVICE

    @Override
    public GameDTO playGame(Long id) {
        Player playerRequest;
        Game game = null;

        try {
            playerRequest = getOptionalPlayer(id);
            game = new Game(playerRequest);
            gamesRepository.save(game);

            return gameConvertDTO(game);

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }

    }

    @Override
    public List<String> getAllPlayers() {
        List<String> returnList;

        try {
            if (playersRepository.findAll().isEmpty()) {
                returnList = null;
            } else {
                returnList = new ArrayList<>();
                for (Player player : playersRepository.findAll().stream().toList()) {
                    returnList.add(player.toString());
                }
            }

            return returnList;

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }

    }

    @Override
    public List<GameDTO> getGamesPlayerId(Long id) {
        List<GameDTO> gamesList = null;
        try {

            if (gamesRepository.findAll().stream().anyMatch(game -> game.getPlayer().getId() == id)) {
                gamesList = gamesRepository.findAll().stream()
                        .filter(game -> Objects.equals(game.getPlayer().getId(), id))
                        .toList()
                        .stream().map(this::gameConvertDTO)
                        .collect(Collectors.toList());
            }

            return gamesList;

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }
    }

    @Override
    public String getAverageAllPlayers() {
        String output;

        try {
            if (!gamesRepository.findAll().isEmpty()){
                output = getRateFromList(gamesRepository.findAll().stream().toList());

            } else {
                output = "";
            }

            return output;

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }
    }

    @Override
    public PlayerDTO getBestLooser() {
        List<PlayerDTO> playerDTOList;

        playerDTOList = getSuccessRatePlayers();
        playerDTOList.removeIf(playerDTO -> playerDTO.getSuccessRate().equalsIgnoreCase("Still haven't played any game."));

        return playerDTOList.stream()
                .min(Comparator.comparingDouble(player -> Double.parseDouble(player.getSuccessRate())))
                .stream().findAny()
                .orElse(null);
    }

    @Override
    public PlayerDTO getBestWinner() {
        List<PlayerDTO> playerDTOList;

        playerDTOList = getSuccessRatePlayers();
        playerDTOList.removeIf(playerDTO -> playerDTO.getSuccessRate().equalsIgnoreCase("Still haven't played any game."));

        return playerDTOList.stream()
                .max(Comparator.comparingDouble(player -> Double.parseDouble(player.getSuccessRate())))
                .stream().findAny()
                .orElse(null);
    }

    @Override
    public PlayerDTO updatePlayerId(PlayerDTO playerDTO, Long id) {
        Player playerRequest;
        PlayerDTO playerDtoResponse;

        try {
            playerRequest = getOptionalPlayer(id);
            playerRequest.setName(playerDTO.getName());
            playersRepository.save(playerRequest);

            playerDtoResponse = playerConvertDTO(playerRequest);
            playerDtoResponse.setSuccessRate(calculateRate(playerDtoResponse));

            return playerDtoResponse;

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }
    }

    @Override
    public int deleteGamesId(Long id) {
        int found = 0;

        try {
            if (playerExistById(id)) {
                if (gamesRepository.findAll().stream().anyMatch(game -> Objects.equals(game.getPlayer().getId(), id))){
                    gamesRepository.findAll().stream()
                            .filter(game -> Objects.equals(game.getPlayer().getId(), id))
                            .toList()
                            .forEach(game -> gamesRepository.deleteById(game.getId()));
                    found = 2;
                } else {
                    found = 1;
                }
            }
        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }

        return found;


    }

    @Override
    public boolean playerExistById(Long id) {

        return playersRepository.existsById(id);

    }

    //endregion SERVICE





    //region UTILS

    @Override
    public Player getOptionalPlayer(Long id) {
        Optional<Player> playerData = playersRepository.findById(id);
        return playerData.get();
    }

    @Override
    public PlayerDTO playerConvertDTO(Player player){
        return modelMapper.map(player, PlayerDTO.class);
    }

    @Override
    public Player playerConvertEntity(PlayerDTO playerDTO){
        return modelMapper.map(playerDTO, Player.class);
    }

    @Override
    public GameDTO gameConvertDTO(Game game){
        return modelMapper.map(game, GameDTO.class);
    }

    @Override
    public List<PlayerDTO> getSuccessRatePlayers() {
        List<PlayerDTO> playerDTOList;

        try {
            playerDTOList = playersRepository.findAll().stream()
                    .map(this::playerConvertDTO)
                    .collect(Collectors.toList());

            playerDTOList.forEach(player -> player.setSuccessRate(calculateRate(player)));

            return playerDTOList;

        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }
    }

    @Override
    public String calculateRate(PlayerDTO playerDTO) {

        String output;

        try {
            if (gamesRepository.findAll().stream().anyMatch(game -> game.getPlayer().getId() == playerDTO.getId())) {

                output = getRateFromList(gamesRepository.findAll().stream().filter(game -> game.getPlayer().getId() == playerDTO.getId()).toList());

            } else {
                output = "Still haven't played any game.";
            }

            return output;
        } catch (UnexpectedErrorException e) {
            throw new UnexpectedErrorException("Unexpected error!");
        }
    }

    @Override
    public String getRateFromList(List<Game> gameList) {

        double result = gameList.stream()
                .mapToDouble(game -> game.getStatus().equals("WINNER") ? 1 : 0)
                .average()
                .orElse(0.0) * 100;

        return String.format("%.2f%%", result);

    }

    //endregion UTILS
}
