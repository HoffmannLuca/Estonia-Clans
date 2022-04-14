package com.rust.estonia.discord.bot.clans.util;

import org.javacord.api.entity.permission.*;
import org.springframework.stereotype.Component;

@Component
public class DiscordUtil {

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

    public Permissions getPermissions(PermissionType[] permissionTypes){

        PermissionsBuilder permissionsBuilder = new PermissionsBuilder().setDenied(PermissionType.READ_MESSAGES).setDenied(PermissionType.SEND_MESSAGES);

        for (PermissionType permissionType : permissionTypes) {
            permissionsBuilder.setAllowed(permissionType);
        }
        return permissionsBuilder.build();
    }

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
