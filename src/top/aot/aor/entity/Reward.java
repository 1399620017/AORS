package top.aot.aor.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：ZhangHe
 * @date ：Created in 2020/9/26 12:26
 * @description：
 */
public class Reward {
    private String name;
    private List<String> cmds;
    private List<String> desc;
    private int time;
    private String itemName;
    private int slot;
    private final String key;

    public Reward(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public Reward setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getCmds() {
        return cmds == null ? new ArrayList<>() : cmds;
    }

    public Reward setCmds(List<String> cmds) {
        this.cmds = cmds;
        return this;
    }

    public List<String> getDesc() {
        return desc == null ? new ArrayList<>() : desc;
    }

    public Reward setDesc(List<String> desc) {
        this.desc = desc;
        return this;
    }

    public int getTime() {
        return time;
    }

    public Reward setTime(int time) {
        this.time = time;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public Reward setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public Reward setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public String getKey() {
        return key;
    }
}
