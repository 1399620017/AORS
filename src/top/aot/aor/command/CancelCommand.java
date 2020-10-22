package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.plugin.APlugin;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 13:28
 * @description：
 */
public class CancelCommand extends APlugin.Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public CancelCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (CreateCommand.map.containsKey(player.getName())) {
            CreateCommand.map.remove(player.getName());
            APlugin.Msg.sendMsgTrue(player, "已经取消编辑Reward");
        } else {
            APlugin.Msg.sendMsgFalse(player, "没有正在编辑的Reward");
        }
        return true;
    }
}
