package com.game.gameservermaster.db;

import com.game.gameservermaster.model.UserAccount;
import com.game.gameservermaster.model.UserAccountChar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserAccountRepository extends CrudRepository<UserAccount, String> {

}
