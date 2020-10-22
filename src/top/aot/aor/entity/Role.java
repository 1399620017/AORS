//package top.aot.aor.entity;
//
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.Player;
//import top.aot.aor.plugin.APlugin;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author ：ZhangHe
// * @date ：Created in 2020/9/26 13:33
// * @description：
// */
//public class Role extends APlugin.AsxConfig {
//
//    private static final Map<String, Role> map = new HashMap<>();
//
//    public static Role getRole(String name) {
//        Role role;
//        if (!map.containsKey(name)) {
//            role = new Role(name);
//            map.put(name, role);
//        } else {
//            role = map.get(name);
//        }
//        return role;
//    }
//
//    public static void updata(Player player) {
//        Role role = getRole(player);
//        role.update();
//    }
//
//    public static Role getRole(Player player) {
//        return getRole(player.getName());
//    }
//
//    public Role(String fileName) {
//        super(fileName);
//    }
//
//
//    @Override
//    protected void defaultValue() {
//
//    }
//
//    @Override
//    protected void loadConfig(FileConfiguration config) {
//
//    }
//
//    @Override
//    protected void saveConfig(FileConfiguration config) {
//
//    }
//
//    public void updataTime() {
//        customConfig.set("time", getTime() + 1);
//        update();
//    }
//
//    public int getTime() {
//        return customConfig.getInt("time", 0);
//    }
//
//    public boolean isReward(String key) {
//        List<String> list = customConfig.getStringList("keys");
//        return list.contains(key);
//    }
//
//    public void setReward(String key) {
//        List<String> list = customConfig.getStringList("keys");
//        list.add(key);
//        customConfig.set("keys", list);
//        update();
//    }
//
//}
