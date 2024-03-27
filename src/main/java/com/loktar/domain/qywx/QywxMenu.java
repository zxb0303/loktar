package com.loktar.domain.qywx;

import java.io.Serializable;

public class QywxMenu implements Serializable {
    private Integer menuId;

    private Integer agentId;

    private String button;

    private Integer buttonLevel;

    private Integer hasSubButton;

    private String name;

    private String type;

    private String key;

    private String url;

    private Integer order;

    private Integer status;

    private static final long serialVersionUID = 1L;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button == null ? null : button.trim();
    }

    public Integer getButtonLevel() {
        return buttonLevel;
    }

    public void setButtonLevel(Integer buttonLevel) {
        this.buttonLevel = buttonLevel;
    }

    public Integer getHasSubButton() {
        return hasSubButton;
    }

    public void setHasSubButton(Integer hasSubButton) {
        this.hasSubButton = hasSubButton;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", menuId=").append(menuId);
        sb.append(", agentId=").append(agentId);
        sb.append(", button=").append(button);
        sb.append(", buttonLevel=").append(buttonLevel);
        sb.append(", hasSubButton=").append(hasSubButton);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", key=").append(key);
        sb.append(", url=").append(url);
        sb.append(", order=").append(order);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}