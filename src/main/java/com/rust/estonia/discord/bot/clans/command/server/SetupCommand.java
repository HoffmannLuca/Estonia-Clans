package com.rust.estonia.discord.bot.clans.command.server;

import com.rust.estonia.discord.bot.clans.command.type.ServerCommand;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class SetupCommand implements ServerCommand {

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public void performCommand(Server server, TextChannel channel, User user, Message message, String[] commandArgs) {
        channel.sendMessage("test");
    }

}
