package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.*;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.LogMessageUtil;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ClanAdminServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private ClanService clanService;

    @Autowired
    private SetupService setupService;

    @Autowired
    private LogMessageUtil logMessageUtil;

    private final String FIRST_OPTION_CREATE = "create";
    private final String FIRST_OPTION_DISBAND = "disband";
    private final String FIRST_OPTION_RENAME = "rename";
    private final String FIRST_OPTION_PROMOTE = "promote";
    private final String FIRST_OPTION_DEMOTE = "demote";
    private final String FIRST_OPTION_UPDATE = "update";

    private final String UPDATE_SECOND_OPTION_CHAT = "chat";
    private final String UPDATE_SECOND_OPTION_ROLE = "role";
    private final String UPDATE_SECOND_OPTION_LEADER = "leader";

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
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, FIRST_OPTION_UPDATE, "description",
                                Arrays.asList(

                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, UPDATE_SECOND_OPTION_CHAT, "description",
                                                Arrays.asList(

                                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.CHANNEL, OptionLabelTag.NEW_CHAT, "description", true)

                                                )
                                        ),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, UPDATE_SECOND_OPTION_ROLE, "description",
                                                Arrays.asList(

                                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.ROLE, OptionLabelTag.NEW_ROLE, "description", true)

                                                )
                                        ),
                                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, UPDATE_SECOND_OPTION_LEADER, "description",
                                                Arrays.asList(

                                                        SlashCommandOption.createWithChoices(SlashCommandOptionType.ROLE, OptionLabelTag.CLAN, "description", true),
                                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.NEW_LEADER, "description", true)

                                                )
                                        )
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

            case FIRST_OPTION_UPDATE:
                updateClan(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_ADMIN_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void createClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

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

            if(!clanLeader.isBot()) {

                if (clanService.getClanRoleByLeader(server, clanLeader) == null) {

                    if (!clanService.isClanMember(server, clanLeader)) {

                        if (setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE) != null) {
                            Role clanLeaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                            Role clanRole = clanService.createClan(server, clanLeader, clanName);
                            clanLeaderRole.addUser(clanLeader);
                            clanRole.addUser(clanLeader);
                            TextChannel clanTextChat = clanService.getClanTextChat(server, clanRole);

                            if (clanTextChat != null && clanService.getClanInfoEmbedBuilder(server, clanRole)!=null) {
                                new MessageBuilder().setContent(clanRole.getMentionTag())
                                        .addEmbed(clanService.getClanInfoEmbedBuilder(server, clanRole)
                                                .setTitle("Here is your new clan __"+clanRole.getName()+"__")
                                        ).send(clanTextChat);
                            }

                            logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.CLAN_CREATED);

                            responseEmbedBuilder.setColor(Color.GREEN)
                                    .setTitle("Clan create success!")
                                    .setDescription(clanRole.getMentionTag() + " clan was created and " + clanLeader.getMentionTag() + " was assigned as **clan leader**");
                        } else {
                            responseEmbedBuilder.setDescription("No Role has been setup for the role tag **" + RoleTag.CLAN_LEADER_ROLE.toUpperCase() + "**");
                        }
                    } else {
                        responseEmbedBuilder.setDescription(clanLeader.getMentionTag() + " is already a member of a clan");
                    }
                } else {
                    responseEmbedBuilder.setDescription(clanLeader.getMentionTag() + " is already assigned as **clan leader** in a clan");
                }
            } else {
                responseEmbedBuilder.setDescription(clanLeader.getMentionTag() + " is a bot. you can only select real users");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void renameClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

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

                logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.CLAN_RENAMED);

                responseEmbedBuilder.setColor(Color.GREEN)
                        .setTitle("Clan rename success!")
                        .setDescription(clanRole.getMentionTag() + " clan was renamed from **"+oldClanName+"** to **"+newClanName+"**");
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void promoteClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan promote error!")
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

                if(clanService.promoteClan(server, clanRole)){

                    String clanRankTag = clanService.getClanRankName(server, clanRole);

                    logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.CLAN_PROMOTED);

                    responseEmbedBuilder.setColor(Color.GREEN)
                            .setTitle("Clan promote success!")
                            .setDescription(clanRole.getMentionTag()+" clan was promoted to **"+ clanRankTag+"**");
                } else {
                    responseEmbedBuilder.setDescription(clanRole.getMentionTag()+" can't be promoted because it already has the highest rank");
                }
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void demoteClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan demote error!")
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

                if(clanService.demoteClan(server, clanRole)){

                    String clanRankTag = clanService.getClanRankName(server, clanRole);

                    logMessageUtil.sendLogMessage(server, user, clanRole, LogMessageTag.CLAN_DEMOTED);

                    responseEmbedBuilder.setColor(Color.GREEN)
                            .setTitle("Clan promote success!")
                            .setDescription(clanRole.getMentionTag()+" clan was demoted to **"+ clanRankTag+"**");
                } else {
                    responseEmbedBuilder.setDescription(clanRole.getMentionTag()+" can't be demoted because it already has the lowes rank");
                }
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void disbandClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

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

                responseEmbedBuilder.setColor(Color.YELLOW)
                        .setTitle("Clan disband warning!")
                        .setDescription("Are you sure you want to disband the clan"+clanRole.getMentionTag()+" ?");

                response.setContent(clanRole.getMentionTag()).addComponents(
                        ActionRow.of(
                                Button.danger(ButtonTag.DISBAND_CLAN, "Disband")
                        )
                );
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void updateClan(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan update error!")
                .setDescription("something went wrong..");

        Role clanRole = null;
        Role newRole = null;
        Channel newChat = null;
        User newLeader = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.CLAN)){
                if(option.getRoleValue().isPresent()){
                    clanRole = option.getRoleValue().get();
                }
            }
            if(option.getName().equals(OptionLabelTag.NEW_ROLE)){
                if(option.getRoleValue().isPresent()){
                    newRole = option.getRoleValue().get();
                }
            }
            if(option.getName().equals(OptionLabelTag.NEW_CHAT)){
                if(option.getChannelValue().isPresent()){
                    newChat = option.getChannelValue().get();
                }
            }
            if(option.getName().equals(OptionLabelTag.NEW_LEADER)){
                if(option.getUserValue().isPresent()){
                    newLeader = option.getUserValue().get();
                }
            }
        }

        if (clanRole!=null) {

            if(clanService.isClanRole(server, clanRole)){

                if(secondOption.equals(UPDATE_SECOND_OPTION_CHAT)){

                    if(newChat!=null){

                        if(newChat.asTextChannel().isPresent()){
                            String oldChatMentionTag = clanService.getClanTextChatAsMentionTag(server, clanRole);

                            if(clanService.setClanTextChat(server, clanRole, newChat.asTextChannel().get().getId())){
                                String newChatMentionTag = clanService.getClanTextChatAsMentionTag(server, clanRole);

                                responseEmbedBuilder.setColor(Color.GREEN)
                                        .setTitle("Clan update success!")
                                        .setDescription("New clan chat was set from "+oldChatMentionTag+" to "+newChatMentionTag);
                            }
                        } else {
                            responseEmbedBuilder.setDescription("Please select a text channel");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("No new clan chat selected");
                    }
                }
                if(secondOption.equals(UPDATE_SECOND_OPTION_ROLE)){

                    if(newRole!=null){

                        if(!clanService.isClanRole(server, newRole)) {

                            if (clanService.setClanRole(server, clanRole, newRole)) {

                                responseEmbedBuilder.setColor(Color.GREEN)
                                        .setTitle("Clan update success!")
                                        .setDescription("New clan role was set from " + clanRole.getMentionTag() + " to " + newRole.getMentionTag());
                            }
                        } else {
                            responseEmbedBuilder.setDescription(newRole.getMentionTag()+" is already a clan role for a different clan");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("No new clan role selected");
                    }
                }
                if(secondOption.equals(UPDATE_SECOND_OPTION_LEADER)){

                    if(newLeader!=null){

                        if(clanService.getClanRoleByLeader(server, newLeader)==null) {

                            if(clanService.isClanMember(server, newLeader)) {

                                if(clanRole.hasUser(newLeader)) {

                                    if (clanService.setNewClanLeader(server, clanRole, newLeader)) {

                                        logMessageUtil.sendLogMessage(server, newLeader, clanRole, LogMessageTag.MEMBER_PROMOTED_LEADER);

                                        responseEmbedBuilder.setColor(Color.GREEN)
                                                .setTitle("Clan update success!")
                                                .setDescription("New leader of " + clanRole.getMentionTag() + " was set to " + newLeader.getMentionTag());
                                    } else {
                                        responseEmbedBuilder.setDescription("ERROR");
                                    }
                                } else {
                                    responseEmbedBuilder.setDescription(newLeader.getMentionTag()+" is a member of a different clan");
                                }
                            } else {
                                responseEmbedBuilder.setDescription(newLeader.getMentionTag()+" is not a member of a clan");
                            }
                        } else {
                            responseEmbedBuilder.setDescription(newLeader.getMentionTag()+" is already a leader of a different clan");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("No new clan role selected");
                    }
                }
            } else {
                responseEmbedBuilder.setDescription(clanRole.getMentionTag() + " is not a clan");
            }
        } else {
            responseEmbedBuilder.setDescription("No clan role selected");
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

}
