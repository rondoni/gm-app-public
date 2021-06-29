package com.game.gameservermaster.service;

import com.game.gameservermaster.model.GameCharacter;

import java.util.List;

public interface GameDBService {

    GameCharacter findCharByID(Integer id);
    List<GameCharacter> findCharsByIDs(List<Integer> idList);
    GameCharacter updateChar(GameCharacter gameCharacter);
    GameCharacter insertChar(GameCharacter gameCharacter);

}
