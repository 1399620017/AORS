package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin;
import top.aot.aor.plugin.APlugin.Msg;
import top.aot.aor.plugin.APlugin.Command;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 12:46
 * @description：
 */
public class ListCommand extends Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public ListCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        Msg.sendMsgTrue(player, "当前拥有的Reward列表:");
        for (Reward reward : ORSMain.rewardMap.getItem()) {
            Msg.sendMsg(player, String.format("id： %s 名字： %s 时间： %s 命令数： %s", reward.getKey(),
                    reward.getName(), reward.getTime(), reward.getCmds().size()));
        }
        Msg.sendMsgTrue(player, "使用/ors edit <id> 选择一个Reward进行编辑");
        return true;
    }
}
