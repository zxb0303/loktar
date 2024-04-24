package com.loktar.dto.transmission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrRequestMethodType {

    SESSION_GET("session-get"),
    SESSION_SET("session-set"),
    TORRENT_ADD("torrent-add"),
    TORRENT_START("torrent-start"),
    TORRENT_REMOVE("torrent-remove"),
    TORRENT_GET("torrent-get"),
    FREE_SPACE("free-space");

    private final String name;
    public static TrRequestMethodType getByName(String name) {
        for (TrRequestMethodType type : TrRequestMethodType.values()) {
            if (type.getName().equals(name)){
                return type;
            }
        }
        return null;
    }
}
