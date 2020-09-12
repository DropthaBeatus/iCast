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
    //Factory Object Creation: Maybe want to create an array of Board instead of several Objs
    private static Checkers gameCheckers = new Checkers();

    private static final String strHelpMessage = "Type '..' to access various board games.\nType ..help to get commands";



    public static void main(String args[]){
        final String token = args[0];

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
                        if(content.startsWith(".." + entry.getKey())){
                            event.getMessage()
                                    .getChannel().block()
                                    .createMessage(eventExecute(content)).block();

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
        commands.put("help", event -> event.getMessage()
                .getChannel().block()
                .createMessage(strHelpMessage).block());
    }


    private static String eventExecute(String content) {
        if (content.startsWith("..pc")) {
            return gameCheckers.commands(content);
        }
        else if (content.startsWith("..help")) {
            return "Type ..pc to access checkers game";
        }
        else {
            return "No commands " + content + "exist of that type. Type ..help for more commands";
        }
    }


}
