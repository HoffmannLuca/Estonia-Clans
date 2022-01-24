package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.ExecutionException;

@Component
public class DiscordCoreUtil {

    private Logger logger = LoggerFactory.getLogger(DiscordCoreUtil.class);


// - - PERMISSIONS - -


    // CONSTANT - Templates

    public final PermissionType[] PERMISSION_EMPTY = new PermissionType[]{};
    public final PermissionType[] PERMISSION_VIEW_ONLY = new PermissionType[]{
            PermissionType.READ_MESSAGES,
            PermissionType.READ_MESSAGE_HISTORY
    };
    public final PermissionType[] PERMISSION_VIEW_SEND = new PermissionType[]{
            PermissionType.READ_MESSAGES,
            PermissionType.READ_MESSAGE_HISTORY,
            PermissionType.SEND_MESSAGES
    };
    public final PermissionType[] PERMISSION_VIEW_CONNECT = new PermissionType[]{
            PermissionType.READ_MESSAGES,
            PermissionType.CONNECT
    };
    public final PermissionType[] PERMISSION_VIEW_CONNECT_SPEAK = new PermissionType[]{
            PermissionType.READ_MESSAGES,
            PermissionType.CONNECT,
            PermissionType.SPEAK,
            PermissionType.USE_VOICE_ACTIVITY
    };
    public final PermissionType[] PERMISSION_VIEW_CONNECT_SPEAK_STREAM = new PermissionType[]{
            PermissionType.READ_MESSAGES,
            PermissionType.CONNECT,
            PermissionType.SPEAK,
            PermissionType.USE_VOICE_ACTIVITY,
            PermissionType.STREAM
    };

    // GET - Permissions

    public Permissions getPermissions(PermissionType[] permissionTypes){

        PermissionsBuilder permissionsBuilder = new PermissionsBuilder().setAllUnset();

        for (PermissionType permissionType : permissionTypes) {
            permissionsBuilder.setAllowed(permissionType);
        }
        return permissionsBuilder.build();
    }


// - - CREATE - -


    // CREATE - ChannelCategory

    public ChannelCategory createChannelCategory(Server server, String name){

        return createChannelCategory(server, name, getPermissions(PERMISSION_EMPTY));
    }

