package com.game.gameservermaster.config;

import java.util.HashMap;

public enum GameInstType {

        ISLAND("islandscene", "game"),
        ;

        private final String val;
        private final String imgName;

        GameInstType(final String val, final String imgName) {
            this.val=val; 
            this.imgName=imgName; 
        }

        public String val(){return val;}
        public String imgName(){return imgName;}

        private static HashMap<String, GameInstType> typeInfo = new HashMap<String, GameInstType>();

        static {
            typeInfo.put(GameInstType.ISLAND.val(), GameInstType.ISLAND);
        }
   
        public static GameInstType find(String key) { return typeInfo.get(key); }
}
