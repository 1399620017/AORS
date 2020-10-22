package top.aot.aor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import setting.MysqlSetup;
import setting.OnlineReward;
import top.aot.aor.command.*;
import top.aot.aor.entity.Reward;
import top.aot.aor.entity.RoleDataBase;
import top.aot.aor.gui.RewardGui;
import top.aot.aor.listener.LoginListener;
import top.aot.aor.plugin.APlugin;

import java.util.Objects;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 12:01
 * @description：
 */
public class ORSMain extends APlugin.CorePlugin {

    @Override
    public void command() {
        new OpenCommand("o", 0, "", "打开奖励菜单", false);
        new CreateCommand("create", 4, "<id> <name> <time> <slot>", "创建一个新的奖励等级", true);
        new AddCmdCommand("addc", 1, "<cmd>", "添加奖励命令，不带/是通过玩家执行，命令中的空格用-代替，玩家变量<p>", true);
        new AddDescCommand("addd", 1, "<desc>", "添加奖励说明，颜色符号使用&", true);
        new ListCommand("list", 0, "", "查看奖励列表", true);
        new CurrCommand("curr", 0, "", "查看正在编辑的Reward", true);
        new EditCommand("edit", 1, "<id>", "选择一个Reward进行编辑", true);
        new CancelCommand("cancel", 0, "", "取消编辑Reward", true);
        new DelCommand("del", 1, "<id>", "删除Reward", true);
        new ReloadCommand("reload", 0, "", "重载数据库连接配置并重新尝试连接数据库", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            RoleDataBase role = RoleDataBase.getRole(player);
            APlugin.GuiBase.openWindow(player, new RewardGui(player, role.getOnTime()));
        }
        if (args.length == 1 && Objects.equals(args[0], "reload")) {
            MysqlSetup mysqlSetup = new MysqlSetup();
            if (APlugin.DataBase.setDBName(mysqlSetup.getDataBase())
                    .setIp(mysqlSetup.getIp())
                    .setPassword(mysqlSetup.getPassword())
                    .setPort(mysqlSetup.getPort())
                    .setTable("box")
                    .setUser(mysqlSetup.getUser())
                    .setTableMap((map) -> {
                        map.put("box", "CREATE TABLE `box` (" +
                                "  `asx_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT ''," +
                                "  `asx_value` text COLLATE utf8_unicode_ci," +
                                "  PRIMARY KEY (`asx_key`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
                        map.put("role", "CREATE TABLE `role` (" +
                                "  `asx_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT ''," +
                                "  `asx_value` text COLLATE utf8_unicode_ci," +
                                "  PRIMARY KEY (`asx_key`)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
                        return map;
                    })
                    .connection()) {
                APlugin.Msg.sendMsgTrue(sender, "已经使用新的配置重新连接数据库");
            } else {
                APlugin.Msg.sendMsgFalse(sender, "连接失败,请保证数据库配置正确,并且已经创建要连接的数据库。");
            }
        }
        return true;
    }

    @Override
    public String[] consoleLog(ConsoleCommandSender serverSender) {
        return new String[0];
    }

    @Override
    public void listenter() {
        regListener(new LoginListener());
    }

    @Override
    public String pluginCommand() {
        return "ors";
    }

    @Override
    public String pluginName() {
        return "AORS";
    }

    @Override
    public String serverName() {
        return "Server";
    }

    public static APlugin.AsxDataBaseMap<Reward> rewardMap;
    public static APlugin.AsxDataBaseMap<RoleDataBase> roleMap;

    @Override
    public void start() {
        MysqlSetup mysqlSetup = new MysqlSetup();
        if (APlugin.DataBase.setDBName(mysqlSetup.getDataBase())
                .setIp(mysqlSetup.getIp())
                .setPassword(mysqlSetup.getPassword())
                .setPort(mysqlSetup.getPort())
                .setTable("box")
                .setUser(mysqlSetup.getUser())
                .setTableMap((map) -> {
                    map.put("box", "CREATE TABLE `box` (" +
                            "  `asx_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT ''," +
                            "  `asx_value` text COLLATE utf8_unicode_ci," +
                            "  PRIMARY KEY (`asx_key`)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
                    map.put("role", "CREATE TABLE `role` (" +
                            "  `asx_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT ''," +
                            "  `asx_value` text COLLATE utf8_unicode_ci," +
                            "  PRIMARY KEY (`asx_key`)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
                    return map;
                })
                .connection()) {
            OnlineReward.get();
            APlugin.Msg.sendConMsgTrue("ok");
            rewardMap = new APlugin.AsxDataBaseMap<>("box", Reward.class);
            roleMap = new APlugin.AsxDataBaseMap<>("role", RoleDataBase.class);
            return;
        }
        APlugin.Msg.sendConMsgFalse("err");
    }

}
