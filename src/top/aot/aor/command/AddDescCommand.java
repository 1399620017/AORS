package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin.Command;
import top.aot.aor.plugin.APlugin.Msg;

import java.util.List;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 12:26
 * @description：
 */
public class AddDescCommand extends Command {
    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public AddDescCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (CreateCommand.map.containsKey(player.getName())) {
            Reward reward = CreateCommand.map.get(player.getName());
            List<String> desc = reward.getDesc();
            desc.add(args[0].replaceAll("&", "§"));
            reward.setDesc(desc);
            ORSMain.rewardMap.put(reward.getKey(), reward);
            Msg.sendMsgTrue(player, "添加" + reward.getKey() + "的奖励说明成功：" + reward.getDesc().size());
        } else {
            Msg.sendMsgFalse(player, "没有正在编辑的Reward, 请使用/ors list查看");
        }
        return true;
    }
}
