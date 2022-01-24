package com.rust.estonia.discord.bot.clans.data.repository;

import com.rust.estonia.discord.bot.clans.data.model.Setup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SetupRepository extends MongoRepository<Setup, String> {

    Setup findByServerId(long serverId);

    List<Setup> findByServerName(String serverName);

}
