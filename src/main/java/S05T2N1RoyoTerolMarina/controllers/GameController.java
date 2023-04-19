package S05T2N1RoyoTerolMarina.controllers;

import S05T2N1RoyoTerolMarina.model.dto.GameDTO;
import S05T2N1RoyoTerolMarina.model.dto.PlayerDTO;
import S05T2N1RoyoTerolMarina.model.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(
            operationId = "Create a player.",
            summary = "Fill up the information to sign up this user to be able play dice games.",
            description = "Enter the players' name.",
            responses = {
                    @ApiResponse(
                            responseCode = "406 - NOT_ACCEPTABLE",
                            description = "The players' name already exist into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "201 - CREATED",
                            description = "Player correctly created into the Database Response",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                examples = {@ExampleObject(name = "Player Creation", value = "Player")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Error creating player Response Response."
                    )
            }
    )
    public ResponseEntity<?> createPlayer (@RequestBody PlayerDTO playerIn){

        PlayerDTO playerResponse = null;

        try {
            playerResponse = gameService.createPlayer(playerIn);
            if (playerResponse == null){
                responseEntity = new ResponseEntity<>(new WrongThreadException("This player already exist into the Database. Please, enter a valid name."), HttpStatus.NOT_ACCEPTABLE);
            } else {
                responseEntity = new ResponseEntity<>(playerResponse, HttpStatus.CREATED);
            }
        } catch (Exception e){
            responseEntity = new ResponseEntity<>(ResponseEntity.ofNullable(playerResponse), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping("/{id}/games")
    @Operation(
            operationId = "Create a dice game for an existing player.",
            summary = "It creates a rolling dice game for a particular player and store it into the Database.",
            description = "Enter the Player ID to roll the dices and play a game.",
            responses = {
                    @ApiResponse(
                            responseCode = "201 - CREATED",
                            description = "Correctly created a game for this player Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                examples = {@ExampleObject(name = "Game created", value = "Game")})
                    ),
                    @ApiResponse(
                            responseCode = "404 - NOT_FOUND",
                            description = "Player doesn't exist into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error while creating a game Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Get all the players.",
            summary = "Visualize all players, their success rate alongside with the time creation sign up details.",
            description = "Get all the players stored into the Database.",
            responses = {
                    @ApiResponse(
                            responseCode = "204 - NO_CONTENT",
                            description = "No players yet stored into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "200 - OK",
                            description = "Correctly retrieved stored players from the Database Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {@ExampleObject(name = "Players list", value = "Players List")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error while retrieving players from the Database Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Get all the games played by a particular player.",
            summary = "Get all the games from a certain Player ID and all the game details.",
            description = "Enter the Player ID in order to see all the games the player played.",
            responses = {
                  @ApiResponse(
                        responseCode = "404 - NOT_FOUND",
                        description = "This player hasn't played any games yet Response."
                  ),
                  @ApiResponse(
                          responseCode = "406 - NOT_ACCEPTABLE",
                          description = "This Player ID doesn't exist into the Database Response."
                  ),
                  @ApiResponse(
                          responseCode = "200 - OK",
                          description = "Correctly retrieved all stored games of this Player ID from the Database Response.",
                          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {@ExampleObject(name = "Games from Player ID", value = "Games List")})
                  ),
                  @ApiResponse(
                          responseCode = "500 - INTERNAL_SERVER_ERROR",
                          description = "Internal Server Error while retrieving games from the Database Response."
                  )
            }
    )
    public ResponseEntity<?> getGamesFromIdPlayer(@PathVariable("id") Long id){
        List<GameDTO> gamesPlayerList;

        try {
            gamesPlayerList = gameService.getGamesPlayerId(id);

            if (gamesPlayerList == null) {
                responseEntity = new ResponseEntity<>("No games for this player found into the Database", HttpStatus.NOT_FOUND);
            } else if (gamesPlayerList.size() == 0) {
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
    @Operation(
            operationId = "Get average rate from all players.",
            summary = "Visualize the average rate of the existing games played and stored into the Database.",
            description = "Get the average rate from all the existing dice game percentage.",
            responses = {
                    @ApiResponse(
                            responseCode = "200 - OK",
                            description = "Correctly retrieved the average rate from the Database Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                examples = {@ExampleObject(name = "Average Rate Percentage", value = "Double Average Rate")})
                    ),
                    @ApiResponse(
                            responseCode = "204 - NO_CONTENT",
                            description = "No results found into the Database to get the average rate Response."
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error getting the average rate Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Get the information of the player with the lowest success rate.",
            summary = "Visualize the player with the lowest success rate from its rolled dice games.",
            description = "Get the worst success rate player from the Database.",
            responses = {
                    @ApiResponse(
                          responseCode = "204 - NO_CONTENT",
                          description = "No available players with rolled dice games stored into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "200 - 0K",
                            description = "Correctly retrieved the worst success rate player from the Database Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                examples = {@ExampleObject(name = "Player", value = "Player object")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error getting the worst player from the Database Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Get the information of the player with the highest success rate.",
            summary = "Visualize the player with the highest success rate from its rolled dice games.",
            description = "Get the best success rate player from the Database.",
            responses = {
                    @ApiResponse(
                            responseCode = "204 - NO_CONTENT",
                            description = "No available players with rolled dice games stored into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "200 - 0K",
                            description = "Correctly retrieved the best success rate player from the Database Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {@ExampleObject(name = "Player", value = "Player object")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error getting the best player from the Database Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Update the name of an existing Player.",
            summary = "Update the name information stored into the Database from a certain player.",
            description = "Enter the Player ID in order to fill up the new name for the Player ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "404 - NOT_FOUND",
                            description = "The Player ID selected doesn't exist into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "202 - ACCEPTED",
                            description = "Correctly updated the new information for the Player ID Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {@ExampleObject(name = "Player updated", value = "Player object")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error updating Player ID name into the Database Response."
                    )
            }
    )
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
    @Operation(
            operationId = "Delete all the stored games from a Player ID.",
            summary = "Get to delete the games stored into the Database from a certain Player ID.",
            description = "Enter the ID from the player you want to delete all their games from the Database.",
            responses = {
                    @ApiResponse(
                            responseCode = "404 - NOT_FOUND",
                            description = "No stored players with this ID stored into the Database Response."
                    ),
                    @ApiResponse(
                            responseCode = "204 - NO_CONTENT",
                            description = "No available players with rolled dice games stored into the Database Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {@ExampleObject(name = "Player", value = "Player object")})
                    ),
                    @ApiResponse(
                            responseCode = "200 - OK",
                            description = "Successfully deleted games Response.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                examples = {@ExampleObject(name = "Success text message", value = "Text message")})
                    ),
                    @ApiResponse(
                            responseCode = "500 - INTERNAL_SERVER_ERROR",
                            description = "Internal Server Error deleting games from the Database Response."
                    )
            }
    )
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


















