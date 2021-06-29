package com.game.gameservermaster.service;

import com.game.gameservermaster.db.GameCharacterRespository;
import com.game.gameservermaster.model.GameCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CassandraGameDBService implements GameDBService {

    @Autowired
    private GameCharacterRespository gameCharRespository;

    @Override
    public GameCharacter findCharByID(Integer id) {
        return gameCharRespository.findById(id).get();
    }

    //TODO: update this when "IN" is supported
    @Override
    public List<GameCharacter> findCharsByIDs(List<Integer> idList) {
        return  idList.stream().map(id -> findCharByID(id)).collect(Collectors.toList());
    }

    @Override
    public GameCharacter updateChar(GameCharacter gameCharacter) {
        return gameCharRespository.save(gameCharacter);
    }

    @Override
    public GameCharacter insertChar(GameCharacter gameCharacter) {
        return gameCharRespository.insert(gameCharacter);
    }

}
