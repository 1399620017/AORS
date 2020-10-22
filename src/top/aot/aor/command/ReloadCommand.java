package top.aot.aor.command;

import org.bukkit.entity.Player;
import setting.MysqlSetup;
import top.aot.aor.plugin.APlugin;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 14:34
 * @description：
 */
public class ReloadCommand extends APlugin.Command {

    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public ReloadCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
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
            sendTrueMsg(player, "已经使用新的配置重新连接数据库");
        } else {
            sendFalseMsg(player, "连接失败,请保证数据库配置正确,并且已经创建要连接的数据库。");
        }
        return true;
    }
}
