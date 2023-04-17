package S05T2N1RoyoTerolMarina.model.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime registration;

    public Player(String name) {
        this.name = name;
        this.registration = LocalDateTime.now();
    }


}
