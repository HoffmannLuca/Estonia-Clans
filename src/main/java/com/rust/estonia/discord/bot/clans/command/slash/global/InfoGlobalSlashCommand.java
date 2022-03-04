package com.rust.estonia.discord.bot.clans.command.slash.global;

import com.rust.estonia.discord.bot.clans.constant.GlobalSlashTag;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class InfoGlobalSlashCommand implements GlobalSlashCommand {

    @Override
    public String getName() {
        return GlobalSlashTag.INFO_COMMAND;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(GlobalSlashTag.INFO_COMMAND, "An info command");
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel) {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(interaction.getApi().getYourself())
                .setTitle("__Info__")
                .setColor(Color.BLUE)
                .addField("Bot Info", "Display information about Bot features")
                .addField("Clan Info", "Display information about Clan features")
                .addField("Event Info", "Display information about Event features");

        interaction.createImmediateResponder()
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .addEmbed(embedBuilder)
                .addComponents(
                        ActionRow.of(
                                Button.primary("info-bot", "Bot Info"),
                                Button.primary("info-clan", "Clan Info"),
                                Button.primary("info-event", "Event Info")
                        )
                )
                .respond();
    }

}
