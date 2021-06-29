package com.game.gameservermaster.db;

import com.game.gameservermaster.model.UserAccountChar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserAccountCharRepository extends CrudRepository<UserAccountChar, Integer> {

    //for convenience
    @Query(value = "SELECT username FROM AccountCharacter WHERE char_id = ?1 ;", nativeQuery = true)
    String findUsernameByCharID(int charId);

}
