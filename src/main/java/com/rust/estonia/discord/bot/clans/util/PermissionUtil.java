package com.rust.estonia.discord.bot.clans.util;

import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
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

    public final String ADMIN_COMMAND_CHANNEL = "admin-command-channel";
    public final String CLAN_COMMAND_CHANNEL = "clan-command-channel";

    public final String ADMIN_ROLE = "admin";
    public final String MODERATOR_ROLE = "moderator";
    public final String CLAN_LEADER_ROLE = "clan-leader";
    public final String CLAN_OFFICER_ROLE = "clan-officer";

    private boolean isValidChannelTag(String channelTag){
        if(channelTag.equals(ADMIN_COMMAND_CHANNEL)
                || channelTag.equals(CLAN_COMMAND_CHANNEL)){
            return true;
        } else {
            logger.error(channelTag+" is not a valid channel tag!");
            return false;
        }
    }

    private boolean isValidRoleTag(String roleTag){
        if(roleTag.equals(ADMIN_ROLE)
                || roleTag.equals(MODERATOR_ROLE)
                || roleTag.equals(CLAN_LEADER_ROLE)
                || roleTag.equals(CLAN_OFFICER_ROLE)){
            return true;
        } else {
            logger.error(roleTag+" is not a valid role tag");
            return false;
        }
    }

    public boolean isCorrectChannel(Server server, TextChannel channel, String channelTag){

        return isCorrectChannel(server, channel, channelTag, false);
    }
    public boolean isCorrectChannel(Server server, TextChannel channel, String channelTag, boolean sendErrorMessage){

        if(isValidChannelTag(channelTag)){
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

    public boolean userHasRole(Server server, TextChannel channel, User user, String roleTag){

        return userHasRole(server, channel, user, roleTag, false);
    }

    public boolean userHasRole(Server server, TextChannel channel, User user, String roleTag, boolean sendErrorMessage){

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

    public void assignChannelToServerChannelTag(Server server, TextChannel channel, String channelTag){

        if(isValidChannelTag(channelTag)){
            if(!setupService.setServerTextChannelByChannelTag(server, channel, channelTag)){
                logger.error("no such text channel available in this server");
            }
        }
    }

    public void assignRoleToServerRoleTag(Server server, Role role, String roleTag){

        if(isValidRoleTag(roleTag)){
            if(!setupService.setServerRoleByRoleTag(server, role, roleTag)){
                logger.error("no such role available in this server");
            }
        }
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
