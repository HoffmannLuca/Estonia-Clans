package com.rust.estonia.discord.bot.clans.listener;

import com.rust.estonia.discord.bot.clans.command.slash.CommandPermissionUtil;
import org.javacord.api.event.server.ServerChangeOwnerEvent;
import org.javacord.api.listener.server.ServerChangeOwnerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewServerOwnerUpdateListener implements ServerChangeOwnerListener {

    @Autowired
    private CommandPermissionUtil commandPermissionUtil;

    @Override
    public void onServerChangeOwner(ServerChangeOwnerEvent serverChangeOwnerEvent) {

        commandPermissionUtil.updateServerSlashCommandPermissions(serverChangeOwnerEvent.getServer());
    }
}
