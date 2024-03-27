package com.loktar.dto.wx;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class Menu {
    private List<Button> button;
    private String menuId;

    public Menu(List<Button> button) {
        this.button = button;
    }

    @Getter
    @Setter
    public static class Button {
        private String name;
        private String type;
        private Integer order;
    }

    @Getter
    @Setter
    public static class ParentButton extends Button {
        private String key;
        private List<Button> subButton = new ArrayList<>();

        public ParentButton(String name, String key, Integer order) {
            super.setName(name);
            super.setOrder(order);
            this.key = key;
        }
    }

    @Getter
    @Setter
    public static class ViewButton extends Button {
        private String url;
        public ViewButton(String name,String url, Integer order) {
            super.setType(KeyButtonType.VIEW.getName());
            super.setName(name);
            super.setOrder(order);
            this.url = url;
        }
    }

    @Getter
    @Setter
    public static class KeyButton extends Button {
        private String key;

        public KeyButton(KeyButtonType type, String name, String key, Integer order) {
            super.setType(type.getName());
            super.setName(name);
            super.setOrder(order);
            this.key = key;
        }
    }

    @Getter
    @Setter
    public static class MiniKeyButton extends Button {
        private String url;
        private String appid;
        private String pagepath;

        public MiniKeyButton(KeyButtonType type, String name, Integer order, String url, String appid, String pagepath) {
            super.setType(type.getName());
            super.setName(name);
            super.setOrder(order);
            this.url = url;
            this.appid = appid;
            this.pagepath = pagepath;
        }
    }

    @Getter
    @Setter
    public static class ViewLimitedKeyButton extends Button {
        private String mediaId;

        public ViewLimitedKeyButton(KeyButtonType type, String name, Integer order, String mediaId) {
            super.setType(type.getName());
            super.setName(name);
            super.setOrder(order);
            this.mediaId = mediaId;
        }
    }
}