    public ChannelCategory createChannelCategory(Server server, String name, Permissions permissions){

        try {
            return new ChannelCategoryBuilder(server)
                    .setName(name)
                    .addPermissionOverwrite(server.getEveryoneRole(), permissions)
                    .create().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // CREATE - TextChannel

    public TextChannel createTextChannel(Server server, String name){

        return createTextChannel(server, name, null, getPermissions(PERMISSION_EMPTY), "");
    }
    public TextChannel createTextChannel(Server server, String name, String topic){

        return createTextChannel(server, name, null, getPermissions(PERMISSION_EMPTY), topic);
    }
    public TextChannel createTextChannel(Server server, String name, ChannelCategory category, boolean takeCategoryPermissions){

        return createTextChannel(server, name, category, takeCategoryPermissions, "");
    }
    public TextChannel createTextChannel(Server server, String name, ChannelCategory category, boolean takeCategoryPermissions, String topic){

        if(takeCategoryPermissions){
            try {
                return new ServerTextChannelBuilder(server)
                        .setName(name)
                        .setCategory(category)
                        .setTopic(topic)
                        .create().get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
                return null;
            }
        } else {
            return createTextChannel(server, name, category, getPermissions(PERMISSION_EMPTY), topic);
        }
    }
    public TextChannel createTextChannel(Server server, String name, Permissions permissions){

        return createTextChannel(server, name, null, permissions, "");
    }
    public TextChannel createTextChannel(Server server, String name, Permissions permissions, String topic){

        return createTextChannel(server, name, null, permissions, topic);
    }
    public TextChannel createTextChannel(Server server, String name, ChannelCategory category, Permissions permissions){

        return createTextChannel(server, name, category, permissions, "");
    }
    public TextChannel createTextChannel(Server server, String name, ChannelCategory category, Permissions permissions, String topic){

        try {
            return new ServerTextChannelBuilder(server)
                    .setName(name)
                    .setCategory(category)
                    .addPermissionOverwrite(server.getEveryoneRole(), permissions)
                    .setTopic(topic)
                    .create().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // CREATE - VoiceChannel

    public VoiceChannel createVoiceChannel(Server server, String name){

        return createVoiceChannel(server, name, getPermissions(PERMISSION_EMPTY), 0);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, int userLimit){

        return createVoiceChannel(server, name, getPermissions(PERMISSION_EMPTY), userLimit);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, ChannelCategory category, boolean takeCategoryPermissions){

        return createVoiceChannel(server, name, category, takeCategoryPermissions, 0);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, ChannelCategory category, boolean takeCategoryPermissions, int userLimit){

        if(takeCategoryPermissions){
            try {
                return new ServerVoiceChannelBuilder(server)
                        .setName(name)
                        .setCategory(category)
                        .setUserlimit(userLimit)
                        .create().get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
                return null;
            }
        } else {
            return createVoiceChannel(server, name, category, getPermissions(PERMISSION_EMPTY), userLimit);
        }
    }
    public VoiceChannel createVoiceChannel(Server server, String name, Permissions permissions){

        return createVoiceChannel(server, name, null, permissions, 0);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, Permissions permissions, int userLimit){

        return createVoiceChannel(server, name, null, permissions, userLimit);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, ChannelCategory category, Permissions permissions){

        return createVoiceChannel(server, name, category, permissions, 0);
    }
    public VoiceChannel createVoiceChannel(Server server, String name, ChannelCategory category, Permissions permissions, int userLimit){

        try {
            return new ServerVoiceChannelBuilder(server)
                    .setName(name)
                    .setCategory(category)
                    .addPermissionOverwrite(server.getEveryoneRole(), permissions)
                    .setUserlimit(userLimit)
                    .create().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // CREATE - Role

    public Role createRole(Server server, String name, boolean displaySeparately, boolean mentionable){

        return createRole(server, name, getPermissions(PERMISSION_EMPTY), null, displaySeparately, mentionable);
    }
    public Role createRole(Server server, String name, Color color, boolean displaySeparately, boolean mentionable){

        return createRole(server, name, getPermissions(PERMISSION_EMPTY), color, displaySeparately, mentionable);
    }
    public Role createRole(Server server, String name, Permissions permissions, boolean displaySeparately, boolean mentionable){

        return createRole(server, name, permissions, null, displaySeparately, mentionable);
    }
    public Role createRole(Server server, String name, Permissions permissions, Color color, boolean displaySeparately, boolean mentionable){

        try {
            return new RoleBuilder(server)
                    .setName(name)
                    .setPermissions(permissions)
                    .setColor(color)
                    .setDisplaySeparately(displaySeparately)
                    .setMentionable(mentionable)
                    .create().get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


//TODO - - GET - -


    //TODO GET - ChannelCategory

    //TODO GET - TextChannel

    //TODO GET - VoiceChannel

    //TODO GET - Role

    //TODO GET - User


//TODO - - GET AS MENTION - -


    //TODO GET AS MENTION - TextChannel

    //TODO GET AS MENTION - Role

    //TODO GET AS MENTION - User


//TODO - - GET NAME - -


    //TODO GET NAME - ChannelCategory

    //TODO GET NAME - TextChannel

    //TODO GET NAME - VoiceChannel

    //TODO GET NAME - Role

    //TODO GET NAME - User


//TODO - - UPDATE - -


    //TODO UPDATE - ChannelCategory

    //TODO UPDATE - TextChannel

    //TODO UPDATE - VoiceChannel

    //TODO UPDATE - Role


//TODO - - DELETE - -


    //TODO DELETE - ChannelCategory

    //TODO DELETE - TextChannel

    //TODO DELETE - VoiceChannel

    //TODO DELETE - Role
}
