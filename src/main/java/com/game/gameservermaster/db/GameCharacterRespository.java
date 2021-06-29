package com.game.gameservermaster.db;

import com.game.gameservermaster.model.GameCharacter;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCharacterRespository extends CassandraRepository<GameCharacter, Integer> {

}
