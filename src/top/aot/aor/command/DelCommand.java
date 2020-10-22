package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.plugin.APlugin.Command;
import top.aot.aor.plugin.APlugin.Msg;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 13:40
 * @description：
 */
public class DelCommand extends Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public DelCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (ORSMain.rewardMap.remove(args[0])) {
            Msg.sendMsgTrue(player, "成功删除一个Reward,使用/ors list 查看列表");
        } else {
            Msg.sendMsgFalse(player, "数据库中不存在这个Reward");
        }
        return true;
    }
}
