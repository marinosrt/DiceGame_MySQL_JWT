package S05T2N1RoyoTerolMarina.model.repository;

import S05T2N1RoyoTerolMarina.model.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository  extends JpaRepository<Game, Long> {

}
