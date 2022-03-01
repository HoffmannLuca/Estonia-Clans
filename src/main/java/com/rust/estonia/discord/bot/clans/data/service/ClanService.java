package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.data.model.Clan;
import com.rust.estonia.discord.bot.clans.data.repository.ClanRepository;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.ExecutionException;

@Component
public class ClanService {

    @Autowired
    private ClanRepository repository;

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    private Logger logger = LoggerFactory.getLogger(ClanService.class);

    public final String CLAN_TEXT_CHAT = "clan-text-chat";

    public Clan createClan(Server server, User leader, String clanName){

        Clan clan = getClan(server, leader);
        if(clan == null){
            clan = repository.save(new Clan(server.getId(), leader.getId(), clanName));
        }
        return clan;
    }

    public Clan getClan(Server server, User leader){

        return repository.findByServerIdAndClanLeaderId(server.getId(), leader.getId());
    }

    public Clan getClanByRole(Server server, Role clanRole){

        return repository.findByServerIdAndClanRoleId(server.getId(), clanRole.getId());
    }

    public Role getClanRole(Server server, Clan clan){

        if(clan != null) {
            if (server.getRoleById(clan.getClanRoleId()).isPresent()) {
                return server.getRoleById(clan.getClanRoleId()).get();
            } else {
                try{
                    Role clanRole = server.createRoleBuilder()
                            .setPermissions(discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_EMPTY))
                            .setName(clan.getClanName())
                            .setMentionable(false)
                            .setDisplaySeparately(false)
                            .setColor(Color.WHITE)
                            .setAuditLogReason("Create clan role")
                            .create().get();
                    clan.setClanRoleId(clanRole.getId());
                    updateClan(clan);
                    return clanRole;
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public TextChannel getClanTextChat(Server server, Clan clan){

        if(clan != null){
            if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
                return server.getTextChannelById(clan.getClanTextChatId()).get();
            } else {
                try{
                    Role clanRole = getClanRole(server, clan);
                    TextChannel clanTextChat = server.createTextChannelBuilder()
                            .setName(clan.getClanName())
                            .addPermissionOverwrite(server.getEveryoneRole(), discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_ONLY))
                            .addPermissionOverwrite(clanRole, discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_SEND))
                           // .setCategory()
                            .setAuditLogReason("Create clan text chat")
                            .create().get();
                    clan.setClanTextChatId(clanTextChat.getId());
                    updateClan(clan);
                    return clanTextChat;
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public void deleteClan(Server server, Clan clan){

        if(server.getRoleById(clan.getClanRoleId()).isPresent()){
            server.getRoleById(clan.getClanRoleId()).get().delete();
        }
        if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
            server.getTextChannelById(clan.getClanTextChatId()).get().delete();
        }
        repository.delete(clan);
    }

    private Clan updateClan(Clan clan){

        return repository.save(clan);
    }
}
