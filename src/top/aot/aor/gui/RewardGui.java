package top.aot.aor.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import setting.OnlineReward;
import top.aot.aor.ORSMain;
import top.aot.aor.entity.Reward;
import top.aot.aor.entity.RoleDataBase;
import top.aot.aor.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 12:09
 * @description：
 */
public class RewardGui extends APlugin.Gui {

    public RewardGui(Player owner, int time) {
        super(owner, OnlineReward.getTitle().replaceAll("<m>", time + ""), OnlineReward.getInvlLevel());
    }

    @Override
    public boolean clickRegion(String clickedRegionName, ClickType clickType, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean closeEvent() {
        return false;
    }

    @Override
    public void updateWindow() {
        Set<Reward> rewardMapItem = ORSMain.rewardMap.getItem();
        for (Reward reward : rewardMapItem) {
            APlugin.AssemblyDynamic<RewardGui> dynamic = new APlugin.AssemblyDynamic<RewardGui>(this) {
                @Override
                protected void init(RewardGui gui, ItemMeta itemMeta) {
                    RoleDataBase role = RoleDataBase.getRole(RewardGui.this.getOwnerName());
                    setTitle(reward.getName());
                    List<String> lore = new ArrayList<>(reward.getDesc());
                    if (role.getOnTime() < reward.getTime()) {
                        lore.add("§c在线时间未达到" + reward.getTime() + "分钟");
                    } else if (role.isReward(reward.getKey())) {
                        lore.add("§c此奖励已经领取完成！");
                    } else {
                        lore.add("§a在线时间已经达到" + reward.getTime() + "分钟");
                        lore.add("§a§n左键点击即可领取奖励");
                    }
                    setLore(lore);
                }

                @Override
                protected Material material() {
                    return Material.getMaterial(reward.getItemName());
                }

                @Override
                protected short secondID() {
                    return 0;
                }
            };
            dynamic.setClickListener((APlugin.LeftClickListener) () -> {
                RoleDataBase role = RoleDataBase.getRole(getOwnerName());
                if (role.getOnTime() >= reward.getTime() && !role.isReward(reward.getKey())) {
                    role.setReward(reward.getKey());
                    ORSMain.roleMap.put(getOwnerName(), role);
                    Player player = getOwner();
                    boolean op = player.isOp();
                    if (!op) {
                        player.setOp(true);
                    }
                    try {
                        for (String cmd : reward.getCmds()) {
                            if (cmd.startsWith("/")) {
                                cmd = cmd.substring(1);
                                Bukkit.dispatchCommand(player, cmd.replaceAll("<p>", player.getName()));
                            } else {
                                Bukkit.dispatchCommand(APlugin.serverSender, cmd.replaceAll("<p>", player.getName()));
                            }
                        }
                    } catch (Exception ignored) {

                    }
                    if (!op) {
                        player.setOp(false);
                    }
                    updateWindow();
                }
            });
            setAssembly(reward.getSlot(), dynamic);
        }
    }

    @Override
    protected void initWindow() {

    }
}
