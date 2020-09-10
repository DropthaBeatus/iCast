package iCast;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.apache.log4j.BasicConfigurator;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.HashMap;
import java.util.Map;

public class tBot {

    private static final Map<String, Command> commands = new HashMap<>();
    private static final Logger log = Loggers.getLogger(tBot.class);




    public static void main(String args[]){
        final String token = args[0];
        Checkers game = new Checkers();
        BasicConfigurator.configure();

        final DiscordClient client = new DiscordClientBuilder(token).build();
        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    final String content = event.getMessage().getContent().orElse("");
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        // We will be using ! as our "prefix" to any command in the system.
                        if (content.startsWith('!' + entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        }
                        //can create game master thing here for game management
                        if(content.equals("Play Checkers")){
                            event.getMessage()
                                    .getChannel().block()
                                    .createMessage(game.printBoard()).block();
                            break;

                        }
                        if(content.startsWith("Move")){
                            //Need to have an args check here
                            event.getMessage()
                                    .getChannel().block()
                                    .createMessage(game.parseMessage(content)).block();
                            break;

                        }

                    }
                });


        client.login().block();



    }

    static {
        commands.put("ping", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Pong!").block());

        commands.put("Play Checkers", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Okay let's see what we can do.\nTest").block());
    }


}
