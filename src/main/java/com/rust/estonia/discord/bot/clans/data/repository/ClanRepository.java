package com.rust.estonia.discord.bot.clans.data.repository;

import com.rust.estonia.discord.bot.clans.data.model.Clan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClanRepository extends MongoRepository<Clan, String> {

    List<Clan> findByServerId(long serverId);

    Clan findByServerIdAndClanLeaderId(long serverId, long clanLeaderId);

    Clan findByServerIdAndClanRoleId(long serverId, long clanRoleId);
}
