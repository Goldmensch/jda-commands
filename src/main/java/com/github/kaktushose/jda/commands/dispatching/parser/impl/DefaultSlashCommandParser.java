package com.github.kaktushose.jda.commands.dispatching.parser.impl;

import com.github.kaktushose.jda.commands.dispatching.CommandContext;
import com.github.kaktushose.jda.commands.dispatching.CommandDispatcher;
import com.github.kaktushose.jda.commands.dispatching.parser.Parser;
import com.github.kaktushose.jda.commands.embeds.error.ErrorMessageFactory;
import com.github.kaktushose.jda.commands.reflect.ImplementationRegistry;
import com.github.kaktushose.jda.commands.settings.GuildSettings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link Parser} that can parse {@link SlashCommandInteractionEvent}.
 * This parser will work within the limitations given by the {@link GuildSettings}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @since 2.3.0
 */
public class DefaultSlashCommandParser extends Parser<SlashCommandInteractionEvent> {

    /**
     * Takes a {@link SlashCommandInteractionEvent}, parses and transpiles it into a {@link CommandContext}.
     *
     * @param event      the {@link SlashCommandInteractionEvent} to parse
     * @param dispatcher the calling {@link CommandDispatcher}
     * @return a new {@link CommandContext}
     */
    @Override
    @NotNull
    public CommandContext parse(@NotNull SlashCommandInteractionEvent event, @NotNull CommandDispatcher dispatcher) {
        event.deferReply(false).queue();

        CommandContext context = new CommandContext();
        ImplementationRegistry registry = dispatcher.getImplementationRegistry();
        GuildSettings settings = registry.getSettingsProvider().getSettings(event.isFromGuild() ? event.getGuild() : null);
        ErrorMessageFactory errorMessageFactory = registry.getErrorMessageFactory();

        if (settings.isMutedGuild()) {
            context.setErrorMessage(errorMessageFactory.getGuildMutedMessage(context));
            return context.setCancelled(true);
        }

        if (settings.getMutedChannels().contains(event.getChannel().getIdLong())) {
            context.setErrorMessage(errorMessageFactory.getChannelMutedMessage(context));
            return context.setCancelled(true);
        }

        StringBuilder message = new StringBuilder(event.getName());
        event.getOptions().forEach(mapping -> message.append(" ").append(mapping.getAsString()));
        String[] input = message.toString().split(" ");

        context.setEvent(event)
                .setSettings(settings)
                .setInput(input)
                .setJdaCommands(dispatcher.getJdaCommands())
                .setImplementationRegistry(registry);

        return context;
    }
}