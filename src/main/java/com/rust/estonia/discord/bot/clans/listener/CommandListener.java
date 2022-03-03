package com.rust.estonia.discord.bot.clans.listener;

import com.rust.estonia.discord.bot.clans.command.server.ServerCommand;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.MessageUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.List;

@Component
public class CommandListener implements MessageCreateListener {

    @Autowired
    private List<ServerCommand> commandList;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private SetupService setupService;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        TextChannel channel = event.getChannel();
        Message message = event.getMessage();
        String messageContent = message.getContent();

        if (event.isServerMessage()) {
            Server server = event.getServer().get();
            String prefix = setupService.getServerPrefix(server);

            if(messageContent.startsWith(prefix)) {
                User user = event.getMessageAuthor().asUser().get();
                String[] commandArgs = messageContent.substring(1).split(" ");
                if (!tryToPerformServerCommand(commandArgs, user, channel, message, server)) {
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255,0,0),
                            "Error",
                            "__" + commandArgs[0]+ "__ is not a valid command"
                    );
                }
            }
        }else if (event.isPrivateMessage()) {
            if (messageContent.equalsIgnoreCase("!ping")) {
                channel.sendMessage("Pong!");
            }
        }
    }

    private boolean tryToPerformServerCommand(String[] commandArgs, User user, TextChannel channel, Message message, Server server){

        for(ServerCommand commandClass: commandList){
            if(commandClass.getName().equals(commandArgs[0])){
                commandClass.performCommand(server, channel, user, message, commandArgs);
                return true;
            }
        }
        return false;
    }
}
