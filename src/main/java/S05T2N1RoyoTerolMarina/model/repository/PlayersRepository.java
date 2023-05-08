package S05T2N1RoyoTerolMarina.model.repository;

import S05T2N1RoyoTerolMarina.model.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayersRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByEmail(String email);

}
