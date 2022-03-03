package com.rust.estonia.discord.bot.clans.command.slash.server;

import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SetupServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    private final String COMMAND_NAME= "setup";

    //Option Tags
    private final String FIRST_OPTION_INFO = "info";
    private final String FIRST_OPTION_ROLE = "role";
    private final String FIRST_OPTION_CHANNEL = "channel";
    private final String FIRST_OPTION_CATEGORY = "category";

    private final String CHANNEL_SECOND_OPTION_TEXT = "text";
    private final String CHANNEL_SECOND_OPTION_VOICE = "voice";

    //Argument Tags
    private final String ADMIN_ROLE = "admin";
    private final String MODERATOR_ROLE = "moderator";
    private final String CLAN_LEADER_ROLE = "clan_leader";
    private final String CLAN_OFFICER_ROLE = "clan_officer";
    private final String[] roleTags = {ADMIN_ROLE, MODERATOR_ROLE, CLAN_LEADER_ROLE, CLAN_OFFICER_ROLE};


    private final String ADMIN_COMMAND_CHANNEL = "admin_command_channel";
    private final String CLAN_COMMAND_CHANNEL = "clan_command_channel";
    private final String CLAN_LOG_CHANNEL = "clan-log_channel";
    private final String[] textChannelTags = {ADMIN_COMMAND_CHANNEL, CLAN_COMMAND_CHANNEL, CLAN_LOG_CHANNEL};

    private final String TEST_CHANNEL = "test_vc";
    private final String[] voiceChannelTags = {TEST_CHANNEL};

    private final String CLAN_CATEGORY = "clan_category";
    private final String[] categoryTags = {CLAN_CATEGORY};





    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {

        List<SlashCommandOptionChoice> roleOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> textChannelOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> voiceChannelOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> categoryOptions = new ArrayList<>();

        for(String option : roleTags){
            roleOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : textChannelTags){
            textChannelOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : voiceChannelTags){
            voiceChannelOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : categoryTags){
            categoryOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }

        return SlashCommand.with(COMMAND_NAME, "A command dedicated to setting up the bot",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_INFO, "Show information about the bot"),

                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_ROLE, "Assign a role to a role tag", Arrays.asList(

                                SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "ROLE-TAG", "The permission to allow", true,roleOptions),
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "ROLE", "The user which permissions should be changed", true)

                        )),

                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, FIRST_OPTION_CHANNEL, "Assign a channel to a channel tag", Arrays.asList(

                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CHANNEL_SECOND_OPTION_TEXT, "Assign a role to a role tag", Arrays.asList(

                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "CHANNEL-TAG", "The permission to allow", true,textChannelOptions),
                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "The user which permissions should be changed", true)

                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CHANNEL_SECOND_OPTION_VOICE, "Assign a role to a role tag", Arrays.asList(

                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "CHANNEL-TAG", "The permission to allow", true,voiceChannelOptions),
                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "The user which permissions should be changed", true)

                                ))
                        )),

                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_CATEGORY, "Assign a category to a category tag", Arrays.asList(

                                SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "CATEGORY-TAG", "The permission to allow", true,categoryOptions),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CATEGORY", "The user which permissions should be changed", true)
                        ))
                )
        ).setDefaultPermission(true);
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, SlashCommandInteractionOption firstOption, SlashCommandInteractionOption secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption.getName()){

            case FIRST_OPTION_INFO:
                showSetupInfo(interaction, server);
                break;

            case FIRST_OPTION_ROLE:
                setRole(interaction);
                break;

            case FIRST_OPTION_CHANNEL:
                setChannel(interaction);
                break;

            case FIRST_OPTION_CATEGORY:
                setCategory(interaction);
                break;
        }

    }

    private void showSetupInfo(SlashCommandInteraction interaction, Server server){

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("__Setup Info__")
                .setAuthor(interaction.getApi().getYourself());

        HashMap<String, Long> roleIdMap = setupService.getServerRoleIdMap(server);
        if (roleIdMap.size() != 0) {
            embedBuilder.addField("Role Settings", "-----------------------------------", false);
            for (String key : roleIdMap.keySet()) {
                embedBuilder.addField(key, discordCoreUtil.getRoleAsMentionTag(server, roleIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> textChannelIdMap = setupService.getServerTextChannelIdMap(server);
        if (textChannelIdMap.size() != 0) {
            embedBuilder.addField("Text Channel Settings", "-----------------------------------", false);
            for (String key : textChannelIdMap.keySet()) {
                embedBuilder.addField(key, discordCoreUtil.getTextChannelAsMentionTag(server, textChannelIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> voiceChannelIdMap = setupService.getServerVoiceChannelIdMap(server);
        if (voiceChannelIdMap.size() != 0) {
            embedBuilder.addField("Voice Channel Settings", "-----------------------------------", false);
            for (String key : voiceChannelIdMap.keySet()) {
                embedBuilder.addField(key, discordCoreUtil.getVoiceChannelName(server, voiceChannelIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> categoryIdMap = setupService.getServerCategoryIdMap(server);
        if (categoryIdMap.size() != 0) {
            embedBuilder.addField("Category Settings", "-----------------------------------", false);
            for (String key : categoryIdMap.keySet()) {
                embedBuilder.addField(key, discordCoreUtil.getChannelCategoryName(server, categoryIdMap.get(key)), true);
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(embedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void setRole(SlashCommandInteraction interaction){

        interaction.createImmediateResponder()
                .setContent("SETUP ROLE")
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .addComponents(
                        ActionRow.of(
                                Button.primary("setup", "Setup"),
                                Button.danger("delete", "Cancel")
                        )
                )
                .respond();
    }

    private void setChannel(SlashCommandInteraction interaction){

        interaction.createImmediateResponder()
                .setContent("SETUP CHANNEL")
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .addComponents(
                        ActionRow.of(
                                Button.primary("setup", "Setup"),
                                Button.danger("delete", "Cancel")
                        )
                )
                .respond();
    }

    private void setCategory(SlashCommandInteraction interaction){

        interaction.createImmediateResponder()
                .setContent("SETUP CATEGORY")
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .addComponents(
                        ActionRow.of(
                                Button.primary("setup", "Setup"),
                                Button.danger("delete", "Cancel")
                        )
                )
                .respond();
    }
}
