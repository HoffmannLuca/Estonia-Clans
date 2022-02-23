package com.rust.estonia.discord.bot.clans.command.server;

import com.rust.estonia.discord.bot.clans.command.type.ServerCommand;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.DiscordCoreUtil;
import com.rust.estonia.discord.bot.clans.util.EmbedFieldModel;
import com.rust.estonia.discord.bot.clans.util.MessageUtil;
import com.rust.estonia.discord.bot.clans.util.PermissionUtil;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class SetupCommand implements ServerCommand {

    @Autowired
    private PermissionUtil permissionUtil;

    @Autowired
    private DiscordCoreUtil discordCoreUtil;

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

        if(permissionUtil.isCorrectChannel(server, channel, user, permissionUtil.ADMIN_COMMAND_CHANNEL, true)){
            if(permissionUtil.userHasRole(server, channel, user, permissionUtil.ADMIN_ROLE, true)){

                if (commandArgs.length > 1) {
                    switch (commandArgs[1]) {

                        case "info":
                            showInfo(server, channel);
                            break;
                        case "prefix":
                            setNewPrefix(server, channel, commandArgs);
                            break;
                        case "role":
                            setRole(server, channel, message, commandArgs);
                            break;
                        case "channel":
                            setChannel(server, channel, message, commandArgs);
                            break;
                        case "category":
                            setCategory(server, channel, commandArgs);
                            break;

                        default:
                            messageUtil.sendMessageAsEmbedWithColor(
                                    channel,
                                    new Color(255, 0, 0),
                                    "Setup error",
                                    "Setup option __" + commandArgs[1] + "__ not available, choose from the following setup options: __info__ / __prefix__ / __role__ / __channel__ / __category__"
                            );
                            break;
                    }
                } else {
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup error",
                            "No setup option selected, choose from the following setup options: __info__ / __prefix__ / __role__ / __channel__ / __category__"
                    );
                }
            }
        }
    }

    private void showInfo(Server server, TextChannel channel) {

        List<EmbedFieldModel> embedFieldList = new ArrayList<>();
        embedFieldList.add(new EmbedFieldModel("General Settings", "-----------------------------------", false));
        embedFieldList.add(new EmbedFieldModel("Server Prefix ", setupService.getServerPrefix(server), true));

        HashMap<String, Long> roleIdMap = setupService.getServerRoleIdMap(server);
        if (roleIdMap.size() != 0) {
            embedFieldList.add(new EmbedFieldModel("Role Settings", "-----------------------------------", false));
            for (String key : roleIdMap.keySet()) {
                embedFieldList.add(new EmbedFieldModel(key, discordCoreUtil.getRoleAsMentionTag(server, roleIdMap.get(key)), true));
            }
        }

        HashMap<String, Long> textChannelIdMap = setupService.getServerTextChannelIdMap(server);
        if (textChannelIdMap.size() != 0) {
            embedFieldList.add(new EmbedFieldModel("Text Channel Settings", "-----------------------------------", false));
            for (String key : textChannelIdMap.keySet()) {
                embedFieldList.add(new EmbedFieldModel(key, discordCoreUtil.getTextChannelAsMentionTag(server, textChannelIdMap.get(key)), true));
            }
        }

        HashMap<String, Long> voiceChannelIdMap = setupService.getServerVoiceChannelIdMap(server);
        if (voiceChannelIdMap.size() != 0) {
            embedFieldList.add(new EmbedFieldModel("Voice Channel Settings", "-----------------------------------", false));
            for (String key : voiceChannelIdMap.keySet()) {
                embedFieldList.add(new EmbedFieldModel(key, discordCoreUtil.getVoiceChannelName(server, voiceChannelIdMap.get(key)), true));
            }
        }

        HashMap<String, Long> categoryIdMap = setupService.getServerCategoryIdMap(server);
        if (categoryIdMap.size() != 0) {
            embedFieldList.add(new EmbedFieldModel("Category Settings", "-----------------------------------", false));
            for (String key : categoryIdMap.keySet()) {
                embedFieldList.add(new EmbedFieldModel(key, discordCoreUtil.getChannelCategoryName(server, categoryIdMap.get(key)), true));
            }
        }

        messageUtil.sendMessageAsEmbedWithFields(
                channel,
                embedFieldList,
                "__Setup Info__",
                null,
                null,
                "Estonia Clans Bot"
        );
    }

    private void setNewPrefix(Server server, TextChannel channel, String[] commandArgs){

        String oldPrefix = setupService.getServerPrefix(server);
        if (commandArgs.length > 2) {
            if (commandArgs[2].length() == 1) {
                if (setupService.setServerPrefix(server, commandArgs[2])) {
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(0, 255, 0),
                            "Setup prefix success",
                            "Old prefix: __"+oldPrefix+"__ New prefix: __"+commandArgs[2]+"__"
                    );
                } else {
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup error",
                            "Prefix could not be set"
                    );
                }
            } else {
                messageUtil.sendMessageAsEmbedWithColor(
                        channel,
                        new Color(255, 0, 0),
                        "Setup error",
                        "Select a prefix with only one character"
                );
            }
        } else {
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255, 0, 0),
                    "Setup prefix error",
                    "No new prefix selected"
            );
        }
    }

    private void setRole(Server server, TextChannel channel, Message message, String[] commandArgs) {

        if (commandArgs.length > 2) {
            if (!message.getMentionedRoles().isEmpty()) {

                Role role = message.getMentionedRoles().get(0);
                if(permissionUtil.assignRoleToServerRoleTag(server, role, commandArgs[2])){
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(0, 255, 0),
                            "Setup role success",
                            "Role "+discordCoreUtil.getRoleAsMentionTag(server, role.getId())+" is now setup for __"+commandArgs[2]+"__"
                    );
                } else {
                    StringBuilder commandsString = new StringBuilder();
                    for(int i = 0; i<permissionUtil.roleTags.length; i++) {
                        if(i!=0) {
                            commandsString.append(" / ");
                        }
                        commandsString.append("__").append(permissionUtil.roleTags[i]).append("__");
                    }
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup role error",
                            "__"+commandArgs[2]+"__ is not a valid role tag, choose from these tags: "+commandsString
                    );
                }
            } else {
                messageUtil.sendMessageAsEmbedWithColor(
                        channel,
                        new Color(255, 0, 0),
                        "Setup role error",
                        "No role was selected"
                );
            }
        } else {
            StringBuilder commandsString = new StringBuilder();
            for(int i = 0; i<permissionUtil.roleTags.length; i++) {
                if(i!=0) {
                    commandsString.append(" / ");
                }
                commandsString.append("__").append(permissionUtil.roleTags[i]).append("__");
            }
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255, 0, 0),
                    "Setup role error",
                    "No role tag was selected, choose from these tags: "+commandsString
            );
        }
    }

    private void setChannel(Server server, TextChannel channel, Message message, String[] commandArgs) {

        if (commandArgs.length > 2) {
            if (!message.getMentionedChannels().isEmpty()) {

                TextChannel textChannel = message.getMentionedChannels().get(0);
                if(permissionUtil.assignTextChannelToServerChannelTag(server, textChannel, commandArgs[2])){
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(0, 255, 0),
                            "Setup text channel success",
                            "Text channel "+discordCoreUtil.getTextChannelAsMentionTag(server, textChannel.getId())+" is now setup for __"+commandArgs[2]+"__"
                    );
                } else {
                    StringBuilder commandsString = new StringBuilder();
                    for(int i = 0; i<permissionUtil.textChannelTags.length; i++) {
                        if(i!=0) {
                            commandsString.append(" / ");
                        }
                        commandsString.append("__").append(permissionUtil.textChannelTags[i]).append("__");
                    }
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup text channel error",
                            "__"+commandArgs[2]+"__ is not a valid text channel tag, choose from these tags: "+commandsString
                    );
                }
            } else if (commandArgs.length > 3) {

                long channelId = Long.parseLong(commandArgs[3]);
                VoiceChannel voiceChannel = discordCoreUtil.getVoiceChannel(server, channelId);
                if(permissionUtil.assignVoiceChannelToServerChannelTag(server, voiceChannel, commandArgs[2])){
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(0, 255, 0),
                            "Setup voice channel success",
                            "Voice channel __"+discordCoreUtil.getVoiceChannelName(server, channelId)+"__ is now setup for __"+commandArgs[2]+"__"
                    );
                } else {
                    StringBuilder commandsString = new StringBuilder();
                    for(int i = 0; i<permissionUtil.voiceChannelTags.length; i++) {
                        if(i!=0) {
                            commandsString.append(" / ");
                        }
                        commandsString.append("__").append(permissionUtil.voiceChannelTags[i]).append("__");
                    }
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup voice channel error",
                            "__"+commandArgs[2]+"__ is not a valid voice channel tag, choose from these tags: "+commandsString
                    );
                }
            } else {
                messageUtil.sendMessageAsEmbedWithColor(
                        channel,
                        new Color(255, 0, 0),
                        "Setup channel error",
                        "No channel was selected"
                );
            }
        } else {
            StringBuilder commandsString = new StringBuilder();
            for(int i = 0; i<permissionUtil.textChannelTags.length; i++) {
                if(i!=0) {
                    commandsString.append(" / ");
                }
                commandsString.append("__").append(permissionUtil.textChannelTags[i]).append("__");
            }
            for(int i = 0; i<permissionUtil.voiceChannelTags.length; i++) {
                if(i!=0 || !commandsString.isEmpty()) {
                    commandsString.append(" / ");
                }
                commandsString.append("__").append(permissionUtil.voiceChannelTags[i]).append("__");
            }
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255, 0, 0),
                    "Setup channel error",
                    "No channel tag was selected, choose from these tags: "+commandsString
            );
        }
    }

    private void setCategory(Server server, TextChannel channel, String[] commandArgs) {

        if (commandArgs.length > 2) {
            if (commandArgs.length > 3) {

                long categoryId = Long.parseLong(commandArgs[3]);
                ChannelCategory category = discordCoreUtil.getCategory(server, categoryId);
                if(permissionUtil.assignCategoryToServerCategoryTag(server, category, commandArgs[2])){
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(0, 255, 0),
                            "Setup category success",
                            "Category __"+category.getName()+"__ is now setup for __"+commandArgs[2]+"__"
                    );
                } else {
                    StringBuilder commandsString = new StringBuilder();
                    for(int i = 0; i<permissionUtil.categoryTags.length; i++) {
                        if(i!=0) {
                            commandsString.append(" / ");
                        }
                        commandsString.append("__").append(permissionUtil.categoryTags[i]).append("__");
                    }
                    messageUtil.sendMessageAsEmbedWithColor(
                            channel,
                            new Color(255, 0, 0),
                            "Setup category error",
                            "__"+commandArgs[2]+"__ is not a valid category tag, choose from these tags: "+commandsString
                    );
                }
            } else {
                messageUtil.sendMessageAsEmbedWithColor(
                        channel,
                        new Color(255, 0, 0),
                        "Setup category error",
                        "No category ID was provided"
                );
            }
        } else {
            StringBuilder commandsString = new StringBuilder();
            for(int i = 0; i<permissionUtil.categoryTags.length; i++) {
                if(i!=0) {
                    commandsString.append(" / ");
                }
                commandsString.append("__").append(permissionUtil.categoryTags[i]).append("__");
            }
            messageUtil.sendMessageAsEmbedWithColor(
                    channel,
                    new Color(255, 0, 0),
                    "Setup category error",
                    "No category tag was selected, choose from these tags: "+commandsString
            );
        }
    }
}
