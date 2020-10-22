package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.entity.RoleDataBase;
import top.aot.aor.gui.RewardGui;
import top.aot.aor.plugin.APlugin;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 14:11
 * @description：a
 */
public class OpenCommand extends APlugin.Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public OpenCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        RoleDataBase role = RoleDataBase.getRole(player);
        APlugin.GuiBase.openWindow(player, new RewardGui(player, role.getOnTime()));
        return true;
    }
}
