package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.OptionLabelTag;
import com.rust.estonia.discord.bot.clans.constant.RoleTag;
import com.rust.estonia.discord.bot.clans.constant.ServerSlashTag;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.InteractionCallbackDataFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ClanAdminServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private ClanService clanService;

    @Autowired
    private SetupService setupService;

    private final String FIRST_OPTION_CREATE = "create";
    private final String FIRST_OPTION_DISBAND = "disband";
    private final String FIRST_OPTION_RENAME = "rename";
    private final String FIRST_OPTION_PROMOTE = "promote";
    private final String FIRST_OPTION_DEMOTE = "demote";

    @Override
    public String getName() { return ServerSlashTag.CLAN_ADMIN_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_ADMIN_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_CREATE, "description",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.LEADER, "description", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, OptionLabelTag.NAME, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DISBAND, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_RENAME, "description",
                                Arrays.asList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true),
                                        SlashCommandOption.create(SlashCommandOptionType.STRING, OptionLabelTag.NAME, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_PROMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true)
                                )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_DEMOTE, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true)
                                )
                        )

                )
        ).setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList.add(ApplicationCommandPermissions.create(server.getOwnerId(), ApplicationCommandPermissionType.USER, true));
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.ADMIN_ROLE, true);
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.MODERATOR_ROLE, true);

        return permissionsList;
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_CREATE:
                createClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_RENAME:
                renameClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_PROMOTE:
                promoteClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_DEMOTE:
                demoteClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_DISBAND:
                disbandClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_ADMIN_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void createClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan create error!")
                .setDescription("something went wrong..");

        User clanLeader = null;
        String clanName = "";

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.LEADER)){
                if(option.getUserValue().isPresent()){
                    clanLeader = option.getUserValue().get();
                }
            }
            if(option.getName().equals(OptionLabelTag.NAME)){
                if(option.getStringValue().isPresent()){
                    clanName = option.getStringValue().get();
                }
            }
        }

        if(clanLeader!=null && !clanName.equals("")) {
            if(clanService.getClanRoleByLeader(server, clanLeader) == null) {
                if(!clanService.isClanMember(server, clanLeader)) {
                    if(setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE) != null) {
                        Role clanLeaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                        Role clanRole = clanService.createClan(server, clanLeader, clanName);
                        clanLeaderRole.addUser(clanLeader);
                        clanRole.addUser(clanLeader);
                        TextChannel clanTextChat = clanService.getClanTextChat(server, clanRole);
                        if (clanTextChat != null) {
                            clanTextChat.sendMessage(clanRole.getMentionTag());
                        }

                        responseEmbedBuilder.setColor(Color.GREEN)
                                .setTitle("Clan create success!")
                                .setDescription(clanRole.getMentionTag() + " clan was created and " + clanLeader.getMentionTag() + " was assigned as **clan leader**");
                    } else {
                        responseEmbedBuilder.setDescription("No Role has been setup for the role tag **"+RoleTag.CLAN_LEADER_ROLE.toUpperCase()+"**");
                    }
                } else {
                    responseEmbedBuilder.setDescription(clanLeader.getMentionTag() + " is already a member of a clan");
                }
            } else {
                responseEmbedBuilder.setDescription(clanLeader.getMentionTag() + " is already assigned as **clan leader** in a clan");
            }
        }
        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();

    }

    private void renameClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan rename error!")
                .setDescription("something went wrong..");


        Role clanRole = null;
        String newClanName = "";

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.CLAN)){
                if(option.getRoleValue().isPresent()){
                    clanRole = option.getRoleValue().get();
                }
            }
            if(option.getName().equals(OptionLabelTag.NAME)){
                if(option.getStringValue().isPresent()){
                    newClanName = option.getStringValue().get();
                }
            }
        }

        if (clanRole != null && !newClanName.equals("")) {
            if(clanService.isClanRole(server, clanRole)){
                String oldClanName = clanRole.getName();
                clanService.renameClan(server, clanRole, newClanName);

                responseEmbedBuilder.setColor(Color.GREEN)
                        .setTitle("Clan rename success!")
                        .setDescription(clanRole.getMentionTag() + " clan was renamed from **"+oldClanName+"** to **"+newClanName+"**");
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void promoteClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan promote error!")
                .setDescription("something went wrong..");

//TODO after category implementation

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void demoteClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan demote error!")
                .setDescription("something went wrong..");

//TODO after category implementation

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }

    private void disbandClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan disband error!")
                .setDescription("something went wrong..");

        Role clanRole = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.CLAN)){
                if(option.getRoleValue().isPresent()){
                    clanRole = option.getRoleValue().get();
                }
            }
        }

        if (clanRole != null) {
            if(clanService.isClanRole(server, clanRole)){

                String clanName = clanRole.getName();
                clanService.deleteClan(server, clanRole);

                responseEmbedBuilder.setColor(Color.GREEN)
                        .setTitle("Clan disband success!")
                        .setDescription("**"+clanName + "** clan got deleted");
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        interaction.createImmediateResponder()
                .addEmbed(responseEmbedBuilder)
                .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                .respond();
    }
}
