package com.rust.estonia.discord.bot.clans.listener;

import com.rust.estonia.discord.bot.clans.command.component.button.ButtonComponentCommand;
import com.rust.estonia.discord.bot.clans.command.component.menu.MenuComponentCommand;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class ComponentListener implements MessageComponentCreateListener {

    @Autowired
    private List<ButtonComponentCommand> buttonComponentCommands;

    @Autowired
    private List<MenuComponentCommand> menuComponentCommands;

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {

        ComponentInteractionOriginalMessageUpdater messageUpdater = event.getMessageComponentInteraction().createOriginalMessageUpdater();

        Server server = null;
        if(event.getInteraction().getServer().isPresent()){
            server = event.getInteraction().getServer().get();
        }

        String customId = event.getMessageComponentInteraction().getCustomId();
        User user = event.getInteraction().getUser();
        Message message = event.getMessageComponentInteraction().getMessage();
        EmbedBuilder errorEmbed = new EmbedBuilder().setColor(Color.RED).setTitle("ERROR");

        if(event.getMessageComponentInteraction().asButtonInteraction().isPresent()){

            ButtonInteraction interaction = event.getMessageComponentInteraction().asButtonInteraction().get();
            if(!tryToPerformButtonCommand(interaction, customId, messageUpdater, server, user, message)){

                errorEmbed.setDescription("**"+customId+"** is not a valid button command");
                interaction.createImmediateResponder()
                        .addEmbed(errorEmbed)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
            }

        } else if(event.getMessageComponentInteraction().asSelectMenuInteraction().isPresent()){

            SelectMenuInteraction interaction = event.getMessageComponentInteraction().asSelectMenuInteraction().get();
            if(!tryToPerformMenuCommand(interaction, customId, messageUpdater, server, user, message)){

                errorEmbed.setDescription("**"+customId+"** is not a valid menu command");
                interaction.createImmediateResponder()
                        .addEmbed(errorEmbed)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
            }
        }

    }

    private boolean tryToPerformButtonCommand(ButtonInteraction interaction, String commandName, ComponentInteractionOriginalMessageUpdater messageUpdater, Server server, User user, Message message){

        for(ButtonComponentCommand commandClass: buttonComponentCommands){
            if(commandClass.getName().equals(commandName)){
                if(commandClass.hasPermission(server, user)) {
                    commandClass.performCommand(interaction, messageUpdater, user,message);
                } else {

                    EmbedBuilder errorEmbed = new EmbedBuilder().setColor(Color.RED).setTitle("Permission error!").setDescription("You don't have permission to perform this button command");
                    interaction.createImmediateResponder()
                            .addEmbed(errorEmbed)
                            .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                            .respond();

                }
                return true;
            }
        }
        return false;
    }

    private boolean tryToPerformMenuCommand(SelectMenuInteraction interaction, String commandName, ComponentInteractionOriginalMessageUpdater messageUpdater, Server server, User user, Message message){

        for(MenuComponentCommand commandClass: menuComponentCommands){
            if(commandClass.getName().equals(commandName)) {
                if (commandClass.hasPermission(server, user)){
                    commandClass.performCommand(interaction, messageUpdater, user, message);
                } else {

                    EmbedBuilder errorEmbed = new EmbedBuilder().setColor(Color.RED).setTitle("Permission error!").setDescription("You don't have permission to perform this menu command");
                    interaction.createImmediateResponder()
                            .addEmbed(errorEmbed)
                            .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                            .respond();

                }
                return true;
            }
        }
        return false;
    }
}
