package S05T2N1RoyoTerolMarina.model.service;

import S05T2N1RoyoTerolMarina.model.domain.Game;
import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
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

    //private static Player playerRequest;
    private static PlayerDTO playerDTORequest;

    public GameServiceImplImpl(PlayersRepository playersRepository, GamesRepository gamesRepository) {
        super();
        this.playersRepository = playersRepository;
        this.gamesRepository = gamesRepository;
    }

    //region SERVICE

    @Override // FALTARIA DATA REGISTRE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {

        Player playerRequest;

        playerDTO.setName(setPlayersName(playerDTO));

        if (playerDTO.getName().equalsIgnoreCase("")) {
            playerRequest = null;
        } else {
            playerRequest = playersRepository.save(playerConvertEntity(playerDTO));
        }

        return playerConvertDTO(playerRequest);

    }

    @Override
    public GameDTO playGame(Long id) {
        Player playerRequest = null;
        Game game = null;

        if (playersRepository.existsById(id)){
            Optional<Player> playerData = playersRepository.findById(id);
            playerRequest = playerData.get();
            game = new Game(playerRequest);
            gamesRepository.save(game);
        }

        return gameConvertDTO(game);
    }

    @Override
    public List<String> getAllPlayers() {
        List<Player> playersList;
        List<String> returnList;

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
    }

    @Override
    public List<GameDTO> getGamesPlayerId(Long id) {
        Player playerRequest;
        List<GameDTO> gamesList = null;

        if (playersRepository.existsById(id)){
            Optional<Player> playerData = playersRepository.findById(id);
            playerRequest = playerData.get();

            gamesList = gamesRepository.findAll().stream()
                    .filter(game -> Objects.equals(game.getPlayer().getId(), playerRequest.getId()))
                    .toList()
                    .stream().map(this::gameConvertDTO)
                    .collect(Collectors.toList());
        } else {
            gamesList = new ArrayList<>();
        }

        return gamesList;
    }

    @Override
    public String getAverageAllPlayers() {
        String output;

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
    }

    @Override
    public PlayerDTO getBestLooser() {
        List<PlayerDTO> playerDTOList;

        playerDTOList = getSuccessRatePlayers();

        return playerDTOList.stream()
                .filter(playerDTO -> !playerDTO.getSuccessRate().equalsIgnoreCase("Still haven't played any game"))
                .min(Comparator.comparingDouble(player -> Double.parseDouble(player.getSuccessRate())))
                .stream().findAny()
                .orElse(null);
    }

    @Override
    public PlayerDTO getBestWinner() {
        List<PlayerDTO> playerDTOList;

        playerDTOList = getSuccessRatePlayers();

        return playerDTOList.stream()
                .filter(playerDTO -> !playerDTO.getSuccessRate().equalsIgnoreCase("Still haven't played any game"))
                .max(Comparator.comparingDouble(player -> Double.parseDouble(player.getSuccessRate())))
                .stream().findAny()
                .orElse(null);
    }

    @Override // AFEGIR EXCEPTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! <--------------------
    public PlayerDTO updatePlayerId(PlayerDTO playerDTO, Long id) {
        Player playerRequest;

        if (playersRepository.existsById(id)){
            Optional<Player> playerData = playersRepository.findById(id);
            playerRequest = playerData.get();
            playerRequest.setName(playerDTO.getName());
            playersRepository.save(playerRequest);
        } else {
            playerRequest = null;
        }

        return playerConvertDTO(playerRequest);

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
            // AFEGIR EXCEPTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! <--------------------
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

        playerDTOList = playersRepository.findAll().stream()
                .map(this::playerConvertDTO)
                .collect(Collectors.toList());

        playerDTOList.forEach(player -> player.setSuccessRate(calculateRate(player)));

        return playerDTOList;

    }

    public String calculateRate(PlayerDTO playerDTO) {

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
    }

    public String setPlayersName(PlayerDTO playerDTO){
        String playerName;

        if (playerDTO.getName().equalsIgnoreCase("") || playerDTO.getName().isEmpty()){
            playerName = "ANONYMOUS";
        } else {
            if(!checkString(playerDTO.getName())){ // check if it already exists into the DDBB
                playerName = playerDTO.getName();
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
