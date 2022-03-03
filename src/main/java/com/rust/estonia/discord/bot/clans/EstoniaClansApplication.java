package com.rust.estonia.discord.bot.clans;

import com.rust.estonia.discord.bot.clans.command.slash.global.GlobalSlashCommand;
import com.rust.estonia.discord.bot.clans.command.slash.server.ServerSlashCommand;
import com.rust.estonia.discord.bot.clans.listener.BotServerJoinListener;
import com.rust.estonia.discord.bot.clans.listener.CommandListener;
import com.rust.estonia.discord.bot.clans.listener.SlashCommandListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
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
	private CommandListener commandListener;

	@Autowired
	private SlashCommandListener slashCommandListener;

	@Autowired
	private List<GlobalSlashCommand> globalSlashCommands;

	@Autowired
	private List<ServerSlashCommand> serverSlashCommands;

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
				.addMessageCreateListener(commandListener)
				.addSlashCommandCreateListener(slashCommandListener)

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
		}
	}

}
