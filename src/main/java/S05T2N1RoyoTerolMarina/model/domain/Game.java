package S05T2N1RoyoTerolMarina.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int dice1;
    private int dice2;

    private String status;

    {
        dice1 = (int) (Math.random() * 6 + 1);
        dice2 = (int) (Math.random() * 6 + 1);
    }

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    public Game(Player player) {
        this.player = player;
        this.status = calculate();
    }

    private String calculate() {
        String result;

        if((this.dice1 + this.dice2) == 7){
            result = "WINNER";
        } else {
            result = "LOOSER";
        }

        return result;
    }


}
