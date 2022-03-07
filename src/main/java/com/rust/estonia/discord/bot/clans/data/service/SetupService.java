package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.data.model.Setup;
import com.rust.estonia.discord.bot.clans.data.repository.SetupRepository;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ChannelCategoryBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ApplicationCommandPermissionType;
import org.javacord.api.interaction.ApplicationCommandPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        Setup setup = getSetup(server);
        if(setup != null) {
            if(setup.getTextChannelIdMap().containsKey(channelTag)) {
                long id = setup.getTextChannelIdMap().get(channelTag);
                if (server.getTextChannelById(id).isPresent()) {
                    return server.getTextChannelById(id).get();
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
