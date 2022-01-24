package com.rust.estonia.discord.bot.clans.command.type;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public interface ServerCommand {

    String getName();

    void performCommand( Server server, TextChannel channel, User user, Message message, String[] commandArgs);
}
