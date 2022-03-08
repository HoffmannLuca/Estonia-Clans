package com.rust.estonia.discord.bot.clans;

import com.rust.estonia.discord.bot.clans.command.slash.CommandPermissionUtil;
import com.rust.estonia.discord.bot.clans.command.slash.global.GlobalSlashCommand;
import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.listener.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EstoniaClansApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstoniaClansApplication.class, args);
	}

	@Autowired
	private Environment env;

	@Autowired
	private BotServerJoinListener botServerJoinListener;

	@Autowired
	private SlashCommandListener slashCommandListener;

	@Autowired
	private ComponentListener componentListener;

	@Autowired
	private NewServerOwnerUpdateListener newServerOwnerUpdateListener;

	@Autowired
	private List<GlobalSlashCommand> globalSlashCommands;

	@Autowired
	private List<ServerSlashCommand> serverSlashCommands;

	@Autowired
	private CommandPermissionUtil commandPermissionUtil;

	@Bean
	@ConfigurationProperties(value = "discord-api")
	public DiscordApi discordApi(){
		DiscordApi api = new DiscordApiBuilder()

				.setToken("TOKEN")
				.setIntents(
						Intent.GUILDS,
						Intent.GUILD_MEMBERS,
						Intent.GUILD_BANS,
						Intent.GUILD_EMOJIS,
						Intent.GUILD_VOICE_STATES,
						Intent.GUILD_MESSAGES,
						Intent.GUILD_MESSAGE_REACTIONS,
						Intent.GUILD_INTEGRATIONS,
						Intent.GUILD_INVITES
				)

				.addServerJoinListener(botServerJoinListener)
				.addSlashCommandCreateListener(slashCommandListener)
				.addMessageComponentCreateListener(componentListener)
				.addServerChangeOwnerListener(newServerOwnerUpdateListener)

				.login()
				.join();

		setSlashCommands(api);

		return api;
	}

	private void setSlashCommands(DiscordApi api){

		List<SlashCommandBuilder> globalCommandBuilder = new ArrayList<>();
		for(GlobalSlashCommand command : globalSlashCommands){
			globalCommandBuilder.add(command.getCommandBuilder());
		}
		api.bulkOverwriteGlobalApplicationCommands(globalCommandBuilder).join();

		List<SlashCommandBuilder> serverCommandBuilder = new ArrayList<>();
		for(ServerSlashCommand command : serverSlashCommands){
			serverCommandBuilder.add(command.getCommandBuilder());
		}
		for(Server server : api.getServers()){

			api.bulkOverwriteServerApplicationCommands(server, serverCommandBuilder);

			commandPermissionUtil.updateServerSlashCommandPermissions(server, api);
		}
	}

}
