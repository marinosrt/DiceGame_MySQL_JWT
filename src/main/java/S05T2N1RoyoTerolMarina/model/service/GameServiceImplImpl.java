package S05T2N1RoyoTerolMarina.model.service;

import S05T2N1RoyoTerolMarina.model.domain.Game;
import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import S05T2N1RoyoTerolMarina.model.exception.NoContentFoundException;
import S05T2N1RoyoTerolMarina.model.exception.PlayerNotFoundException;
import S05T2N1RoyoTerolMarina.model.repository.GamesRepository;
import S05T2N1RoyoTerolMarina.model.repository.PlayersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImplImpl implements GameService{

    @Autowired
    private ModelMapper modelMapper;

    private PlayersRepository playersRepository;
    private GamesRepository gamesRepository;

    public GameServiceImplImpl(PlayersRepository playersRepository, GamesRepository gamesRepository) {
        super();
        this.playersRepository = playersRepository;
        this.gamesRepository = gamesRepository;
    }

    //region SERVICE

    @Override // FALTARIA DATA REGISTRE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Player playerRequest;
        PlayerDTO playerDtoResponse;

        playerRequest = playerConvertEntity(playerDTO);
        playerRequest.setName(setPlayersName(playerRequest));

        if (playerRequest.getName().equalsIgnoreCase("")) {
            return null;
        } else {
            playersRepository.save(playerRequest);
            playerDtoResponse = playerConvertDTO(playerRequest);
            playerDtoResponse.setSuccessRate(calculateRate(playerDtoResponse));

            return playerDtoResponse;
        }
    }

    @Override
    public GameDTO playGame(Long id) {
        Player playerRequest;
        Game game = null;

        try {
            if (playersRepository.existsById(id)){
                Optional<Player> playerData = playersRepository.findById(id);
                playerRequest = playerData.get();
                game = new Game(playerRequest);
                gamesRepository.save(game);
            }

            return gameConvertDTO(game);

        } catch (PlayerNotFoundException e) {
            throw new PlayerNotFoundException("Player", id);
        }

    }

    @Override
    public List<String> getAllPlayers() {
        List<Player> playersList;
        List<String> returnList;

        try {
            playersList = playersRepository.findAll().stream().toList();

            if (gamesRepository.findAll().isEmpty()){
                returnList = null;
            } else {
                returnList = new ArrayList<>();
                for (Player player : playersList) {
                    returnList.add(player.toString());
                }
            }

            return returnList;

        } catch (NoContentFoundException e) {
            throw new NoContentFoundException("Unable to access players repository");
        }

    }

    @Override
    public List<GameDTO> getGamesPlayerId(Long id) {
        Player playerRequest;
        List<GameDTO> gamesList;

        try {
            if (playersRepository.existsById(id)) {
                if (gamesRepository.findAll().stream().noneMatch(game -> game.getPlayer().getId() == id)) {
                    gamesList = null;
                } else {
                    Optional<Player> playerData = playersRepository.findById(id);
                    playerRequest = playerData.get();

                    gamesList = gamesRepository.findAll().stream()
                            .filter(game -> Objects.equals(game.getPlayer().getId(), playerRequest.getId()))
                            .toList()
                            .stream().map(this::gameConvertDTO)
                            .collect(Collectors.toList());
                }
            } else {
                gamesList = new ArrayList<>();
            }

            return gamesList;

        } catch (PlayerNotFoundException e) {
            throw new PlayerNotFoundException("Player", id);
        }
    }

    @Override
    public String getAverageAllPlayers() {
        String output;

        try {
            if (!gamesRepository.findAll().isEmpty()){
                double average = gamesRepository.findAll().stream()
                        .mapToDouble(game -> game.getStatus().equals("WINNER") ? 1 : 0)
                        .average()
                        .orElse(0.0) * 100.0;

                output = String.format("%.2f", average);
            } else {
                output = "";
            }

            return output;

        } catch (NoContentFoundException e) {
            throw new NoContentFoundException("Unable to access games repository");
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
            if (playersRepository.existsById(id)) {
                Optional<Player> playerData = playersRepository.findById(id);
                playerRequest = playerData.get();
                playerRequest.setName(playerDTO.getName());
                playersRepository.save(playerRequest);

                playerDtoResponse = playerConvertDTO(playerRequest);
                playerDtoResponse.setSuccessRate(calculateRate(playerDtoResponse));
            } else {
                playerDtoResponse = null;
            }

            return playerDtoResponse;

        } catch (PlayerNotFoundException e) {
            throw new PlayerNotFoundException("Player", id);
        }


    }

    @Override
    public int deleteGamesId(Long id) {
        int found = 0;

        try {
            if (playersRepository.existsById(id)){
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
        } catch (Exception e){
            throw new PlayerNotFoundException("Player", id);
        }

        return found;


    }

    //endregion SERVICE





    //region USEFUL

    public PlayerDTO playerConvertDTO(Player player){
        return modelMapper.map(player, PlayerDTO.class);
    }

    public Player playerConvertEntity(PlayerDTO playerDTO){
        return modelMapper.map(playerDTO, Player.class);
    }

    public GameDTO gameConvertDTO(Game game){
        return modelMapper.map(game, GameDTO.class);
    }

    private List<PlayerDTO> getSuccessRatePlayers() {
        List<PlayerDTO> playerDTOList;

        try {
            playerDTOList = playersRepository.findAll().stream()
                    .map(this::playerConvertDTO)
                    .collect(Collectors.toList());

            playerDTOList.forEach(player -> player.setSuccessRate(calculateRate(player)));

            return playerDTOList;

        } catch (NoContentFoundException e) {
            throw new NoContentFoundException("Unable to access players repository");
        }


    }

    public String calculateRate(PlayerDTO playerDTO) {

        try {
            if (gamesRepository.findAll().stream().anyMatch(game -> game.getPlayer().getId() == playerDTO.getId())) {
                double result = gamesRepository.findAll().stream()
                        .filter(game -> game.getPlayer().getId() == playerDTO.getId())
                        .toList().stream()
                        .mapToDouble(game -> game.getStatus().equals("WINNER") ? 1 : 0)
                        .average()
                        .orElse(0.0) * 100;

                return String.format(String.valueOf(result), result);
            } else {
                return "Still haven't played any game.";
            }
        } catch (NoContentFoundException e) {
            throw new NoContentFoundException("Unable to access games repository");
        }

    }

    public String setPlayersName(Player player){
        String playerName;

        if (player.getName().equalsIgnoreCase("") || player.getName().isEmpty()){
            playerName = "ANONYMOUS";
        } else {
            if(!checkString(player.getName())){ // check if it already exists into the DDBB
                playerName = player.getName();
            } else {
                playerName = "";
            }
        }

        return playerName;
    }

    public boolean checkString(String stringName){

        return playersRepository.findAll().stream().anyMatch(player -> player.getName().equalsIgnoreCase(stringName));

    }

    //endregion USEFUL
}
