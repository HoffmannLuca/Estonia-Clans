package com.rust.estonia.discord.bot.clans.command.server;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

@Deprecated
public interface ServerCommand {

    @Deprecated
    String getName();

    @Deprecated
    void performCommand( Server server, TextChannel channel, User user, Message message, String[] commandArgs);
}
