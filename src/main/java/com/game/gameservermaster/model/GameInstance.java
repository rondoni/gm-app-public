package com.game.gameservermaster.model;

import com.game.gameservermaster.config.GMConstants.JsonKeys;
import com.game.gameservermaster.utils.JSON;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameInstance {

    private String instAlias;
    private String instID;
    private String addr;
    private int port;
    private boolean isPublic;
    private V1Pod pod;
    private boolean offCluster = false;
    //private Long initTime;
    //private int errorStateCounter = 0;

    public JSON toClientJson() {
        JSON json = new JSON();
        json.put(JsonKeys.INST_ALIAS, instAlias);
        json.put(JsonKeys.INST_ID, instID);
        json.put(JsonKeys.SERVER_ADDR, addr);
        json.put(JsonKeys.SERVER_PORT, port);
        return json;
    }

}
