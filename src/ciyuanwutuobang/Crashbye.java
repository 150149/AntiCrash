package ciyuanwutuobang;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class Crashbye  extends JavaPlugin implements Listener {
    private ProtocolManager pm;
    Plugin thisplug = this;
    private Map<String, Double> LastX = new HashMap<>();
    private Map<String,Integer> KickCount = new HashMap<>();
    private Map<String,Integer> CheckTimer = new HashMap<>();

    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getConsoleSender().sendMessage("防蹦服2.0已开启");
        Bukkit.getConsoleSender().sendMessage("作者：150149 QQ：1802796278");
        pm = ProtocolLibrary.getProtocolManager();

        pm.addPacketListener(new PacketAdapter(PacketAdapter.params()

                .plugin(thisplug)
                .clientSide()
                .listenerPriority(ListenerPriority.HIGH)
                .gamePhase(GamePhase.PLAYING)
                .optionAsync()
                .types(PacketType.Play.Client.POSITION)
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer().getName().contains("LOTR")) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                PacketType packettype = event.getPacketType();

                double x = packet.getDoubles().getValues().get(0);
                double y = packet.getDoubles().getValues().get(1);
                double z = packet.getDoubles().getValues().get(2);


                LastX.putIfAbsent(event.getPlayer().getName(),x);
                KickCount.putIfAbsent(event.getPlayer().getName(),0);
                CheckTimer.putIfAbsent(event.getPlayer().getName(),0);

                if (y>1000 || x>1000000 || y>1000000) {
                    event.setCancelled(true);
                    BukkitTask time=new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!event.getPlayer().isBanned()) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"ban " + event.getPlayer().getName() +" &7[ &c反作弊 &7]&f禁止崩服");
                            }
                            this.cancel();
                        }
                    }.runTaskTimer(thisplug, 2L,0);
                } else if ( Math.abs(x- LastX.get(event.getPlayer().getName()))>5000) {
                    KickCount.put(event.getPlayer().getName(),KickCount.get(event.getPlayer().getName())+ 1);
                    CheckTimer.put(event.getPlayer().getName(),30);
                }

                if (CheckTimer.get(event.getPlayer().getName())>0) {
                    CheckTimer.put(event.getPlayer().getName(),CheckTimer.get(event.getPlayer().getName())-1);
                }
                if (CheckTimer.get(event.getPlayer().getName())<=0) {
                    KickCount.put(event.getPlayer().getName(),0);
                }
                if (KickCount.get(event.getPlayer().getName())>=8) {
                    BukkitTask time=new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!event.getPlayer().isBanned()) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"ban " + event.getPlayer().getName() +" &7[ &c反作弊 &7]&f禁止崩服[AAC new]");
                            }
                            KickCount.put(event.getPlayer().getName(),0);
                            this.cancel();
                        }
                    }.runTaskTimer(thisplug, 2L,0);
                }
                LastX.put(event.getPlayer().getName(),x);
            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("防蹦服2.0已关闭");
    }

}
