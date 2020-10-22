package top.aot.aor.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import top.aot.aor.entity.RoleDataBase;
import top.aot.aor.plugin.APlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 14:03
 * @description：
 */
public class LoginListener implements Listener {

    Map<String, BukkitRunnable> map = new HashMap<>();

    @EventHandler
    public void loginListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (map.containsKey(player.getName())) {
            map.get(player.getName()).cancel();
            map.remove(player.getName());
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    RoleDataBase.update(player);
                } else {
                    map.remove(player.getName());
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(APlugin.plugin, 1200, 1200);
        map.put(player.getName(), runnable);
    }

    @EventHandler
    public void loginListener(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (map.containsKey(player.getName())) {
            map.get(player.getName()).cancel();
            map.remove(player.getName());
        }
    }
}
