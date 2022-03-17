package com.rust.estonia.discord.bot.clans.command.slash.server.clan;

import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.constant.*;
import com.rust.estonia.discord.bot.clans.data.service.ClanService;
import com.rust.estonia.discord.bot.clans.data.service.SetupService;
import com.rust.estonia.discord.bot.clans.util.LogMessageUtil;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
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
import java.util.concurrent.*;

@Component
public class ClanOfficerServerSlashCommand implements ServerSlashCommand {

    @Autowired
    private SetupService setupService;

    @Autowired
    private ClanService clanService;

    @Autowired
    private LogMessageUtil logMessageUtil;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final String FIRST_OPTION_INVITE = "invite";
    private final String FIRST_OPTION_KICK = "kick";

    @Override
    public String getName() { return ServerSlashTag.CLAN_OFFICER_COMMAND; }

    @Override
    public SlashCommandBuilder getCommandBuilder() {
        return SlashCommand.with(ServerSlashTag.CLAN_OFFICER_COMMAND, "description",
                Arrays.asList(
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_INVITE, "description",
                            Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                            )
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, FIRST_OPTION_KICK, "description",
                                Collections.singletonList(
                                        SlashCommandOption.create(SlashCommandOptionType.USER, OptionLabelTag.USER, "description", true)
                                )
                        )

                )
        ).setDefaultPermission(false);
    }

    @Override
    public List<ApplicationCommandPermissions> addApplicationCommandPermissions(List<ApplicationCommandPermissions> permissionsList, Server server) {

        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.CLAN_LEADER_ROLE, true);
        permissionsList = setupService.addPermissionsBySetupRoleTag(permissionsList, server, RoleTag.CLAN_OFFICER_ROLE, true);

        return permissionsList;
    }

    @Override
    public void performCommand(SlashCommandInteraction interaction, String firstOption, String secondOption, List<SlashCommandInteractionOption> commandArguments,
                               User user, TextChannel channel, Server server) {

        switch (firstOption){

            case FIRST_OPTION_INVITE:
                inviteUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            case FIRST_OPTION_KICK:
                kickUser(interaction, secondOption, commandArguments, user, channel, server);
                break;

            default:

                interaction.createImmediateResponder()
                        .setContent("ERROR: "+ServerSlashTag.CLAN_OFFICER_COMMAND+" - No command found with the name "+firstOption)
                        .setFlags(InteractionCallbackDataFlag.EPHEMERAL)
                        .respond();
        }
    }

    private void inviteUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan member invite error!")
                .setDescription("something went wrong..");

        User newMember = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.USER)){
                if(option.getUserValue().isPresent()){
                    newMember = option.getUserValue().get();
                }
            }
        }

        if(newMember!=null) {

            if(!newMember.isBot()) {
                Role clanRole = clanService.getClanRoleByMember(server, user);

                if (clanRole != null) {

                    if(clanRole.getUsers().size()<25) {

                        if (!clanRole.hasUser(newMember)) {

                            if (!clanService.isClanMember(server, newMember)) {
                                TextChannel clanInvites = setupService.getServerTextChannelByChannelTag(server, TextChannelTag.CLAN_INVITE_CHANNEL, true);

                                if (clanInvites != null) {

                                    EmbedBuilder inviteEmbed = clanService.getClanInfoEmbedBuilder(server, clanRole);
                                    inviteEmbed.setTitle("You got invited to __" + clanRole.getName() + "__ clan");

                                    try {
                                        Message inviteMessage = new MessageBuilder()
                                                .setContent(newMember.getMentionTag())
                                                .addEmbed(inviteEmbed)
                                                .addActionRow(
                                                        Button.success(ButtonTag.JOIN_CLAN, "Join clan"),
                                                        Button.danger(ButtonTag.DECLINE_CLAN, "Decline")
                                                )
                                                .send(clanInvites).get();

                                        final Runnable deleteInviteMessage = new Runnable() {
                                            public void run() { inviteMessage.delete(); }
                                        };

                                        scheduler.schedule(deleteInviteMessage, 1, TimeUnit.DAYS);

                                        responseEmbedBuilder.setColor(Color.GREEN)
                                                .setTitle("Clan member invite success!")
                                                .setDescription(newMember.getMentionTag() + " received an invitation in the **" + TextChannelTag.CLAN_INVITE_CHANNEL + "** channel");

                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    responseEmbedBuilder.setDescription("No clan invite channel available");
                                }
                            } else {
                                responseEmbedBuilder.setDescription(newMember.getMentionTag() + " is already a member of another clan");
                            }
                        } else {
                            responseEmbedBuilder.setDescription(newMember.getMentionTag() + " is already a member of your clan");
                        }
                    } else {
                        responseEmbedBuilder.setDescription("clan is already full! you can only have 25 members max");
                    }
                } else {
                    responseEmbedBuilder.setDescription("Your clan role does not exist");
                }
            } else {
                responseEmbedBuilder.setDescription(newMember.getMentionTag() + " is a bot. you can only select real users");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }

    private void kickUser(SlashCommandInteraction interaction, String secondOption, List<SlashCommandInteractionOption> commandArguments, User user, TextChannel channel, Server server) {

        InteractionImmediateResponseBuilder response = interaction.createImmediateResponder().setFlags(InteractionCallbackDataFlag.EPHEMERAL);

        EmbedBuilder responseEmbedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Clan member kick error!")
                .setDescription("something went wrong..");

        User kickMember = null;

        for(SlashCommandInteractionOption option : commandArguments){
            if(option.getName().equals(OptionLabelTag.USER)){
                if(option.getUserValue().isPresent()){
                    kickMember = option.getUserValue().get();
                }
            }
        }

        if(kickMember!=null) {
            if(!kickMember.isBot()) {
                Role clanRole = clanService.getClanRoleByMember(server, user);
                if (clanRole != null) {
                    if (clanRole.hasUser(kickMember)) {
                        Role leaderRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_LEADER_ROLE);
                        Role officerRole = setupService.getServerRoleByRoleTag(server, RoleTag.CLAN_OFFICER_ROLE);
                        if(leaderRole!= null){
                            if(officerRole!=null){
                                if(!leaderRole.hasUser(kickMember)){
                                    if(!officerRole.hasUser(kickMember)){

                                        clanRole.removeUser(kickMember);

                                        logMessageUtil.sendLogMessage(server, kickMember, clanRole, LogMessageTag.MEMBER_KICKED);

                                        responseEmbedBuilder.setColor(Color.GREEN)
                                                .setTitle("Clan member kick success!")
                                                .setDescription(kickMember.getMentionTag() + " is no longer a member of your clan");
                                    } else {
                                        responseEmbedBuilder.setDescription("You cant kick members with the "+officerRole.getMentionTag()+" role");
                                    }
                                } else {
                                    responseEmbedBuilder.setDescription("You cant kick members with the "+leaderRole.getMentionTag()+" role");
                                }
                            } else {
                                responseEmbedBuilder.setDescription("No role was setup for the role tag: officer");
                            }
                        } else {
                            responseEmbedBuilder.setDescription("No role was setup for the role tag: leader");
                        }
                    } else {
                        responseEmbedBuilder.setDescription(kickMember.getMentionTag() + " is not a member of your clan");
                    }
                } else {
                    responseEmbedBuilder.setDescription("Your clan role does not exist");
                }
            } else {
                responseEmbedBuilder.setDescription(kickMember.getMentionTag() + " is a bot. you can only select real users");
            }
        }

        response.addEmbed(responseEmbedBuilder).respond();
    }
}
