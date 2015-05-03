package spittr.data;

import org.springframework.data.jpa.repository.JpaRepository;
import spittr.Spitter;

import java.util.List;

/**
 * Created by shawnritchie on 16/04/15.
 */
public interface SpitterRepository extends JpaRepository<Spitter, Long>  {

    Spitter findByUsername(String username);

    List<Spitter> findByUsernameOrFullNameLike(String username, String fullName);

}
