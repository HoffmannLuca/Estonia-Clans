package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;


@Component
public class PermissionUtil {

    @Autowired
    private SetupService setupService;

    @Autowired
    private MessageUtil messageUtil;

    private Logger logger = LoggerFactory.getLogger(PermissionUtil.class);

    public final String CLAN_CATEGORY = "clan-category";
    public final String[] categoryTags = {CLAN_CATEGORY};

    public final String ADMIN_COMMAND_CHANNEL = "admin-command-channel";
    public final String CLAN_COMMAND_CHANNEL = "clan-command-channel";
    public final String[] textChannelTags = {ADMIN_COMMAND_CHANNEL, CLAN_COMMAND_CHANNEL};

    public final String TEST_CHANNEL = "test-vc";
    public final String[] voiceChannelTags = {TEST_CHANNEL};

    public final String ADMIN_ROLE = "admin";
    public final String MODERATOR_ROLE = "moderator";
    public final String CLAN_LEADER_ROLE = "clan-leader";
    public final String CLAN_OFFICER_ROLE = "clan-officer";
    public final String[] roleTags = {ADMIN_ROLE, MODERATOR_ROLE, CLAN_LEADER_ROLE, CLAN_OFFICER_ROLE};

    private boolean isValidCategoryTag(String categoryTag){

        for (String tag : categoryTags) {
            if (tag.equalsIgnoreCase(categoryTag)) {
                return true;
            }
        }
        logger.error(categoryTag+" is not a valid category tag!");
        return false;
    }

    private boolean isValidTextChannelTag(String channelTag){

        for (String tag : textChannelTags) {
            if (tag.equalsIgnoreCase(channelTag)) {
                return true;
            }
        }
        logger.error(channelTag+" is not a valid text channel tag!");
        return false;
    }

    private boolean isValidVoiceChannelTag(String channelTag){

        for (String tag : voiceChannelTags) {
            if (tag.equalsIgnoreCase(channelTag)) {
                return true;
            }
        }
        logger.error(channelTag+" is not a valid voice channel tag!");
        return false;
    }

    private boolean isValidRoleTag(String roleTag){

        for (String tag : roleTags) {
            if (tag.equalsIgnoreCase(roleTag)) {
                return true;
            }
        }
        logger.error(roleTag+" is not a valid role tag!");
        return false;
    }

    public boolean isCorrectChannel(Server server, TextChannel channel, User user, String channelTag){

        return isCorrectChannel(server, channel, user, channelTag, false);
    }
    public boolean isCorrectChannel(Server server, TextChannel channel, User user, String channelTag, boolean sendErrorMessage){

        if(channelTag.equalsIgnoreCase(ADMIN_COMMAND_CHANNEL) && (server.isAdmin(user) || server.isOwner(user))){
            return true;
        }
        if(isValidTextChannelTag(channelTag)){
            TextChannel correctChannel = setupService.getServerTextChannelByChannelTag(server, channelTag);
            if(correctChannel != null){
                if(correctChannel.getId() == channel.getId()){
                    return true;
                } else if(sendErrorMessage){
                    sendErrorMessageForWrongChannel(channel);
                }
            } else if(sendErrorMessage){
                sendErrorMessageForNoChannelAssigned(channel, channelTag);
            }
        }
        return false;
    }

    public boolean isCorrectChannel(Server server, TextChannel channel, User user, String[] channelTagArray){

        return isCorrectChannel(server, channel, user, channelTagArray, false);
    }
    public boolean isCorrectChannel(Server server, TextChannel channel, User user, String[] channelTagArray, boolean sendErrorMessage){

        boolean isCorrectChannel = false;
        for (String channelTag : channelTagArray) {
            if (isCorrectChannel(server, channel, user, channelTag)) {
                isCorrectChannel = true;
                break;
            }
        }
        if(!isCorrectChannel && sendErrorMessage){
            sendErrorMessageForNoPermission(channel);
        }
        return isCorrectChannel;
    }

    public boolean userHasRole(Server server, TextChannel channel, User user, String roleTag){

        return userHasRole(server, channel, user, roleTag, false);
    }

    public boolean userHasRole(Server server, TextChannel channel, User user, String roleTag, boolean sendErrorMessage){

        if(roleTag.equalsIgnoreCase(ADMIN_ROLE) && (server.isAdmin(user) || server.isOwner(user))){
            return true;
        }
        if(isValidRoleTag(roleTag)){
            Role role = setupService.getServerRoleByRoleTag(server, roleTag);
            if(role != null){
                if(role.hasUser(user)){
                    return true;
                } else if(sendErrorMessage){
                    sendErrorMessageForNoPermission(channel);
                }
            } else if(sendErrorMessage){
                sendErrorMessageForNoRoleAssigned(channel, roleTag);
            }
        }
        return false;
    }

    public boolean userHasRole(Server server, TextChannel channel, User user, String[] roleTagArray){

        return userHasRole(server, channel, user, roleTagArray, false);
    }

    public boolean userHasRole(Server server, TextChannel channel, User user, String[] roleTagArray, boolean sendErrorMessage){

        boolean hasPermission = false;
        for (String roleTag : roleTagArray) {
            if (userHasRole(server, channel, user, roleTag)) {
                hasPermission = true;
                break;
            }
        }
        if(!hasPermission && sendErrorMessage){
            sendErrorMessageForNoPermission(channel);
        }
        return hasPermission;
    }

    public boolean assignCategoryToServerCategoryTag(Server server, ChannelCategory category, String categoryTag){

        if(isValidCategoryTag(categoryTag)){
            return setupService.setServerCategoryByCategoryTag(server, category, categoryTag);
        }
        return false;
    }

    public boolean assignTextChannelToServerChannelTag(Server server, TextChannel channel, String channelTag){

        if(isValidTextChannelTag(channelTag)){
            return setupService.setServerTextChannelByChannelTag(server, channel, channelTag);
        }
        return false;
    }

    public boolean assignVoiceChannelToServerChannelTag(Server server, VoiceChannel channel, String channelTag){

        if(isValidVoiceChannelTag(channelTag)){
            return setupService.setServerVoiceChannelByChannelTag(server, channel, channelTag);
        }
        return false;
    }

    public boolean assignRoleToServerRoleTag(Server server, Role role, String roleTag){

        if(isValidRoleTag(roleTag)){
            return setupService.setServerRoleByRoleTag(server, role, roleTag);
        }
        return false;
    }

    //Errors

    private void sendErrorMessageForNoPermission(TextChannel channel){
        messageUtil.sendMessageAsEmbedWithColor(
                channel,
                new Color(255,0,0),
                "Permission error",
                "You don't have permission to perform this command"
        );
    }

    private void sendErrorMessageForNoRoleAssigned(TextChannel channel, String permissionRoleName){
        messageUtil.sendMessageAsEmbedWithColor(
                channel,
                new Color(255,0,0),
                "Permission error",
                "No role has been setup for __" + permissionRoleName + "__ permissions"
        );
    }

    private void sendErrorMessageForNoChannelAssigned(TextChannel channel, String permissionChannelName){
        messageUtil.sendMessageAsEmbedWithColor(
                channel,
                new Color(255,0,0),
                "Permission error",
                "No channel has been setup for __" + permissionChannelName + "__ permissions"
        );
    }

    private void sendErrorMessageForWrongChannel(TextChannel channel){
        messageUtil.sendMessageAsEmbedWithColor(
                channel,
                new Color(255,0,0),
                "Permission error",
                "Wrong channel for this command"
        );
    }
}
