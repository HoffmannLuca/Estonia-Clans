package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.constant.CategoryTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.data.model.Clan;
import com.rust.estonia.discord.bot.clans.data.repository.ClanRepository;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;
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

    public Role getClanRoleByMember(Server server, User member){

        for(Role role : server.getRoles()){
            if(isClanRole(server, role)){
                if(role.hasUser(member)){
                    return role;
                }
            }
        }
        return null;
    }

    public TextChannel getClanTextChat(Server server, Role clanRole){

        Clan clan = getClanByRole(server, clanRole);

        if(clan != null){
            if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
                return server.getTextChannelById(clan.getClanTextChatId()).get();
            } else {
                try{
                    ServerTextChannelBuilder channelBuilder = server.createTextChannelBuilder()
                            .setName(clan.getClanName())
                            .addPermissionOverwrite(server.getEveryoneRole(), discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_ONLY))
                            .addPermissionOverwrite(clanRole, discordCoreUtil.getPermissions(discordCoreUtil.PERMISSION_VIEW_SEND))
                            .setTopic(clanRole.getMentionTag())
                            .setAuditLogReason("Create clan text chat");

                    ChannelCategory category = setupService.getServerCategoryByCategoryTag(server, getClanCategoryTag(clan.getClanRank()), true);
                    if(category!=null){
                        channelBuilder.setCategory(category);
                    }
                    TextChannel clanTextChat = channelBuilder.create().get();
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

        return getClanRoleByMember(server, user) != null;
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

    public EmbedBuilder getClanInfoEmbedBuilder(Server server, Role clanRole) {

        EmbedBuilder clanInfoEmbedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("Clan info __"+clanRole.getName()+"__")
                .setDescription("");

        if(server!=null){
            Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
            Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);

            String clanRankTag = getClanCategoryTag(getClanRank(server, clanRole))
                    .toUpperCase().replace("-", " ");

            if(!clanRankTag.equals("")){

                //remove `s` (last character)
                clanRankTag = Optional.of(clanRankTag)
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.substring(0, s.length() - 1))
                        .orElse(clanRankTag);

                clanInfoEmbedBuilder.addField("Clan rank", "**"+clanRankTag+"**", true);
            }
            clanInfoEmbedBuilder.addField("Clan role", clanRole.getMentionTag(), true);
            Clan clan = getClanByRole(server, clanRole);

            if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()){
                clanInfoEmbedBuilder.addField("Clan chat", server.getTextChannelById(clan.getClanTextChatId()).get().getMentionTag(),true);
            }

            if(leaderRole!=null && officerRole!=null){
                boolean clanLeaderIsSet=false;
                StringBuilder officerInfo = new StringBuilder();
                StringBuilder memberInfo = new StringBuilder();

                for (User member : clanRole.getUsers()){
                    if(!clanLeaderIsSet && leaderRole.hasUser(member)){
                        clanInfoEmbedBuilder.addField("Leader", member.getName()+" "+member.getMentionTag());
                        clanLeaderIsSet=true;
                    } else if (officerRole.hasUser(member)){
                        officerInfo.append(member.getName()).append(member.getMentionTag()).append("\n");
                    } else {
                        memberInfo.append(member.getName()).append(member.getMentionTag()).append("\n");
                    }
                }
                if(!officerInfo.toString().equals("")){
                    clanInfoEmbedBuilder.addField("Officer", officerInfo.toString());
                }
                if(!memberInfo.toString().equals("")){
                    clanInfoEmbedBuilder.addField("Member", memberInfo.toString());
                }
            }
        }

        clanInfoEmbedBuilder.setFooter("Clan-ID["+clanRole.getId()+"]");
        return clanInfoEmbedBuilder;
    }

    public Role getClanRoleFromEmbedFooter(Server server, Embed embed){

        if(server.getRoleById(getClanRoleIdFromEmbedFooter(embed)).isPresent()){
            Role role = server.getRoleById(getClanRoleIdFromEmbedFooter(embed)).get();
            if(isClanRole(server, role)){
                return role;
            }
        }
        return null;
    }

    private long getClanRoleIdFromEmbedFooter(Embed embed){

        if(embed!=null) {

            if(embed.getFooter().isPresent()){
                EmbedFooter footer = embed.getFooter().get();

                if(footer.getText().isPresent()) {
                    String text = footer.getText().get();
                    String clanId = text.replace("Clan-ID[", "").replace("]", "");

                    if (!clanId.equals("")) {
                        return Long.parseLong(clanId);
                    }
                }
            }
        }
        return 0;
    }

    public int getClanRank(Server server, Role clanRole) {

        Clan clan = getClanByRole(server, clanRole);
        if(clan!=null){
            return clan.getClanRank();
        }
        return -1;
    }

    public String getClanCategoryTag(int rank){

        String clanCategoryTag="";

        switch (rank){

            case 0:
                clanCategoryTag = CategoryTag.NEW_CLAN_CATEGORY;
                break;

            case 1:
                clanCategoryTag = CategoryTag.ESTABLISHED_CLAN_CATEGORY;
                break;

            case 2:
                clanCategoryTag = CategoryTag.LEGACY_CLAN_CATEGORY;
                break;

            case 3:
                clanCategoryTag = CategoryTag.TOP_CLAN_CATEGORY;

        }
        return clanCategoryTag;
    }

    private boolean setClanCategory(Server server, Clan clan, int newRank){

        if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()) {

            ChannelCategory clanCategory = setupService.getServerCategoryByCategoryTag(server, getClanCategoryTag(newRank), true);

            if(clanCategory!=null){
                if(server.getTextChannelById(clan.getClanTextChatId()).isPresent()) {
                    server.getTextChannelById(clan.getClanTextChatId()).get().updateCategory(clanCategory);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean promoteClan(Server server, Role clanRole){

        Clan clan = getClanByRole(server, clanRole);
        if(clan!=null){
            if(clan.getClanRank()<3){
                if(setClanCategory(server, clan, (clan.getClanRank())+1)) {
                    clan.setClanRank(clan.getClanRank() + 1);
                    updateClan(clan);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean demoteClan(Server server, Role clanRole){

        Clan clan = getClanByRole(server, clanRole);
        if(clan!=null){
            if(clan.getClanRank()>0){
                if(setClanCategory(server, clan, (clan.getClanRank())-1)) {
                    clan.setClanRank(clan.getClanRank() - 1);
                    updateClan(clan);
                    return true;
                } else {
                    System.out.println("demoteClan() - setClanCategory()==false");
                }
            } else {
                System.out.println("demoteClan() - !clan.getRank()>0");
            }
        } else {
            System.out.println("demoteClan() - clan == null");
        }
        return false;
    }

    public boolean setNewClanLeader(Server server, Role clanRole, User newLeader) {

        Clan clan = getClanByRole(server, clanRole);
        if(clan!=null){
            if(server.getMemberById(clan.getClanLeaderId()).isPresent()){
                User oldLeader = server.getMemberById(clan.getClanLeaderId()).get();
                Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);
                Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                if(officerRole!=null && leaderRole!=null){

                    leaderRole.removeUser(oldLeader);
                    leaderRole.addUser(newLeader);

                    officerRole.removeUser(newLeader);
                    officerRole.addUser(oldLeader);

                    clan.setClanLeaderId(newLeader.getId());
                    updateClan(clan);
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
}
