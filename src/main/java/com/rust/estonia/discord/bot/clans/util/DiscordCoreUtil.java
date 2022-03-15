package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.permission.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.ExecutionException;

@Component
public class DiscordCoreUtil {

    private final Logger logger = LoggerFactory.getLogger(DiscordCoreUtil.class);


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
            PermissionType.SEND_MESSAGES,
            PermissionType.ATTACH_FILE,
            PermissionType.EMBED_LINKS,
            PermissionType.ADD_REACTIONS,
            PermissionType.USE_APPLICATION_COMMANDS
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

        PermissionsBuilder permissionsBuilder = new PermissionsBuilder().setDenied(PermissionType.READ_MESSAGES);

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


// - - GET - -


    // GET - ChannelCategory
    public ChannelCategory getCategory(Server server, long categoryId){

        if(server.getChannelCategoryById(categoryId).isPresent()){
            return server.getChannelCategoryById(categoryId).get();
        }
        return null;
    }

    // GET - TextChannel
    public TextChannel getTextChannel(Server server, long channelId){

        if(server.getTextChannelById(channelId).isPresent()){
            return server.getTextChannelById(channelId).get();
        }
        return null;
    }

    // GET - VoiceChannel
    public VoiceChannel getVoiceChannel(Server server, long channelId){

        if(server.getVoiceChannelById(channelId).isPresent()){
            return server.getVoiceChannelById(channelId).get();
        }
        return null;
    }

    // GET - Role
    public Role getRole(Server server, long roleId){

        if(server.getRoleById(roleId).isPresent()){
            return server.getRoleById(roleId).get();
        }
        return null;
    }

    // GET - User
    public User getUser(Server server, long userId){

        if(server.getMemberById(userId).isPresent()){
            return server.getMemberById(userId).get();
        }
        return null;
    }


// - - GET AS MENTION - -


    // GET AS MENTION - TextChannel
    public String getTextChannelAsMentionTag(Server server, long channelId){

        if(server.getTextChannelById(channelId).isPresent()){
            return server.getTextChannelById(channelId).get().getMentionTag();
        }
        return "";
    }

    // GET AS MENTION - Role
    public String getRoleAsMentionTag(Server server, long roleId){

        if(server.getRoleById(roleId).isPresent()){
            return server.getRoleById(roleId).get().getMentionTag();
        }
        return "";
    }

    // GET AS MENTION - User
    public String getUserAsMentionTag(Server server, long userId){

        if(server.getMemberById(userId).isPresent()){
            return server.getMemberById(userId).get().getMentionTag();
        }
        return "";
    }


// - - GET NAME - -


    // GET NAME - ChannelCategory
    public String getChannelCategoryName(Server server, long channelId){

        if(server.getChannelCategoryById(channelId).isPresent()){
            return server.getChannelCategoryById(channelId).get().getName();
        }
        return "(no-category)";
    }

    // GET NAME - TextChannel
    public String getTextChannelName(Server server, long channelId){

        if(server.getTextChannelById(channelId).isPresent()){
            return server.getTextChannelById(channelId).get().getName();
        }
        return "(no-text-channel)";
    }

    // GET NAME - VoiceChannel
    public String getVoiceChannelName(Server server, long channelId){

        if(server.getVoiceChannelById(channelId).isPresent()){
            return server.getVoiceChannelById(channelId).get().getName();
        }
        return "(no-voice-channel)";
    }

    // GET NAME - Role
    public String getRoleName(Server server, long roleId){

        if(server.getRoleById(roleId).isPresent()){
            return server.getRoleById(roleId).get().getName();
        }
        return "(no-role)";
    }

    // GET NAME - User
    public String getUserName(Server server, long userId){

        if(server.getMemberById(userId).isPresent()){
            return server.getMemberById(userId).get().getName();
        }
        return "(no-user)";
    }


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

// - - Util - -


    public long getIdFromMentionedTag(String mentionedTag){

        if(mentionedTag.length()== 22){
            String idString = mentionedTag.substring(3,21);
            return Long.parseLong(idString);
        }
        if(mentionedTag.length()== 21){
            String idString = mentionedTag.substring(2,20);
            return Long.parseLong(idString);
        }
        return 0;
    }
}
