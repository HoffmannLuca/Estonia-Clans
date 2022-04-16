package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.data.model.Setup;
import com.rust.estonia.discord.bot.clans.data.repository.SetupRepository;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ApplicationCommandPermissionType;
import org.javacord.api.interaction.ApplicationCommandPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class SetupService {

    @Autowired
    private SetupRepository repository;

    private Logger logger = LoggerFactory.getLogger(SetupService.class);

    private Setup getSetup(Server server){

        long serverId = server.getId();
        String serverName = server.getName();

        if(serverId == 0){
            return null;
        }
        Setup setup = repository.findByServerId(serverId);

        if(setup == null){
            setup = repository.save(new Setup(serverId, serverName));
        }

        if(!serverName.equals(setup.getServerName())){
            setup.setServerName(serverName);
            repository.save(setup);
        }

        return setup;
    }

    public ChannelCategory getServerCategoryByCategoryTag(Server server, String categoryTag){

        return getServerCategoryByCategoryTag(server, categoryTag, false);
    }

    public ChannelCategory getServerCategoryByCategoryTag(Server server, String categoryTag, boolean createIfNotExist){

        Setup setup = getSetup(server);
        if(setup != null) {
            if(setup.getCategoryIdMap().containsKey(categoryTag)) {
                long id = setup.getCategoryIdMap().get(categoryTag);
                if (server.getChannelCategoryById(id).isPresent()) {
                    return server.getChannelCategoryById(id).get();
                }
            }
            if(createIfNotExist){
                try {
                    ChannelCategory category = new ChannelCategoryBuilder(server)
                            .setName(categoryTag.replace("-", " "))
                            .setAuditLogReason("Create category "+categoryTag.toUpperCase()+" because not yet assigned")
                            .create().get();

                    setServerCategoryByCategoryTag(server, category, categoryTag);
                    return category;
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public boolean setServerCategoryByCategoryTag(Server server, ChannelCategory category, String categoryTag){

        if(server.getChannelCategoryById(category.getId()).isPresent()){
            Setup setup = getSetup(server);
            if(setup != null) {
                HashMap<String, Long> idMap = setup.getCategoryIdMap();
                idMap.put(categoryTag, category.getId());
                setup.setCategoryIdMap(idMap);
                updateSetup(setup);
                return true;
            }
        }
        return false;
    }

    public TextChannel getServerTextChannelByChannelTag(Server server, String channelTag){

        return getServerTextChannelByChannelTag(server, channelTag, false, null);
    }

    public TextChannel getServerTextChannelByChannelTag(Server server, String channelTag, boolean createIfNotExist){

        return getServerTextChannelByChannelTag(server, channelTag, createIfNotExist, null);
    }

    public TextChannel getServerTextChannelByChannelTag(Server server, String channelTag, boolean createIfNotExist, ChannelCategory category){

        Setup setup = getSetup(server);
        if(setup != null) {
            if(setup.getTextChannelIdMap().containsKey(channelTag)) {
                long id = setup.getTextChannelIdMap().get(channelTag);
                if (server.getTextChannelById(id).isPresent()) {
                    return server.getTextChannelById(id).get();
                }
            }
            if(createIfNotExist){
                try {
                    ServerTextChannelBuilder builder = new ServerTextChannelBuilder(server)
                            .setName(channelTag)
                            .setAuditLogReason("Create "+channelTag.toUpperCase()+" text channel");

                    if(category!=null){
                        builder.setCategory(category);
                    }
                    TextChannel channel = builder.create().get();
                    setServerTextChannelByChannelTag(server, channel, channelTag);
                    return channel;
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public boolean setServerTextChannelByChannelTag(Server server, TextChannel channel, String channelTag){

        if(server.getTextChannelById(channel.getId()).isPresent()){
            Setup setup = getSetup(server);
            if(setup != null) {
                HashMap<String, Long> idMap = setup.getTextChannelIdMap();
                idMap.put(channelTag, channel.getId());
                setup.setTextChannelIdMap(idMap);
                updateSetup(setup);
                return true;
            }
        }
        return false;
    }

    public VoiceChannel getServerVoiceChannelByChannelTag(Server server, String channelTag){

        Setup setup = getSetup(server);
        if(setup != null) {
            if(setup.getVoiceChannelIdMap().containsKey(channelTag)) {
                long id = setup.getVoiceChannelIdMap().get(channelTag);
                if (server.getVoiceChannelById(id).isPresent()) {
                    return server.getVoiceChannelById(id).get();
                }
            }
        }
        return null;
    }

    public boolean setServerVoiceChannelByChannelTag(Server server, VoiceChannel channel, String channelTag){

        if(server.getVoiceChannelById(channel.getId()).isPresent()){
            Setup setup = getSetup(server);
            if(setup != null) {
                HashMap<String, Long> idMap = setup.getVoiceChannelIdMap();
                idMap.put(channelTag, channel.getId());
                setup.setVoiceChannelIdMap(idMap);
                updateSetup(setup);
                return true;
            }
        }
        return false;
    }

    public Role getServerRoleByRoleTag(Server server, String roleTag){

        Setup setup = getSetup(server);
        if(setup != null) {
            if(setup.getRoleIdMap().containsKey(roleTag)) {
                long id = setup.getRoleIdMap().get(roleTag);
                if (server.getRoleById(id).isPresent()) {
                    return server.getRoleById(id).get();
                }
            }
        }
        return null;
    }

    public boolean setServerRoleByRoleTag(Server server, Role role, String roleTag){

        if(server.getRoleById(role.getId()).isPresent()){
            Setup setup = getSetup(server);
            if(setup != null) {
                HashMap<String, Long> idMap = setup.getRoleIdMap();
                idMap.put(roleTag, role.getId());
                setup.setRoleIdMap(idMap);
                updateSetup(setup);
                return true;
            }
        }
        return false;
    }

    public boolean addClanRank(Server server, String name){

        if(!isClanRank(server, name)) {
            long newRank = getRankList(server, "clan").size();
            return setClanRank(server, newRank, name.toLowerCase());
        }
        return false;
    }

    private boolean setClanRank(Server server, long rank, String name){

        Setup setup = getSetup(server);

        if(setup!=null) {

            if(belowMaxRanks(server)) {
                HashMap<Long, String> clanRanks = setup.getClanRanks();

                if (clanRanks == null) {
                    clanRanks = new HashMap<>();
                }
                clanRanks.put(rank, name);

                setup.setClanRanks(clanRanks);
                updateSetup(setup);
                return true;
            }
        }
        return false;
    }

    public String getClanRank(Server server, long rank){

        Setup setup = getSetup(server);

        if(setup!=null) {
            int maxRanks = setup.getMaxClanRanks();

            if(rank<maxRanks && rank>=0) {
                HashMap<Long, String> clanRanks = setup.getClanRanks();

                if (!clanRanks.isEmpty()) {
                    if (clanRanks.get(rank) != null) {
                        return clanRanks.get(rank);
                    }
                }
            }
        }
        return "";
    }

    public List<SelectMenuOption> getRankSelectOptions(Server server, String rankType){

        List<SelectMenuOption> rankOptions = new ArrayList<>();

        for(String rank : getRankList(server, rankType)){
            rankOptions.add(SelectMenuOption.create(rank.toUpperCase(), rank));
        }
        return rankOptions;
    }

    public ArrayList<String> getRankList(Server server, String rankType) {

        ArrayList<String> allRanks= new ArrayList<>();
        Setup setup = getSetup(server);

        if(setup!=null) {
            HashMap<Long, String> allRanksMap = setup.getClanRanks();

            for (long rank : setup.getClanRanks().keySet()){
                String rankName = allRanksMap.get(rank);
                if(rankName!=null) {
                    allRanks.add(allRanksMap.get(rank));
                }
            }
        }

        return allRanks;
    }

    public EmbedBuilder addClanRankListAsEmbedField(Server server, EmbedBuilder embedBuilder){

        Setup setup = getSetup(server);

        if(setup!=null){
            HashMap<Long, String> allRanks = setup.getClanRanks();
            StringBuilder ranks = new StringBuilder();

            for (long i = allRanks.size()-1; i >= 0; i--) {
                ranks.append("**").append(i+1).append(" - ").append(allRanks.get(i).toUpperCase()).append("**").append("\n");
            }

            if(!ranks.toString().equals("")){
                embedBuilder.addField("__Ranks__", ranks.toString(), true);
            }
        }
        return embedBuilder;
    }

    public boolean isClanRank(Server server, String name){

        return getRankList(server, "clan").contains(name.toLowerCase());
    }

    public boolean belowMaxRanks(Server server){

        Setup setup = getSetup(server);

        if(setup!=null) {

            if(setup.getClanRanks()!=null) {
                long rankCount =setup.getClanRanks().size();
                int maxRanks = setup.getMaxClanRanks();
                return rankCount < maxRanks;
            }
        }
        return false;
    }

    public boolean switchClanRanks(Server server, long clanRank, long switchWith) {

        Setup setup = getSetup(server);

        if(setup!=null) {

            if (setup.getClanRanks() != null) {

                if (setup.getClanRanks().containsKey(clanRank) && setup.getClanRanks().containsKey(switchWith)) {
                    String temp = setup.getClanRanks().get(clanRank);
                    setup.getClanRanks().put(clanRank, setup.getClanRanks().get(switchWith));
                    setup.getClanRanks().put(switchWith, temp);

                    updateSetup(setup);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeClanRank(Server server, long rank){

        Setup setup = getSetup(server);

        if(setup!=null) {

            if (setup.getClanRanks() != null) {

                if(setup.getClanRanks().containsKey(rank)){

                    for(long i = rank; i<setup.getClanRanks().size(); i++){

                        if(setup.getClanRanks().containsKey(i+1)){
                            setup.getClanRanks().put(i, setup.getClanRanks().get(i+1));
                        } else {
                            setup.getClanRanks().remove(i);
                        }
                    }
                    updateSetup(setup);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean roleHasUserByRoleTag(Server server, User user, String roleTag){

        if(server!=null) {
            Role role = getServerRoleByRoleTag(server, roleTag);
            if (role != null) {
                return role.hasUser(user);
            }
        }
        return false;
    }

    public HashMap<String, Long> getServerRoleIdMap(Server server) {

        Setup setup = getSetup(server);
        if(setup != null) {
            return setup.getRoleIdMap();
        }
        return null;
    }

    public void setServerRoleIdMap(Server server, HashMap<String, Long> roleIdMap){

        Setup setup = getSetup(server);
        if(setup != null) {
            setup.setRoleIdMap(roleIdMap);
            updateSetup(setup);
        }
    }

    public HashMap<String, Long> getServerTextChannelIdMap(Server server) {

        Setup setup = getSetup(server);
        if(setup != null) {
            return setup.getTextChannelIdMap();
        }
        return null;
    }

    public void setServerTextChannelIdMap(Server server, HashMap<String, Long> textChannelIdMap){

        Setup setup = getSetup(server);
        if(setup != null) {
            setup.setTextChannelIdMap(textChannelIdMap);
            updateSetup(setup);
        }
    }

    public HashMap<String, Long> getServerVoiceChannelIdMap(Server server) {

        Setup setup = getSetup(server);
        if(setup != null) {
            return setup.getVoiceChannelIdMap();
        }
        return null;
    }

    public void setServerVoiceChannelIdMap(Server server, HashMap<String, Long> voiceChannelIdMap){

        Setup setup = getSetup(server);
        if(setup != null) {
            setup.setVoiceChannelIdMap(voiceChannelIdMap);
            updateSetup(setup);
        }
    }

    public HashMap<String, Long> getServerCategoryIdMap(Server server) {

        Setup setup = getSetup(server);
        if(setup != null) {
            return setup.getCategoryIdMap();
        }
        return null;
    }

    public void setServerCategoryIdMap(Server server, HashMap<String, Long> categoryIdMap){

        Setup setup = getSetup(server);
        if(setup != null) {
            setup.setCategoryIdMap(categoryIdMap);
            updateSetup(setup);
        }
    }

    public List<ApplicationCommandPermissions> addPermissionsBySetupRoleTag(List<ApplicationCommandPermissions> permissionsList, Server server, String roleTag, boolean permission) {

        Role role = getServerRoleByRoleTag(server, roleTag);
        if(role!=null){
            permissionsList.add(ApplicationCommandPermissions.create(role.getId(), ApplicationCommandPermissionType.ROLE, permission));
        }

        return permissionsList;
    }

    private Setup updateSetup(Setup setup){

        return repository.save(setup);
    }
}
