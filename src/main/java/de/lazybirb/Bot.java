package main.java.de.lazybirb;

import io.github.cdimascio.dotenv.Dotenv;
import me.dilley.MineStat;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;

public class Bot {
    public static void main(String[] args)  throws InterruptedException
    {
        System.out.println("HELLO WORLD! WE ARE NOW PREPARING FOR BOOTING! SETTING UP JDA!");
        Dotenv dotenv = Dotenv.configure().load();
        String token = dotenv.get("TOKEN");
        String ip = dotenv.get("IP");
        String portString = dotenv.get("PORT");
        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            port = 25565;
        }

        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("Loading...."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .build()
                .awaitReady();

        Timer t = new Timer();
        int finalPort = port;
        System.out.println("Starting services... (Services will be online when RPC updates regularly!)");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Updating RPC...");
                System.out.println("====================================");
                MineStat ms = new MineStat(ip, finalPort,5);
                if (ms.isServerUp())
                {
                    if (ms.getCurrentPlayers() == 0)
                    {
                        jda.getPresence().setStatus(OnlineStatus.IDLE);
                        System.out.println("Server is idle.");
                    }
                    else {
                        jda.getPresence().setStatus(OnlineStatus.ONLINE);
                        System.out.println("Server has players: "+ms.getCurrentPlayers()+"/"+ms.getMaximumPlayers()+"!!");
                    }

                    jda.getPresence().setActivity(Activity.watching(ms.getCurrentPlayers()+"/"+ms.getMaximumPlayers()));

                }
                else if (!ms.isServerUp()){
                    System.out.println("Server is unreachable!");
                    jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    jda.getPresence().setActivity(Activity.playing("Offline.."));
                }
                System.out.println("Debug data (only use when having problems with getting staus):\n" +
                        "Server IP from config file: "+ip+"\n"+
                        "Server Port from config file: "+finalPort+"\n" +
                        "Is requested server online?: "+ms.isServerUp()+"\n"+
                        "IP of requested server: "+ms.getAddress()+"\n" +
                        "Port of requested server:"+ms.getPort()+"\n" +
                        "Player Count: "+ms.getCurrentPlayers()+"/"+ms.getMaximumPlayers()+"\n" +
                        "Latency from bot to Server: "+ms.getLatency()
                );

                System.out.println("====================================\n");
                System.out.println("Updated!");
            }
        }, 0, 10000);
    }
}
