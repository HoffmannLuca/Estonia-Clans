package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.constant.RoleTag;
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
    private SetupService setupService;

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    private Logger logger = LoggerFactory.getLogger(ClanService.class);

    public final String CLAN_TEXT_CHAT = "clan-text-chat";

    public Role createClan(Server server, User leader, String clanName){

        Clan clan = getClan(server, leader);
        if(clan == null){
            clan = repository.save(new Clan(server.getId(), leader.getId(), clanName));
        }

        return getClanRole(server, clan);
    }

    private Clan getClan(Server server, User leader){

        return repository.findByServerIdAndClanLeaderId(server.getId(), leader.getId());
    }

    private Clan getClanByRole(Server server, Role clanRole){

        return repository.findByServerIdAndClanRoleId(server.getId(), clanRole.getId());
    }

    private Role getClanRole(Server server, Clan clan){

        if(clan != null) {
            if (server.getRoleById(clan.getClanRoleId()).isPresent()) {
                return server.getRoleById(clan.getClanRoleId()).get();
            } else {
                try{
                    Role clanRole = server.createRoleBuilder()
                            .setPermissions(discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_EMPTY))
                            .setName(clan.getClanName())
                            .setMentionable(true)
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

    public Role getClanRoleByLeader(Server server, User clanLeader){

        Clan clan = getClan(server, clanLeader);
        return getClanRole(server, clan);
    }

    public TextChannel getClanTextChat(Server server, Role clanRole){

        Clan clan = getClanByRole(server, clanRole);

        if(clan != null){
            if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
                return server.getTextChannelById(clan.getClanTextChatId()).get();
            } else {
                try{
                    TextChannel clanTextChat = server.createTextChannelBuilder()
                            .setName(clan.getClanName())
                            .addPermissionOverwrite(server.getEveryoneRole(), discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_ONLY))
                            .addPermissionOverwrite(clanRole, discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_SEND))
                           // .setCategory()
                            .setTopic(clanRole.getMentionTag())
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

    public boolean isClanRole(Server server, Role clanRole){

        Clan clan = getClanByRole(server, clanRole);
        return clan != null;
    }

    public boolean isClanMember(Server server, User user){

        for(Role role : server.getRoles()){
            if(isClanRole(server, role)){
                if(role.hasUser(user)){
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteClan(Server server, Role clanRole){


        Role clanLeaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
        Role clanOfficerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);

        for(User member : clanRole.getUsers()){
            if(clanLeaderRole != null){
                clanLeaderRole.removeUser(member);
            }
            if(clanOfficerRole != null) {
                clanOfficerRole.removeUser(member);
            }
        }

        Clan clan = getClanByRole(server, clanRole);

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

    public void renameClan(Server server, User clanLeader, String newName) {

        Role clanRole = getClanRoleByLeader(server, clanLeader);
        renameClan(server, clanRole, newName);
    }

    public void renameClan(Server server, Role clanRole, String newName) {

        Clan clan = getClanByRole(server, clanRole);
        clan.setClanName(newName);
        updateClan(clan);

        clanRole.updateName(newName);
        if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
            server.getTextChannelById(clan.getClanTextChatId()).get().updateName(newName);
        }

    }
}
