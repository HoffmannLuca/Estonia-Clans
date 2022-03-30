package com.rust.estonia.discord.bot.clans.command.slash.global;

import com.rust.estonia.discord.bot.clans.constant.GlobalSlashTag;
import org.javacord.api.entity.channel.TextChannel;
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
                .setTitle("__Clan Info__")
                .setDescription("here are a list of commands you can use with this bot")
                .setColor(Color.BLUE)
                .addField("/clan info", "Display information about a clan")
                .addField("/clan leave", "Leave the clan that you are a member of")
                .addField("/clan-officer invite", "Send a clan invite to a user (leaders can use this as well)")
                .addField("/clan-officer kick", "Kick a member of your clan (leaders can use this as well)")
                .addField("/clan-leader rename", "Give the clan a new name")
                .addField("/clan-leader promote", "Promote a member of your clan")
                .addField("/clan-leader demote", "Demote a member of your clan")
                .addField("/clan-leader disband", "Disband your clan or give leader to someone else");

        interaction.createImmediateResponder()
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .addEmbed(embedBuilder)
                .respond();
    }

}
