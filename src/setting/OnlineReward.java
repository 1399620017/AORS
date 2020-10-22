package setting;

import org.bukkit.configuration.file.FileConfiguration;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin;

import java.util.*;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 12:13
 * @description：
 */
public class OnlineReward extends APlugin.AsxConfig {

    private static Map<Integer, Reward> slotMap;

    private static OnlineReward onlineReward;

    private static String title;
    private static int invlLevel;

    public static OnlineReward get() {
        if (onlineReward == null) {
            onlineReward = new OnlineReward();
        }
        return onlineReward;
    }

    public static OnlineReward reload() {
        onlineReward = new OnlineReward();
        return onlineReward;
    }

    public static Reward getReward(int slot) {
        return slotMap.get(slot);
    }

    private OnlineReward() {
        super("onlineRewardSetup");
    }

    public static Map<Integer, Reward> getSlotMap() {
        return slotMap;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        OnlineReward.title = title;
    }

    public static int getInvlLevel() {
        return invlLevel;
    }

    public static void setInvlLevel(int invlLevel) {
        OnlineReward.invlLevel = invlLevel;
    }

    @Override
    protected void defaultValue() {
        customConfig.set("title", "§c在线奖励领取|在线时间：<m>分钟");
        customConfig.set("inv-level", 6);
    }

    private static Set<String> set = new HashSet<>();
    static {
        set.add("title");
        set.add("inv-level");
    }

    @Override
    protected void loadConfig(FileConfiguration config) {
        slotMap = new HashMap<>();
        invlLevel = config.getInt("inv-level", 6);
        title = config.getString("title", "§c在线奖励领取|在线时间：<m>分钟");
    }

    @Override
    protected void saveConfig(FileConfiguration config) {

    }
}
