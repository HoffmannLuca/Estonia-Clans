package com.rust.estonia.discord.bot.clans;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class EstoniaClansApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstoniaClansApplication.class, args);
	}

	@Autowired
	private Environment env;

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

				.login()
				.join();

		return api;
	}

}
