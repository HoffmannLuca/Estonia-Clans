package com.rust.estonia.discord.bot.clans.command.slash.server;

import com.rust.estonia.discord.bot.clans.util.ApplicationCommandUtil;
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
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
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

    @Autowired
    private ApplicationCommandUtil applicationCommandUtil;

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

                                SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, OptionLabelTag.ROLE_TAG, "description", true,roleOptions),
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.ROLE, "description", true)

                        )),

                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, FIRST_OPTION_CHANNEL, "Assign a channel to a channel tag", Arrays.asList(

                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CHANNEL_SECOND_OPTION_TEXT, "Assign a text channel to a channel tag", Arrays.asList(

                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, OptionLabelTag.CHANNEL_TAG, "description", true,textChannelOptions),
                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, OptionLabelTag.CHANNEL, "description", true)

                                )),
                                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, CHANNEL_SECOND_OPTION_VOICE, "Assign a voice channel to a channel tag", Arrays.asList(

                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, OptionLabelTag.CHANNEL_TAG, "description", true,voiceChannelOptions),
                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, OptionLabelTag.CHANNEL, "description", true)

                                ))
                        )),

                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_CATEGORY, "Assign a category to a category tag", Arrays.asList(

                                SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, OptionLabelTag.CATEGORY_TAG, "description", true,categoryOptions),
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, OptionLabelTag.CATEGORY, "description", true)
                        ))
                )
        ).setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList.add(ApplicationCommandPermissions.create(server.getOwnerId(), ApplicationCommandPermissionType.USER, true));
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.ADMIN_ROLE, true);

        return permissionsList;
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

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.SETUP_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void showSetupInfo(SlashCommandInteraction interaction, Server server){

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setTitle("__Setup Info__")
                .setAuthor(interaction.getApi().getYourself());

        HashMap<String, Long> roleIdMap = setupService.getServerRoleIdMap(server);
        if (roleIdMap.size() != 0) {
            responseEmbedBuilder.addField("Role Settings", "-----------------------------------", false);
            for (String key : roleIdMap.keySet()) {
                responseEmbedBuilder.addField(key, discordCoreUtil.getRoleAsMentionTag(server, roleIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> textChannelIdMap = setupService.getServerTextChannelIdMap(server);
        if (textChannelIdMap.size() != 0) {
            responseEmbedBuilder.addField("Text Channel Settings", "-----------------------------------", false);
            for (String key : textChannelIdMap.keySet()) {
                responseEmbedBuilder.addField(key, discordCoreUtil.getTextChannelAsMentionTag(server, textChannelIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> voiceChannelIdMap = setupService.getServerVoiceChannelIdMap(server);
        if (voiceChannelIdMap.size() != 0) {
            responseEmbedBuilder.addField("Voice Channel Settings", "-----------------------------------", false);
            for (String key : voiceChannelIdMap.keySet()) {
                responseEmbedBuilder.addField(key, discordCoreUtil.getVoiceChannelName(server, voiceChannelIdMap.get(key)), true);
            }
        }

        HashMap<String, Long> categoryIdMap = setupService.getServerCategoryIdMap(server);
        if (categoryIdMap.size() != 0) {
            responseEmbedBuilder.addField("Category Settings", "-----------------------------------", false);
            for (String key : categoryIdMap.keySet()) {
                responseEmbedBuilder.addField(key, discordCoreUtil.getChannelCategoryName(server, categoryIdMap.get(key)), true);
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void setRole(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments){

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
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
                responseEmbedBuilder.setColor(Color.GREEN)
                        .setTitle("Setup success!")
                        .setDescription("**"+role.getMentionTag()+"** was assigned to the role tag: **"+roleTag+"**");
            }
        }

        applicationCommandUtil.updateServerSlashCommandPermissions(server);

        response.addEmbed(responseEmbedBuilder).respond();
    }


    private void setChannel(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments, String secondOption) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
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
                            responseEmbedBuilder.setColor(Color.GREEN)
                                    .setTitle("Setup success!")
                                    .setDescription(textChannelName + " was assigned to the text channel tag: **" + channelTag + "**");
                        }
                    } else {
                        responseEmbedBuilder.setColor(Color.RED)
                                .setTitle("Setup error!")
                                .setDescription("Please select a text channel");
                    }
                    break;

                case CHANNEL_SECOND_OPTION_VOICE:
                    if(channel.asServerVoiceChannel().isPresent()){
                        VoiceChannel voiceChannel = channel.asServerVoiceChannel().get();
                        String voiceChannelName = channel.asServerVoiceChannel().get().getName();
                        if(setupService.setServerVoiceChannelByChannelTag(server, voiceChannel, channelTag)) {
                            responseEmbedBuilder.setColor(Color.GREEN)
                                    .setTitle("Setup success!")
                                    .setDescription("**"+voiceChannelName + "** was assigned to the voice channel tag: **" + channelTag + "**");
                        }
                    } else {
                        responseEmbedBuilder.setColor(Color.RED)
                                .setTitle("Setup error!")
                                .setDescription("Please select a voice channel");
                    }
                    break;

                default:
                    System.out.println(secondOption);
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void setCategory(SlashCommandInteraction interaction, Server server, List<SlashCommandInteractionOption> commandArguments){

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
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
                    responseEmbedBuilder.setColor(Color.GREEN)
                            .setTitle("Setup success!")
                            .setDescription("**"+category.getName()+"** was assigned to the category tag: **"+categoryTag+"**");
                }
            } else {
                responseEmbedBuilder.setColor(Color.RED)
                        .setTitle("Setup error!")
                        .setDescription("Please select a category");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }
}
