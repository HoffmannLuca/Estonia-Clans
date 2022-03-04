package com.rust.estonia.discord.bot.clans.command.component.menu;

import com.rust.estonia.discord.bot.clans.command.constant.MenuTag;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.stereotype.Component;

@Component
public class SelectColorMenuCommand implements MenuComponentCommand{

    @Override
    public String getName() { return MenuTag.SELECT_COLOR; }

    @Override
    public void performCommand(SelectMenuInteraction interaction, ComponentInteractionOriginalMessageUpdater messageUpdater, User user, Message message) {

        interaction.createImmediateResponder()
                .setContent(MenuTag.SELECT_COLOR)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}
