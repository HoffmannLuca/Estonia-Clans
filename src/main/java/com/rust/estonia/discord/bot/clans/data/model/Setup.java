package com.rust.estonia.discord.bot.clans.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Setup {

    @Id
    private String id;

    private long serverId;

    private String serverName;

    private String prefix;

    public Setup(long serverId, String serverName) {
        this.serverId = serverId;
        this.serverName = serverName;
        this.prefix = "!";
    }

    public long getServerId() {
        return serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
