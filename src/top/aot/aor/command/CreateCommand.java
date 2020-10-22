package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin;
import top.aot.aor.plugin.APlugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 11:55
 * @description：d
 */
public class CreateCommand extends Command {

    public static final Map<String, Reward> map = new HashMap<>();

    /**
     * 创建命令类 name 命令字符串 len 命令参数长度
     *
     * @param name
     * @param len
     * @param usage
     * @param desc
     * @param op
     */
    public CreateCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (map.containsKey(player.getName())) {
            APlugin.Msg.sendMsgFalse(player, "已经存在一个正在编辑的Reward!");
            return true;
        }
        if (ORSMain.rewardMap.containKey(args[0])) {
            APlugin.Msg.sendMsgFalse(player, "数据库中已经有这个Reward了，请使用/ors edit " + args[0]);
            return true;
        } else {
            Reward reward;
            reward = new Reward(args[0]);
            reward.setName(args[1].replaceAll("&", "§"));
            try {
                reward.setTime(Integer.parseInt(args[2]));
            } catch (Exception e) {
                APlugin.Msg.sendMsgFalse(player, "分钟数必须是整数");
            }
            try {
                reward.setSlot(Integer.parseInt(args[3]));
            } catch (Exception e) {
                APlugin.Msg.sendMsgFalse(player, "栏位数必须是整数");
            }
            List<String> desc = new ArrayList<>();
            reward.setDesc(desc);
            List<String> cmds = new ArrayList<>();
            reward.setDesc(cmds);
            reward.setItemName("APPLE");
            ORSMain.rewardMap.put(args[0], reward);
            map.put(player.getName(), reward);
        }
        return true;
    }
}
