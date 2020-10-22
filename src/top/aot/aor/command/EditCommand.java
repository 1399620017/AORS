package top.aot.aor.command;

import org.bukkit.entity.Player;
import top.aot.aor.ORSMain;
import top.aot.aor.entity.Reward;
import top.aot.aor.plugin.APlugin.Command;
import top.aot.aor.plugin.APlugin.Msg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/30 11:55
 * @description：d
 */
public class EditCommand extends Command {

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
    public EditCommand(String name, int len, String usage, String desc, boolean op) {
        super(name, len, usage, desc, op);
    }

    @Override
    public boolean send(Player player, String[] args) {
        if (ORSMain.rewardMap.containKey(args[0])) {
            Reward reward = ORSMain.rewardMap.get(args[0]);
            CreateCommand.map.put(player.getName(), reward);
            Msg.sendMsgTrue(player, "已将 " + args[0] + "设置为正在编辑的Reward");
            return true;
        } else {
            Msg.sendMsgFalse(player, "没有Id是" + args[0] + "的Reward,请使用功能/ors list 查看");
        }
        return true;
    }
}
