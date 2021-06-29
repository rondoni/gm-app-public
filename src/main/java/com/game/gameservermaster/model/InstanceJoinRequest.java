package com.game.gameservermaster.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.game.gameservermaster.config.GMConstants;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(value = "true")
public class InstanceJoinRequest {

    @JsonCreator
    public InstanceJoinRequest(
        @JsonProperty(value = GMConstants.JsonKeys.CHAR_ID, required = true) int playerID,
        @JsonProperty(value = GMConstants.JsonKeys.INST_ID, required = true) String instID
    ) {
        this.playerID = playerID;
        this.instID = instID;
    }

    private int playerID;
    private String instID;
}
