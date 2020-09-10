package iCast;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface game_commands {
    void execute(MessageCreateEvent event);
}
