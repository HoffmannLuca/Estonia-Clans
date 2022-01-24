package com.rust.estonia.discord.bot.clans.data.service;

import com.rust.estonia.discord.bot.clans.data.model.Setup;
import com.rust.estonia.discord.bot.clans.data.repository.SetupRepository;
import org.javacord.api.entity.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupService {

    @Autowired
    private SetupRepository repository;

    public Setup getSetup(Server server){

        long serverId = server.getId();
        String serverName = server.getName();

        if(serverId == 0){
            return null;
        }
        Setup setup = repository.findByServerId(serverId);

        if(setup == null){
            setup = repository.save(new Setup(serverId, serverName));
        }

        if(serverName != setup.getServerName()){
            setup.setServerName(serverName);
            repository.save(setup);
        }

        return setup;
    }
    public Setup updateSetup(Setup setup){

        return repository.save(setup);
    }
}
