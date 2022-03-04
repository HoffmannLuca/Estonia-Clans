package com.rust.estonia.discord.bot.clans.command.slash.server;

import com.rust.estonia.discord.bot.clans.constant.*;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;

@Component
public class SetupServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

    //Option Tags
    private final String FIRST_OPTION_INFO = "info";
    private final String FIRST_OPTION_ROLE = "role";
    private final String FIRST_OPTION_CHANNEL = "channel";
    private final String FIRST_OPTION_CATEGORY = "category";

    private final String CHANNEL_SECOND_OPTION_TEXT = "text";
    private final String CHANNEL_SECOND_OPTION_VOICE = "voice";

    @Override
    public String getName() {
        return ServerSlashTag.SETUP_COMMAND;
    }

    @Override
    public SlashCommandBuilder getCommandBuilder() {

        List<SlashCommandOptionChoice> roleOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> textChannelOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> voiceChannelOptions = new ArrayList<>();
        List<SlashCommandOptionChoice> categoryOptions = new ArrayList<>();

        for(String option : RoleTag.roleTags){
            roleOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : TextChannelTag.textChannelTags){
            textChannelOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : VoiceChannelTag.voiceChannelTags){
            voiceChannelOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }
        for(String option : CategoryTag.categoryTags){
            categoryOptions.add(SlashCommandOptionChoice.create(option.toUpperCase(), option));
        }

        return SlashCommand.with(ServerSlashTag.SETUP_COMMAND, "A command dedicated to setting up the bot",
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
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_INFO:
                showSetupInfo(interaction, server);
                break;

            case FIRST_OPTION_ROLE:
                setRole(interaction, server, commandArguments);
                break;

            case FIRST_OPTION_CHANNEL:
                setChannel(interaction, server, commandArguments, secondOption);
                break;

            case FIRST_OPTION_CATEGORY:
                setCategory(interaction, server, commandArguments);
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

    private void setRole(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments){

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Setup error!")
                .setDescription("something went wrong..");

        String roleTag ="";
        Role role = null;

        for(SlashCommandInteractionOption commandArgument : commandArguments){
            if(commandArgument.getRoleValue().isPresent()){
                role = commandArgument.getRoleValue().get();
            } else if (commandArgument.getStringValue().isPresent()) {
                roleTag = commandArgument.getStringValue().get();
            }
        }

        if(role!=null && !roleTag.equals("")){
            if(setupService.setServerRoleByRoleTag(server, role, roleTag)){
                embedBuilder.setColor(Color.GREEN)
                        .setTitle("Setup success!")
                        .setDescription("**"+role.getMentionTag()+"** was assigned to the role tag: **"+roleTag+"**");
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(embedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }


    private void setChannel(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments, String secondOption) {

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Setup error!")
                .setDescription("something went wrong..");

        String channelTag ="";
        Channel channel = null;

        for(SlashCommandInteractionOption commandArgument : commandArguments){
            if(commandArgument.getChannelValue().isPresent()){
                channel = commandArgument.getChannelValue().get();;
            } else if (commandArgument.getStringValue().isPresent()) {
                channelTag = commandArgument.getStringValue().get();
            }
        }

        if(channel!=null && !channelTag.equals("")){

            switch (secondOption){

                case CHANNEL_SECOND_OPTION_TEXT:
                    if(channel.asServerTextChannel().isPresent()){
                        TextChannel textChannel = channel.asServerTextChannel().get();
                        String textChannelName = channel.asServerTextChannel().get().getMentionTag();
                        if(setupService.setServerTextChannelByChannelTag(server, textChannel, channelTag)) {
                            embedBuilder.setColor(Color.GREEN)
                                    .setTitle("Setup success!")
                                    .setDescription(textChannelName + " was assigned to the text channel tag: **" + channelTag + "**");
                        }
                    } else {
                        embedBuilder.setColor(Color.RED)
                                .setTitle("Setup error!")
                                .setDescription("Please select a text channel");
                    }
                    break;

                case CHANNEL_SECOND_OPTION_VOICE:
                    if(channel.asServerVoiceChannel().isPresent()){
                        VoiceChannel voiceChannel = channel.asServerVoiceChannel().get();
                        String voiceChannelName = channel.asServerVoiceChannel().get().getName();
                        if(setupService.setServerVoiceChannelByChannelTag(server, voiceChannel, channelTag)) {
                            embedBuilder.setColor(Color.GREEN)
                                    .setTitle("Setup success!")
                                    .setDescription("**"+voiceChannelName + "** was assigned to the voice channel tag: **" + channelTag + "**");
                        }
                    } else {
                        embedBuilder.setColor(Color.RED)
                                .setTitle("Setup error!")
                                .setDescription("Please select a voice channel");
                    }
                    break;

                default:
                    System.out.println(secondOption);
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(embedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void setCategory(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments){

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Setup error!")
                .setDescription("something went wrong..");

        String categoryTag ="";
        Channel channel = null;

        for(SlashCommandInteractionOption commandArgument : commandArguments){
            if(commandArgument.getChannelValue().isPresent()){
                channel = commandArgument.getChannelValue().get();
            } else if (commandArgument.getStringValue().isPresent()) {
                categoryTag = commandArgument.getStringValue().get();
            }
        }

        if(channel!=null && !categoryTag.equals("")){
            if(channel.asChannelCategory().isPresent()){
                ChannelCategory category = channel.asChannelCategory().get();
                if(setupService.setServerCategoryByCategoryTag(server, category, categoryTag)){
                    embedBuilder.setColor(Color.GREEN)
                            .setTitle("Setup success!")
                            .setDescription("**"+category.getName()+"** was assigned to the category tag: **"+categoryTag+"**");
                }
            } else {
                embedBuilder.setColor(Color.RED)
                        .setTitle("Setup error!")
                        .setDescription("Please select a category");
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(embedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}
