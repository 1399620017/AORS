package setting;

import org.bukkit.configuration.file.FileConfiguration;
import top.aot.aor.plugin.APlugin.AsxConfig;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 9:12
 * @description：
 */
public class MysqlSetup extends AsxConfig {

    public MysqlSetup() {
        super("mysql");
    }

    @Override
    protected void defaultValue() {
        customConfig.set("user", "root");
        customConfig.set("password", "root");
        customConfig.set("database", "or");
        customConfig.set("port", "3306");
        customConfig.set("ip", "localhost");
    }

    @Override
    protected void loadConfig(FileConfiguration config) {

    }

    @Override
    protected void saveConfig(FileConfiguration config) {

    }

    public String getUser() {
        return customConfig.getString("user", "root");
    }

    public String getPassword() {
        return customConfig.getString("password", "root");
    }

    public String getDataBase() {
        return customConfig.getString("database", "or");
    }

    public String getPort() {
        return customConfig.getString("port", "3306");
    }

    public String getIp() {
        return customConfig.getString("ip", "localhost");
    }
}
