package com.github.kaktushose.jda.commands.dispatching.sender.impl;

import com.github.kaktushose.jda.commands.dispatching.sender.ReplyCallback;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Implementation of {@link ReplyCallback} used for {@link SlashCommandInteractionEvent SlashCommandInteractionEvents}.
 *
 * @author Kaktushose
 * @version 2.3.0
 * @see ReplyCallback
 * @see TextReplyCallback
 * @since 2.3.0
 */
public class InteractionReplyCallback implements ReplyCallback {

    private final SlashCommandInteractionEvent event;
    private boolean initialReply;

    /**
     * Constructs a new {@link ReplyCallback}.
     *
     * @param event the corresponding {@link SlashCommandInteractionEvent}
     */
    public InteractionReplyCallback(@NotNull SlashCommandInteractionEvent event) {
        this.event = event;
        initialReply = false;
    }

    @Override
    public void sendMessage(@NotNull String message, @Nullable Consumer<Message> success) {
        initialReply().sendMessage(message).queue(success);
    }

    @Override
    public void sendMessage(@NotNull Message message, @Nullable Consumer<Message> success) {
        initialReply().sendMessage(message).queue(success);
    }

    @Override
    public void sendMessage(@NotNull MessageEmbed embed, @Nullable Consumer<Message> success) {
        initialReply().sendMessageEmbeds(embed).queue(success);
    }

    private InteractionHook initialReply() {
        if (!initialReply) {
            initialReply = true;
            return event.deferReply().complete();
        }
        return event.getHook();
    }

}
