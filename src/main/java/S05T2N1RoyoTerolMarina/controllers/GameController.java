package S05T2N1RoyoTerolMarina.controllers;

import S05T2N1RoyoTerolMarina.model.domain.Player;
import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import S05T2N1RoyoTerolMarina.model.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/players")
@Tag(name = "Dice Game Controller")
public class GameController {

    private GameService gameService;

    private ResponseEntity<?> responseEntity;

    public GameController(GameService gameService) {
        super();
        this.gameService = gameService;
    }

    @PostMapping("")
    public ResponseEntity<?> createPlayer (@RequestBody PlayerDTO playerIn){

        PlayerDTO playerResponse = null;

        try {
            playerResponse = gameService.createPlayer(playerIn);
            if (playerResponse == null){
                responseEntity = new ResponseEntity<>("This player already exist!", HttpStatus.NOT_ACCEPTABLE);
            } else {
                responseEntity = new ResponseEntity<>(playerResponse, HttpStatus.CREATED);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>(ResponseEntity.ofNullable(playerResponse), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping("/{id}/games")
    public ResponseEntity<?> playGame (@PathVariable("id") Long id){
        GameDTO gameResponse;

        try {
            gameResponse = gameService.playGame(id);
            if (gameResponse != null){
                responseEntity = new ResponseEntity<>(gameResponse, HttpStatus.CREATED);
            } else {
                responseEntity = new ResponseEntity<>("This player does not exist into the Database", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

        @GetMapping("")
    public ResponseEntity<?> getAllPlayers(){
        List<String> playersList;

        try {
            playersList = gameService.getAllPlayers();

            if (playersList == null){
                responseEntity = new ResponseEntity<>("No players signed up to the game just yet.", HttpStatus.NO_CONTENT);
            } else {
                responseEntity = new ResponseEntity<>(playersList, HttpStatus.OK);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<?> getGamesFromIdPlayer(@PathVariable("id") Long id){
        List<GameDTO> gamesPlayerList = null;

        try {
            gamesPlayerList = gameService.getGamesPlayerId(id);

            if (gamesPlayerList == null){
                responseEntity = new ResponseEntity<>("No games for this player found into the Database", HttpStatus.NOT_FOUND);
            } else if (gamesPlayerList.isEmpty()){
                responseEntity = new ResponseEntity<>("This player doesn't exist into the Database", HttpStatus.NOT_ACCEPTABLE);
            } else {
                responseEntity = new ResponseEntity<>(gamesPlayerList, HttpStatus.OK);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getAverageAllPlayers(){
        String averageAllPlayers;

        try {
            averageAllPlayers = gameService.getAverageAllPlayers();

            if (!averageAllPlayers.equalsIgnoreCase("")){
                responseEntity = new ResponseEntity<>(averageAllPlayers, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>("No results found", HttpStatus.NO_CONTENT);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/ranking/looser")
    public ResponseEntity<?> getBestLooser(){
        PlayerDTO playerResponse;

        try {
            playerResponse = gameService.getBestLooser();

            if (playerResponse == null){
                responseEntity = new ResponseEntity<>("No results found", HttpStatus.NO_CONTENT);
            } else {
                responseEntity = new ResponseEntity<>(playerResponse, HttpStatus.OK);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/ranking/winner")
    public ResponseEntity<?> getBestWinner(){
        PlayerDTO playerResponse;

        try {
            playerResponse = gameService.getBestWinner();

            if (playerResponse == null){
                responseEntity = new ResponseEntity<>("No results found", HttpStatus.NO_CONTENT);
            } else {
                responseEntity = new ResponseEntity<>(playerResponse, HttpStatus.OK);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayersName(@PathVariable("id") Long id, @RequestBody PlayerDTO playerDTO){
        PlayerDTO playerResponse;

        try {
            playerResponse = gameService.updatePlayerId(playerDTO, id);

            if (playerResponse == null){
                responseEntity = new ResponseEntity<>("No such player into the Database", HttpStatus.NOT_FOUND);
            } else {
                responseEntity = new ResponseEntity<>(playerResponse, HttpStatus.ACCEPTED);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @DeleteMapping("/{id}/games")
    public ResponseEntity<?> deleteGamesPlayerId(@PathVariable("id") Long id){
        int deleted;

        try {
            deleted = gameService.deleteGamesId(id);

            switch (deleted){
                case 0 -> responseEntity = new ResponseEntity<>("Player not found into the Database", HttpStatus.NOT_FOUND);
                case 1 -> responseEntity = new ResponseEntity<>("This player still hasn't played any game.", HttpStatus.NO_CONTENT);
                case 2 -> responseEntity = new ResponseEntity<>("Games deleted!", HttpStatus.OK);
            }

        } catch (Exception e){
            responseEntity = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}


















