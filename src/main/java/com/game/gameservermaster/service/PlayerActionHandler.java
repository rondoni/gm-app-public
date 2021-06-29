package com.game.gameservermaster.service;

import com.game.gameservermaster.config.GMErrorCode;
import com.game.gameservermaster.db.UserAccountCharRepository;
import com.game.gameservermaster.model.*;
import com.game.gameservermaster.utils.JSON;
import com.game.gameservermaster.utils.JwtUtils;
import com.game.gameservermaster.db.UserAccountRepository;
import com.game.gameservermaster.exception.InvalidUserAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.gameservermaster.config.GMConstants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.game.gameservermaster.utils.GMUtils.addError;

@Slf4j
@Component
public class PlayerActionHandler {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountCharRepository accountCharRepository;

    @Autowired
    private GameDBService gameDBService;

    @Autowired
    private GameInstanceManager instManager;

    @Autowired
    private JwtUtils jwtUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String INST_JOIN_FAIL = "instance join failed";

    //TODO: break this up ?
    public JSON handleInstanceJoin(String username, String payload) {
        JSON response = new JSON();
        GMErrorCode errorCode = null;
        try {
            InstanceJoinRequest req = objectMapper.readValue(payload, InstanceJoinRequest.class);
            try {
                GameCharacter character = gameDBService.findCharByID(req.getPlayerID());
                GameInstance gameInst = instManager.getInstByID(req.getInstID());
                validateInstJoin(username, character, gameInst);
                String tokenString = jwtUtils.buildClientToken(username)
                    .claim(GMConstants.JsonKeys.CHAR_ID, character.getId())
                    .claim(GMConstants.JsonKeys.CHAR_NAME, character.getCharName())
                    .claim(GMConstants.JsonKeys.INST_ID, gameInst.getInstID())
                    .compact();
                response.put(GMConstants.JsonKeys.AUTH_TOKEN, tokenString);
                response.put(GMConstants.JsonKeys.SERVER_INFO, gameInst.toClientJson());

                log.info("player {} [{}] joining instance {}",
                    character.getCharName(),
                    character.getId(),
                    gameInst.getInstID());

                return response;

            } catch (InvalidUserAction e) {
                errorCode = e.getErrorCode();
            } catch (Exception e) {
                log.error("Exception in player join", e);
            }
        } catch (JsonMappingException e) {
            errorCode = GMErrorCode.MISSING_REQUIRED_PARAM;
        } catch (JsonProcessingException e) {
            errorCode = GMErrorCode.MALFORMED_JSON;
        }

        addError(response, INST_JOIN_FAIL, errorCode);
        log.info("player failed to join instance - Request: {} - Response: {}",
            payload.toString(), response.toString());

        return response;
    }

    private void validateInstJoin(String username, GameCharacter character, GameInstance gameInst) {

        String charAccountUsername = accountCharRepository.findUsernameByCharID(character.getId());
        if(charAccountUsername == null || !charAccountUsername.equals(username))
            throw new InvalidUserAction(GMErrorCode.CHAR_NOT_FOUND, "invalid user character");

        if (gameInst == null)
            throw new InvalidUserAction(GMErrorCode.GAME_INST_NOT_FOUND, "game inst not found");

        if (gameInst.getInstID() !=  character.getInstID()){
            if(gameInst.isPublic()) {
                //TODO: send player disconnect to current inst
            } else {
                throw new InvalidUserAction(GMErrorCode.CHAR_INVALID_GAME_INST, "char invalid instance join");
            }
        }
    }

    private List<GameCharacter> findCharListByUser(UserAccount userAccount) {
        List<Integer> userAccountCharIDs = userAccount.getUserAccountCharList().stream().map(e -> e.getCharID()).collect(Collectors.toList());
        return gameDBService.findCharsByIDs(userAccountCharIDs);
    }

    public void addCharList(String username, JSON json) {
        UserAccount userAccount = userAccountRepository.findById(username).get();
        addCharList(userAccount, json);
    }

    private void addCharList(UserAccount userAccount, JSON json) {
        List<GameCharacter> charList = findCharListByUser(userAccount);
        List<JSON> charJSONList = charList.stream().map(
            c -> c.toClientJson()).collect(Collectors.toList());
        json.put(GMConstants.JsonKeys.CHAR_LIST, charJSONList);
    }


}
