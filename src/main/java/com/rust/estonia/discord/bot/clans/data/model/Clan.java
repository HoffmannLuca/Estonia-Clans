package com.rust.estonia.discord.bot.clans.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document
public class Clan {

    @Id
    private String id;

    private long serverId;

    private long clanLeaderId;

    private String clanName;

    private long clanRoleId;

    private int clanRank;

    private long clanTextChatId;

    public Clan(long serverId, long clanLeaderId, String clanName) {
        this.serverId = serverId;
        this.clanLeaderId = clanLeaderId;
        this.clanName = clanName;
        this.clanRoleId = 0;
        this.clanRank=0;
        this.clanTextChatId = 0;
    }

    public long getServerId() { return serverId; }

    //ClanLeaderId
    public long getClanLeaderId() { return clanLeaderId; }
    public void setClanLeaderId(long clanLeaderId) { this.clanLeaderId = clanLeaderId; }

    //ClanName
    public String getClanName() { return clanName; }
    public void setClanName(String clanName) { this.clanName = clanName; }

    //ClanRoleId
    public long getClanRoleId() { return clanRoleId; }
    public void setClanRoleId(long clanRoleId) { this.clanRoleId = clanRoleId; }

    //ClanRank
    public int getClanRank() { return clanRank; }
    public void setClanRank(int clanRank) { this.clanRank = clanRank; }

    //ClanTextChat
    public long getClanTextChatId() { return clanTextChatId; }
    public void setClanTextChatId(long clanTextChatId) { this.clanTextChatId = clanTextChatId; }
}
