package com.rust.estonia.discord.bot.clans.command.server;

import com.rust.estonia.discord.bot.clans.command.type.ServerCommand;
import com.rust.estonia.discord.bot.clans.data.model.Setup;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.MessageUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class SetupCommand implements ServerCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private MessageUtil messageUtil;

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public void performCommand(Server server, TextChannel channel, User user, Message message, String[] commandArgs) {

        if (commandArgs.length > 1) {
            switch (commandArgs[1]) {

                case "prefix":
                    if (commandArgs.length > 2) {
                        setNewPrefix(server, channel, commandArgs[2]);
                    } else {
                        messageUtil.sendMessageAsEmbedWithColor(
                                channel,
                                new Color(255, 0, 0),
                                "Setup prefix error",
                                "No new prefix selected"
                        );
                    }
                    break;

                default:
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup error",
                            "Setup option __" + commandArgs[1] + "__ not available, choose from the following setup options: __info__ / __prefix__ / __role__ / __channel__"
                    );
                    break;
            }
        } else {
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255, 0, 0),
                    "Setup error",
                    "No setup option selected, choose from the following setup options: __info__ / __prefix__ / __role__ / __channel__"
            );
        }
    }

    private void setNewPrefix(Server server, TextChannel channel, String newPrefix){

        Setup setup = setupService.getSetup(server);

        if(newPrefix.length() == 1){
            messageUtil.sendMessageAsEmbed(channel, "Setup", "Old prefix: " + setup.getPrefix());
            setup.setPrefix(newPrefix);
            setupService.updateSetup(setup);
            messageUtil.sendMessageAsEmbed(channel, "Setup", "New prefix: " + setup.getPrefix());
        } else {
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255,0,0),
                    "Setup error",
                    "Select a prefix with only one character"
            );
        }
    }

}
