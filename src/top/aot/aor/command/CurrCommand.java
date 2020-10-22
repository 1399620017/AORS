package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin;
import top.aot.aor.plugin.APlugin.Msg;
import top.aot.aor.plugin.APlugin.Command;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 13:10
 * @description：
 */
public class CurrCommand extends Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public CurrCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (CreateCommand.map.containsKey(player.getName())) {
            Msg.sendMsgTrue(player, "当前编辑的Reward");
            Reward reward = CreateCommand.map.get(player.getName());
            Msg.sendMsgTrue(player, "ID: " + reward.getKey());
            Msg.sendMsgTrue(player, "奖励显示名: " + reward.getName());
            Msg.sendMsgTrue(player, "按钮材质: " + reward.getItemName());
            Msg.sendMsgTrue(player, "要求时间: " + reward.getTime() + "分钟");
            Msg.sendMsgTrue(player, "按钮位置: " + reward.getSlot());
            Msg.sendMsgTrue(player, "命令列表: ");
            for (String cmd : reward.getCmds()) {
                Msg.sendMsg(player, cmd);
            }
            Msg.sendMsgTrue(player, "奖励说明: ");
            for (String desc : reward.getDesc()) {
                Msg.sendMsg(player, desc);
            }
        } else {
            Msg.sendMsgFalse(player, "没有正在编辑的Reward");
        }
        return true;
    }
}
