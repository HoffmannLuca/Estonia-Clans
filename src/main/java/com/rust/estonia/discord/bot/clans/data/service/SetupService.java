package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.data.model.Setup;
import com.rust.estonia.discord.bot.clans.data.repository.SetupRepository;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SetupService {

    @Autowired
    private SetupRepository repository;

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

    public String getServerPrefix(Server server){

        return getSetup(server).getPrefix();
    }

    public boolean setServerPrefix(Server server, String newPrefix){

        Setup setup = getSetup(server);
        setup.setPrefix(newPrefix);
        updateSetup(setup);
        return true;
    }

    public ChannelCategory getServerCategoryByCategoryTag(Server server, String categoryTag){

        long id = getSetup(server).getCategoryIdMap().get(categoryTag);
        if(server.getChannelCategoryById(id).isPresent()){
            return server.getChannelCategoryById(id).get();
        }
        return null;
    }

    public boolean setServerCategoryByCategoryTag(Server server, ChannelCategory category, String categoryTag){

        if(server.getChannelCategoryById(category.getId()).isPresent()){
            Setup setup = getSetup(server);
            HashMap<String, Long> idMap = setup.getCategoryIdMap();
            idMap.put(categoryTag, category.getId());
            setup.setCategoryIdMap(idMap);
            updateSetup(setup);
            return true;
        }
        return false;
    }

    public TextChannel getServerTextChannelByChannelTag(Server server, String channelTag){

        long id = getSetup(server).getTextChannelIdMap().get(channelTag);
        if(server.getTextChannelById(id).isPresent()){
            return server.getTextChannelById(id).get();
        }
        return null;
    }

    public boolean setServerTextChannelByChannelTag(Server server, TextChannel channel, String channelTag){

        if(server.getTextChannelById(channel.getId()).isPresent()){
            Setup setup = getSetup(server);
            HashMap<String, Long> idMap = setup.getTextChannelIdMap();
            idMap.put(channelTag, channel.getId());
            setup.setTextChannelIdMap(idMap);
            updateSetup(setup);
            return true;
        }
        return false;
    }

    public Role getServerRoleByRoleTag(Server server, String roleTag){

        long id = getSetup(server).getRoleIdMap().get(roleTag);
        if(server.getRoleById(id).isPresent()){
            return server.getRoleById(id).get();
        }
        return null;
    }

    public boolean setServerRoleByRoleTag(Server server, Role role, String roleTag){

        if(server.getRoleById(role.getId()).isPresent()){
            Setup setup = getSetup(server);
            HashMap<String, Long> idMap = setup.getRoleIdMap();
            idMap.put(roleTag, role.getId());
            setup.setRoleIdMap(idMap);
            updateSetup(setup);
            return true;
        }
        return false;
    }

    private Setup updateSetup(Setup setup){

        return repository.save(setup);
    }
}
