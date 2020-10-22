package top.aot.aor.entity;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 13:33
 * @description：
 */
public class RoleDataBase {

    private int onTime;
    private List<String> list;
    private String updataTime;

    public void updataOnTime() {
        onTime++;
        if (!Objects.equals(updataTime, APlugin.Util.DateTool.getDateString())) {
            updataTime = APlugin.Util.DateTool.getDateString();
            onTime = 1;
            list = new ArrayList<>();
        }
    }

    public int getOnTime() {
        return onTime;
    }

    public boolean isReward(String key) {
        if (list == null) {
            list = new ArrayList<>();
            return false;
        }
        return list.contains(key);
    }

    public void setReward(String key) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(key);
    }

    public static void update(Player player) {
        if (ORSMain.roleMap.containKey(player.getName())) {
            RoleDataBase roleDataBase = ORSMain.roleMap.get(player.getName());
            roleDataBase.updataOnTime();
            ORSMain.roleMap.put(player.getName(), roleDataBase);
        } else {
            RoleDataBase roleDataBase = new RoleDataBase();
            roleDataBase.setUpdataIime();
            roleDataBase.updataOnTime();
            ORSMain.roleMap.put(player.getName(), roleDataBase);
        }
    }

    public static RoleDataBase getRole(Player player) {
        return getRole(player.getName());
    }

    public static RoleDataBase getRole(String playerName) {
        if (ORSMain.roleMap.containKey(playerName)) {
            RoleDataBase roleDataBase = ORSMain.roleMap.get(playerName);
            return roleDataBase;
        } else {
            RoleDataBase roleDataBase = new RoleDataBase();
            roleDataBase.setUpdataIime();
            ORSMain.roleMap.put(playerName, roleDataBase);
            return roleDataBase;
        }
    }

    private void setUpdataIime() {
        updataTime = APlugin.Util.DateTool.getDateString();
    }
}
