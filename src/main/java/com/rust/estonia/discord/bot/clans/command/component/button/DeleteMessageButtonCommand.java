package com.rust.estonia.discord.bot.clans.command.component.button;

import com.rust.estonia.discord.bot.clans.command.constant.ButtonTag;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.springframework.stereotype.Component;

@Component
public class DeleteMessageButtonCommand implements ButtonComponentCommand {

    @Override
    public String getName() { return ButtonTag.DELETE_MESSAGE; }

    @Override
    public void performCommand(ButtonInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {

        message.delete();
    }
}
