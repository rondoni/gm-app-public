package com.game.gameservermaster.model;

import com.game.gameservermaster.config.GMConstants;

import com.game.gameservermaster.utils.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(value = "Character")
public class GameCharacter {

    @PrimaryKey
    @Column("id")
    private int id;
    @Column("char_name")
    private String charName;
    @Column("inventory")
    private String inventory;
    @Column("inst_id")
    private String instID;
    @Column("last_updated")
    private long lastUpdated;

    public JSON toClientJson() {
        JSON json = new JSON();
        json.put(GMConstants.JsonKeys.CHAR_ID, id);
        json.put(GMConstants.JsonKeys.CHAR_NAME, charName);
        return json;
    }

}
