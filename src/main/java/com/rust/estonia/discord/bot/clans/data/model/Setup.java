package com.rust.estonia.discord.bot.clans.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document
public class Setup {

    @Id
    private String id;

    private long serverId;

    private String serverName;

    private HashMap<String, Long> categoryIdMap;

    private HashMap<String, Long> textChannelIdMap;

    private HashMap<String, Long> voiceChannelIdMap;

    private HashMap<String, Long> roleIdMap;

    public Setup(long serverId, String serverName) {
        this.serverId = serverId;
        this.serverName = serverName;
        this.categoryIdMap=new HashMap<>();
        this.textChannelIdMap=new HashMap<>();
        this.voiceChannelIdMap=new HashMap<>();
        this.roleIdMap=new HashMap<>();
    }

    //ServerId
    public long getServerId() {
        return serverId;
    }

    //ServerName
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    //CategoryIdMap
    public HashMap<String, Long> getCategoryIdMap() { return categoryIdMap; }
    public void setCategoryIdMap(HashMap<String, Long> categoryIdMap) { this.categoryIdMap = categoryIdMap; }

    //TextChannelIdMap
    public HashMap<String, Long> getTextChannelIdMap() { return textChannelIdMap; }
    public void setTextChannelIdMap(HashMap<String, Long> textChannelIdMap) { this.textChannelIdMap = textChannelIdMap; }

    //VoiceChannelIdMap
    public HashMap<String, Long> getVoiceChannelIdMap() { return voiceChannelIdMap; }
    public void setVoiceChannelIdMap(HashMap<String, Long> voiceChannelIdMap) { this.voiceChannelIdMap = voiceChannelIdMap; }

    //RoleIdMap
    public HashMap<String, Long> getRoleIdMap() { return roleIdMap; }
    public void setRoleIdMap(HashMap<String, Long> roleIdMap) { this.roleIdMap = roleIdMap; }
}
