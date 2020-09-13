package iCast;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.voice.AudioProvider;
import org.apache.log4j.BasicConfigurator;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                .subscribe(event -> {
                    final String content = event.getMessage().getContent().orElse("");
                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (content.startsWith('!' + entry.getKey())) {
                            try {
                                entry.getValue().execute(event);
                            } catch (InterruptedException e) {
                                //lets find a way to save exceptions and write to local file!
                                e.printStackTrace();
                            }
                            break;
                        }
                        if(content.startsWith("..")){
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
        //ripping from Discord4j Tutorial here
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
// This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
// Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
// Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();
// We will be creating LavaPlayerAudioProvider in the next step
        final AudioProvider provider = new LavaPlayerMusicProvider(player);

        commands.put("Hi", event -> event.getMessage()
                .getChannel().block()
                .createMessage("Umm... Hello?").block());
        commands.put("help", event -> event.getMessage()
                .getChannel().block()
                .createMessage(strHelpMessage).block());
        //ripped from discord4j tutorial
        //https://github.com/Discord4J/Discord4J/wiki/Music-Bot-Tutorial
        commands.put("join", event -> {
            final Member member = event.getMember().orElse(null);
            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();
                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();
                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        channel.join(spec -> spec.setProvider(provider)).block();
                    }
                }
            }
        });
        //ripped from discord4j tutorial
        //https://github.com/Discord4J/Discord4J/wiki/Music-Bot-Tutorial
        final TrackScheduler scheduler = new TrackScheduler(player);
        commands.put("play", event -> {
            final String content = event.getMessage().getContent().get();
            final List<String> command = Arrays.asList(content.split(" "));
            playerManager.loadItem(command.get(1), scheduler);
        });

      //  commands.put("pause", event -> {
       //     scheduler.setPause();
      //!  });




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
