package top.aot.aor.plugin;

import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

/**
 * APlugin - 设计模块
 *
 * @version 1.0.6 添加DataBase 用于数据库连接
 * 功能说明
 * {@link Msg} 消息处理工具类
 * @see Msg#sendMsgTrue(CommandSender, String) 给CommandSender发送绿色系统提示
 * @see Msg#sendMsg(CommandSender, String) 给CommandSender发送白色系统提示
 * @see Msg#sendMsgFalse(CommandSender, String) 给CommandSender发送红色系统提示
 * @see Msg#sendJsonTrue(Player, TextComponent...) 给Player发送绿色TextComponent消息
 * @see Msg#sendConMsgTrue(String) 给后台发送绿色消息
 * @see Msg#sendConMsgFalse(String) 给后台发送红色消息
 * @see Msg#getPluginName() 获取插件名字
 * @see Msg#getServerName() 获取服务器名字
 * @see Msg#sendConPlugin(String) 以插件名义发送后台信息
 * @see Msg#sendCs(CommandSender, String) 给CommandSender发送测试信息
 * @see Msg#sendPluginBroad(String) 以插件名义发送公告
 * @see Msg#sendServerBroad(String) 以服务器名义发送公告
 * {@link Gui} 窗口类
 * @see Gui#Gui(Player, String, int) 创建GUI
 * @see Gui#Gui(Gui, Player, String, int) 创建带有父GUI的GUI
 * {@link Util.Particle} 粒子类
 * {@link AsxConfig} 配置保存类
 * {@link DataBase} 数据库初始化类
 * @see DataBase#setDBName(String) 设置数据库名
 * @see DataBase#setIp(String) 设置数据库连接IP
 * @see DataBase#setPassword(String) 设置连接密码
 * @see DataBase#setPort(String) 设置连接端口
 * @see DataBase#setUser(String) 设置用户名
 * @see DataBase#setTable(String) 设置表（暂未使用）
 * @see DataBase#setTableMap(TableMap) 设置自动建表sql
 * @see DataBase#connection() 开始连接数据库(支持重复调用)
 * {@link AsxDataBaseMap} 数据库保存配置类
 * {@link TableMap} 数据库建表接口
 */
public final class APlugin {

    /**
     * 插件实例
     */
    public static JavaPlugin plugin;

    /**
     * 服务端后台实例
     */
    public static ConsoleCommandSender serverSender;

    /**
     * 插件信息实例
     */
    public static PluginManager pluginManager;

    /**
     * 插件中文名
     */
    public static String pluginName;

    /**
     * 公告名字
     */
    public static String serverName;

    /**
     * 命令名
     */
    public static String cmdName;


    /**
     * 组件点击监听接口
     */
    public interface AllClickListener extends AssemblyClickListener {

        void leftClick();

        void leftShiftClick();

        void rightClick();

        void rightShiftClick();
    }

    /**
     * 基础组件类
     */
    public static abstract class Assembly<T> {
        private ItemStack itemStack;
        private ItemMeta itemMeta;
        private boolean finish = false;
        protected final T gui;

        Assembly(T gui) {
            this.gui = gui;
            setItemStack(new ItemStack(material())); // 初始化物品
            itemMeta = getItemStack().getItemMeta();
            if (secondID() > 0) {
                setSecondID(secondID());
            }
            init(gui, itemMeta);
            finish(); // 手动确认组件已经完成创建
        }

        /**
         * 完成组件创建 未执行此方法的组件无法添加到gui上
         */
        void finish() {
            getItemStack().setItemMeta(itemMeta);
            setFinish(true);
        }

        public T getGui() {
            return gui;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        /**
         * 设置组件图标 一般在创建时固定好 调用此方法后 Title和lore都需要重新设置 并且需要重新执行 finish() 来完成组件创建
         */
        public void setIcon(Material material) {
            getItemStack().setType(material);
            itemMeta = getItemStack().getItemMeta();
            setFinish(false);
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        /**
         * 设置组件显示数量 一般无实际意义 只做显示用 或者物品等级标记
         */
        public void setLevel(int level) {
            getItemStack().setAmount(level);
        }

        /**
         * 设置组件描述
         */
        public void setLore(List<String> lore) {
            itemMeta.setLore(lore);
        }

        /**
         * 设置组件名字
         */
        public void setTitle(String title) {
            itemMeta.setDisplayName(title);
        }

        protected short getSecondID(short id) {
            return itemStack.getDurability();
        }

        /**
         * 初始化方法 设置组件标题 内容等
         *
         * @param gui
         */
        protected abstract void init(T gui, ItemMeta itemMeta);

        /**
         * 设置组件图标
         */
        protected abstract Material material();

        /**
         * 物品附加id
         */
        protected abstract short secondID();

        /**
         * 设置物品附加id
         */
        protected void setSecondID(short id) {
            itemStack.setDurability(id);
        }

    }

    /**
     * 组件点击监听接口
     */
    public static interface AssemblyClickListener {
    }

    /**
     * 动态组件
     */
    public static abstract class AssemblyDynamic<T> extends Assembly<T> {
        private AssemblyClickListener clickListener;

        public AssemblyDynamic(T gui) {
            super(gui);
        }

        public AssemblyClickListener getClickListener() {
            return clickListener;
        }

        public AssemblyDynamic<T> setClickListener(AssemblyClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }
    }

    /**
     * 固定组件 窗口创建后不会再次变化
     */
    public static abstract class AssemblyFixed<T> extends Assembly<T> {

        public AssemblyFixed(T gui) {
            super(gui);
        }

    }

    /**
     * 配置保存类
     */
    public static abstract class AsxConfig {

        @SuppressWarnings("rawtypes")
        public static final List<String> getConfigs(Class clzz) {
            List<String> list = new ArrayList<>();
            for (File file : AsxFile.get(clzz.getName().replaceAll("\\.", "/")).getFiles()) {
                list.add(file.getName().replace(".yml", ""));
            }
            return list;
        }

        protected FileConfiguration customConfig;
        private File customConfigFile;
        private String path;
        private final String fileName;

        private AsxFile asxFile; // 类所在文件夹路径

        public AsxConfig(String fileName) {
            this.fileName = fileName + ".yml";
            asxFile = AsxFile.get(this.getClass().getName().replaceAll("\\.", "/"));
            path = asxFile.getDirStr();
            setCustomConfigFile(new File(path, this.fileName));
            if (!hasConfig()) {
                AsxFile.createFile(this);
                this.customConfig = getConfig();
                save();
            } else {
                this.customConfig = getConfig();
            }
            loadConfig(this.customConfig);
        }

        public FileConfiguration getConfig() {
            if (this.customConfig == null) {
                this.customConfig = AsxFile.getConfig(this.customConfigFile);
            }
            return this.customConfig;
        }

        public File getCustomConfigFile() {
            return this.customConfigFile;
        }

        public void remove() {
            AsxFile.removeFile(this.customConfigFile);
        }

        public void setCustomConfigFile(File customConfigFile) {
            this.customConfigFile = customConfigFile;
        }

        /**
         * 更新文件时使用
         */
        public void update() {
            saveConfig(this.customConfig);
            AsxFile.saveConfig(this.customConfig, this.customConfigFile);
        }

        private boolean hasConfig() {
            return this.customConfigFile.exists();
        }

        private void save() {
            defaultValue();
            saveConfig(this.customConfig);
            AsxFile.saveConfig(this.customConfig, this.customConfigFile);
        }

        /**
         * 定义保存的属性默认值
         */
        protected abstract void defaultValue();

        /**
         * 定义属性读取方法
         */
        protected abstract void loadConfig(FileConfiguration config);

        /**
         * 定义属性保存方法
         */
        protected abstract void saveConfig(FileConfiguration config);
    }

    public static final class AsxFile {
        private static File dataFolder;

        public static void createFile(AsxConfig asxConfig) {
            File configFile = asxConfig.getCustomConfigFile();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取配置文件对象的文件夹
         */
        public static AsxFile get(String name) {
            return new AsxFile(name);
        }

        /**
         * 获取完整 YamlConfiguration 实例
         */
        public static YamlConfiguration getConfig(File file) {
            /*
             * 获取配置文件 输入 file 完整路径名
             */
            return YamlConfiguration.loadConfiguration(file);
        }

        /**
         * 移除某个文件
         */
        public static boolean removeFile(File file) {
            return file.delete();
        }

        public static boolean saveConfig(FileConfiguration customConfig, File customConfigFile) {
            try {
                customConfig.save(customConfigFile);
            } catch (IOException e) {
                Msg.sendConPlugin(customConfigFile.getName() + "保存失败！");
                return false;
            }
            return true;
        }

        public static void setPlugin() {
            dataFolder = plugin.getDataFolder();
        }

        private File filedir;

        private AsxFile(String dir) {
            filedir = new File(dataFolder.toString() + "/" + dir);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
        }

        /**
         * 获取文件夹地址
         */
        public String getDirStr() {
            return filedir.toString();
        }

        public File[] getFiles() {
            return filedir.listFiles();
        }
    }

    /**
     * 返回按钮
     */
    public static class BackButton extends Button<Gui> {
        public static BackButton getButton(final APlugin.Gui gui) {
            BackButton backButton = new BackButton(gui);
            backButton.setClickListener(new APlugin.LeftClickListener() {
                public void leftClick() {
                    APlugin.GuiBase.openWindow(gui.getOwner(), gui.getBeforeGui());
                }
            });
            return backButton;
        }

        private BackButton(APlugin.Gui gui) {
            super(gui);
        }

        protected String buttonName() {
            return "返回";
        }

        protected String explain() {
            return "§b返回上一页";
        }

        @SuppressWarnings("deprecation")
        protected Material material() {
            return Material.getMaterial(347);
        }
    }

    /**
     * 按钮抽象类
     */
    public static abstract class Button<T> extends AssemblyDynamic<T> {
        public Button(T gui) {
            super(gui);
        }

        protected abstract String buttonName();

        protected abstract String explain();

        protected void init(T gui, ItemMeta itemMeta) {
            setTitle("§b[" + buttonName() + "§b]");
            setLore(Arrays.asList(explain().split("\\.n")));
        }

        protected short secondID() {
            return 0;
        }
    }

    /**
     * 命令抽象类
     */
    public static abstract class Command {
        private static Map<String, Command> commandList = new HashMap<>();

        /**
         * 添加命令
         */
        public final static void addCommand(Command command) {
            commandList.put(command.getName(), command);
        }

        /**
         * 获取命令列表
         */
        public final static Map<String, Command> getCommands() {
            return commandList;
        }

        /**
         * 发送可用命令列表
         */
        public final static void sendCmdShow(Player player) {
            for (Command command : Command.commandList.values()) {
                if (command.op && !player.isOp()) {
                    continue;
                }
                command.cmdUsage(player, command);
            }
        }

        /**
         * 命令名字
         */
        protected String name;

        /**
         * 命令参数长度
         */
        protected int len;

        /**
         * 命令使用方法
         */
        protected String usage;

        /**
         * 命令介绍
         */
        protected String desc;
        /**
         * 命令介绍
         */
        protected boolean op;

        /**
         * 创建命令类 name 命令字符串 len 命令参数长度
         */
        public Command(String name, int len, String usage, String desc, boolean op) {
            this.name = name;
            this.len = len;
            this.usage = usage;
            this.desc = desc;
            this.op = op;
            addCommand(this);
        }

        /**
         * 发送当前命令使用方式
         */
        final void cmdUsage(Player player, Command command) {
            Msg.sendMessage(player, Msg.getPluginName() + APlugin.cmdName + " " +
                    command.name + " " + this.usage + "  " + this.desc);
        }

        /**
         * 获取命令介绍
         */
        public final String getDesc() {
            return desc;
        }

        /**
         * 获取命令参数长度
         */
        public final int getLen() {
            return len;
        }

        /**
         * 获取命令名
         */
        public final String getName() {
            return name;
        }

        /**
         * 获取是否op可用
         */
        public final boolean getOp() {
            return op;
        }

        /**
         * 获取命令使用方法
         */
        public final String getUsage() {
            return usage;
        }

        /**
         * 玩家使用命令 args 命令参数 返回false为命令使用方式错误
         */
        public abstract boolean send(Player player, String[] args);

        protected void sendFalseMsg(CommandSender sender, String message) {
            Msg.sendMsgFalse(sender, message);
        }

        protected void sendTrueMsg(CommandSender sender, String message) {
            Msg.sendMsgTrue(sender, message);
        }
    }

    /**
     * 命令监听器
     */
    public static class CommandListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void _a(PlayerCommandPreprocessEvent e) {
            String cmd = e.getMessage();
            String[] cmdargs = cmd.toLowerCase().split(" ");
            if (!cmdargs[0].equals(APlugin.cmdName)) {
                return;
            }
            Player ePlayer = e.getPlayer();
            if (cmdargs.length == 1) {
                Msg.sendMsgTrue(ePlayer, "可用命令如下:");
                Command.sendCmdShow(ePlayer);
                e.setCancelled(true);
                return;
            }
            if (!Command.commandList.containsKey(cmdargs[1])) {
                Command.sendCmdShow(ePlayer);
                e.setCancelled(true);
                return;
            }
            Command command = Command.commandList.get(cmdargs[1]);
            if (command.op && !ePlayer.isOp()) {
                return;
            }
            if (!(command.len == cmdargs.length - 2)) {
                command.cmdUsage(ePlayer, command);
                e.setCancelled(true);
                return;
            }
            if (command.len == 0) {
                if (!command.send(ePlayer, null)) {
                    command.cmdUsage(ePlayer, command);
                }
                e.setCancelled(true);
                return;
            }
            if (!command.send(ePlayer, Arrays.copyOfRange(cmdargs, 2, cmdargs.length))) {
                command.cmdUsage(ePlayer, command);
            }
            e.setCancelled(true);
            return;
        }
    }

    public static abstract class CorePlugin extends JavaPlugin {
        /**
         * 监听器注册
         */
        public static APlugin regListener(Listener listener) {
            pluginManager.registerEvents(listener, plugin);
            return null;
        }

        /**
         * 注册命令 new E() extend Command ;
         */
        public abstract void command();

        /**
         * 处理插件完成时控制台提示
         *
         * @param serverSender
         */
        public abstract String[] consoleLog(ConsoleCommandSender serverSender);

        /**
         * 事件注册 regListener(Listener listener)
         */
        public abstract void listenter();

        public final void onDisable() {

        }

        public final void onEnable() {
            pluginName = "§a[" + pluginName() + "]§e";
            serverName = "§b[" + serverName() + "公告]§e";
            cmdName = "/" + pluginCommand();
            plugin = this;
            pluginManager = getServer().getPluginManager();
            serverSender = getServer().getConsoleSender();
            AsxFile.setPlugin();
            start();
            listenter();
            command();
            regListener(new CommandListener());
            regListener(new GuiBase());
            String[] logArray = consoleLog(serverSender);
            if (logArray != null) {
                serverSender.sendMessage(logArray);
            }
        }

        /**
         * 插件主命令名字 不用带斜杠
         */
        public abstract String pluginCommand();

        /**
         * 插件中文名字 用于发送消息
         */
        public abstract String pluginName();

        /**
         * 服务端中文名 用于发送公告
         */
        public abstract String serverName();

        /**
         * 插件入口 启动提示 插件实例变量 等已经获取
         */
        public abstract void start();
    }

    /**
     * 窗口类
     */
    public static abstract class Gui {
        protected static Map<Inventory, Gui> invtable = new HashMap<>();

        public static Gui getGui(Inventory inv) {
            return invtable.get(inv);
        }

        public static boolean isGui(Inventory inv) {
            return invtable.containsKey(inv);
        }

        private Map<int[], String> regionMap = new HashMap<>();

        /**
         * 从哪个窗口跳转过来
         */
        private Gui beforeGui;

        /**
         * 窗口所用的背包实体
         */
        private Inventory i;

        /**
         * 窗口的拥有者 玩家
         */
        private Player owner;
        /**
         * 窗口的大小等级
         */
        private int level;
        /**
         * 窗口的所有者名字
         */
        private String ownerName;
        /**
         * 允许放入物品
         */
        private boolean can;

        private Map<Integer, AssemblyClickListener> assemblyClick = new HashMap<>(); // 动态组件 点击监听列表
        @SuppressWarnings("rawtypes")
        private Map<Integer, AssemblyFixed> assemblyMap = new HashMap<>(); // 固定组件 只做显示 不实现任何功能
        @SuppressWarnings("rawtypes")
        private Map<Integer, AssemblyDynamic> assemblyMap2 = new HashMap<>(); // 动态组件 实现点击动态显示等功能

        /**
         * 创建窗口过程
         */
        public Gui(Gui beforeGui, Player owner, String title, int lv) {
            setBeforeGui(beforeGui);
            setOwner(owner);
            level = lv * 9;
            i = Bukkit.createInventory(null, level, title);
            invtable.put(i, this);
            initWindow();
            drawWindow();
        }

        /**
         * 创建窗口过程
         */
        public Gui(Player owner, String title, int lv) {
            setOwner(owner);
            level = lv * 9;
            i = Bukkit.createInventory(null, level, title);
            invtable.put(i, this);
            initWindow();
            drawWindow();
        }

        /**
         * 添加区域
         */
        public void addRegion(int x, int y, int dx, int dy, String regionName) {
            regionMap.put(new int[]{x, y, dx, dy}, regionName);
        }

        public boolean clickRegion(int rslot, ClickType clickType, ItemStack itemStack) {
            int num = rslot + 1;
            int clickX = num % 9;
            int clickY = num / 9 + 1;
            for (Entry<int[], String> entry : regionMap.entrySet()) {
                int[] location = entry.getKey();
                if ((clickX >= location[0] && clickX <= location[2] && clickY >= location[1]
                        && clickY <= location[3])) {
                    return clickRegion(entry.getValue(), clickType, itemStack);
                }
            }
            return false;
        }

        /**
         * 区域判断
         */
        public abstract boolean clickRegion(String clickedRegionName, ClickType clickType, ItemStack itemStack);

        /**
         * 窗口关闭事件 返回true则清除
         */
        public abstract boolean closeEvent();

        /**
         * 删除区域 name
         */
        public void delRegion(String regionName) {
            for (Entry<int[], String> entry : regionMap.entrySet()) {
                if (Objects.equals(entry.getValue(), regionName)) {
                    regionMap.remove(entry.getKey());
                }
            }
        }

        /**
         * 窗口绘制方法 用于绘制边框等不变项目 只在窗口创建后调用一次
         */
        @SuppressWarnings("rawtypes")
        public void drawWindow() {
            for (Entry<Integer, AssemblyFixed> entry : assemblyMap.entrySet()) {
                i.setItem(entry.getKey().intValue(), entry.getValue().getItemStack());
            }
            for (Entry<Integer, AssemblyDynamic> entry : assemblyMap2.entrySet()) {
                int key = entry.getKey();
                AssemblyDynamic value = entry.getValue();
                i.setItem(key, value.getItemStack());
                AssemblyClickListener listener = value.getClickListener();
                if (listener != null) {
                    // 添加点击监听
                    assemblyClick.put(key, listener);
                }
            }
        }

        /**
         * 获取父窗口
         */
        public Gui getBeforeGui() {
            return beforeGui;
        }

        public Inventory getInventory() {
            return i;
        }

        public ItemStack getItemStack(int x, int y) {
            Integer key = x - 1 + (y - 1) * 9;
            return i.getItem(key);
        }

        /**
         * 获取窗口大小等级
         */
        public int getLevel() {
            return level;
        }

        /**
         * 当前gui主人
         */
        public Player getOwner() {
            return owner;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public List<ItemStack> getReigonItems(String regionName) {
            List<ItemStack> itemList = new ArrayList<>();
            for (Entry<int[], String> entry : regionMap.entrySet()) {
                if (Objects.equals(regionName, entry.getValue())) {
                    int[] location = entry.getKey();
                    for (int i = location[0]; i <= location[2]; i++) {
                        for (int j = location[1]; j <= location[3]; j++) {
                            ItemStack itemStack = getItemStack(i, j);
                            if (itemStack != null && itemStack.getType() != Material.AIR) {
                                itemList.add(itemStack);
                            }
                        }
                    }
                    break;
                }
            }
            return itemList;
        }

        public boolean hasBeforeGui() {
            return this.beforeGui != null;
        }

        public boolean isCan() {
            return can;
        }

        /**
         * 打开Gui
         */
        public void open() {
            getOwner().closeInventory();
            getOwner().openInventory(i);
        }

        /**
         * 移除窗口动态组件
         */
        public void removeAssembly(int x, int y) {
            if (x < 1 || y < 1 || x > 9 || y > 6) {
                Msg.sendConMsgFalse("组件坐标超出Gui最大值");
                return;
            }
            Integer key = x - 1 + (y - 1) * 9;
            if (assemblyMap2.containsKey(key)) {
                // 动态组件存在时
                assemblyMap2.remove(key);
                assemblyClick.remove(key);
                i.setItem(key, null);
            }
        }

        /**
         * 设置窗口动态组件
         */
        @SuppressWarnings("rawtypes")
        public void setAssembly(int index, AssemblyDynamic assembly) {
            if (index < 0 || index > 53) {
                Msg.sendConMsgFalse("组件坐标超出Gui最大值");
                return;
            }
            if (!assembly.isFinish()) {
                Msg.sendConMsgFalse("尝试将一个未完整创建的组件添加到Gui上,添加失败,组件初始化完毕请执行finish()确认创建。");
                return;
            }
            this.assemblyMap2.put(index, assembly);
            this.i.setItem(index, assembly.getItemStack());
            if (assembly.getClickListener() != null) {
                // 添加点击监听
                this.assemblyClick.put(index, assembly.getClickListener());
            }
            return;
        }

        /**
         * 设置窗口动态组件
         */
        @SuppressWarnings("rawtypes")
        public void setAssembly(int x, int y, AssemblyDynamic assembly) {
            if (x < 1 || y < 1 || x > 9 || y > 6) {
                Msg.sendConMsgFalse("组件坐标超出Gui最大值");
                return;
            }
            if (!assembly.isFinish()) {
                Msg.sendConMsgFalse("尝试将一个未完整创建的组件添加到Gui上,添加失败,组件初始化完毕请执行finish()确认创建。");
                return;
            }
            int key = x - 1 + (y - 1) * 9;
            this.assemblyMap2.put(key, assembly);
            this.i.setItem(key, assembly.getItemStack());
            if (assembly.getClickListener() != null) {
                // 添加点击监听
                this.assemblyClick.put(key, assembly.getClickListener());
            }
            return;
        }

        /**
         * 设置窗口固定组件
         */
        @SuppressWarnings("rawtypes")
        public void setAssembly(int x, int y, AssemblyFixed assembly) {
            if (x < 1 || y < 1 || x > 9 || y > 6) {
                Msg.sendConMsgFalse("组件坐标超出Gui最大值");
                return;
            }
            if (!assembly.isFinish()) {
                Msg.sendConMsgFalse("尝试将一个未完整创建的组件添加到Gui上,添加失败,组件初始化完毕请执行finish()确认创建。");
                return;
            }
            assemblyMap.put(x - 1 + (y - 1) * 9, assembly);
        }

        /**
         * 设置窗口固定组件
         */
        public void setAssembly(int x, int y, ItemStack itemStack) {
            if (x < 1 || y < 1 || x > 9 || y > 6) {
                Msg.sendConMsgFalse("组件坐标超出Gui最大值");
                return;
            }
            i.setItem(x - 1 + (y - 1) * 9, itemStack);
        }

        public void setBeforeGui(Gui beforeGui2) {
            this.beforeGui = beforeGui2;
        }

        public void setCan(boolean can) {
            this.can = can;
        }

        public void setInventory(Inventory inventory) {
            this.i = inventory;
        }

        public void setOwner(Player owner) {
            this.owner = owner;
            this.ownerName = owner.getName();
        }

        /**
         * 窗口更新方法 每次打开窗口都会调用 用来添加动态按钮
         */
        public abstract void updateWindow();

        /**
         * <p>
         * 窗口初始化 添加组件 setAssembly()
         * </p>
         * <p>
         * addRegion(String regionName)添加区域
         * </p>
         */
        protected abstract void initWindow();
    }

    /**
     * 窗口监听类
     */
    public static class GuiBase implements Listener {
        private static Map<Player, Gui> windowlist = new HashMap<>();

        public static void closeWindow(Player player) {
            player.closeInventory();
        }

        public static Gui getWindow(Player player, Gui gui) {
            windowlist.put(player, gui);
            return windowlist.get(player);
        }

        /**
         * 打开窗口
         */
        public static void openWindow(Player owner, Gui gui) {
            if (gui == null) {
                owner.closeInventory();
                return;
            }
            Gui guik = getWindow(owner, gui);
            guik.updateWindow();
            guik.open();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void test(InventoryClickEvent e) {
            if (Gui.isGui(e.getInventory())) { // 判断是否为ASX创建的GUI
                boolean isCan = true;
                Gui gui = Gui.getGui(e.getInventory()); // 获取这个GUI
                Integer rslot = e.getRawSlot(); // 获取点击的原始坐标
                if (Objects.equals(gui.getOwner(), (Player) e.getWhoClicked())) { // 点击者为GUI所有者
                    if (gui.assemblyClick.containsKey(rslot)) {
                        AssemblyClickListener assemblyClickListener = gui.assemblyClick.get(rslot);
                        if (e.isLeftClick()) {
                            if (e.isShiftClick()) {
                                if (assemblyClickListener instanceof LeftShiftClickListener) {
                                    ((LeftShiftClickListener) assemblyClickListener).leftShiftClick();
                                }
                                if (assemblyClickListener instanceof AllClickListener) {
                                    ((AllClickListener) assemblyClickListener).leftShiftClick();
                                }
                            } else {
                                if (assemblyClickListener instanceof LeftClickListener) {
                                    ((LeftClickListener) assemblyClickListener).leftClick();
                                }
                                if (assemblyClickListener instanceof AllClickListener) {
                                    ((AllClickListener) assemblyClickListener).leftClick();
                                }
                            }
                        } else {
                            if (e.isShiftClick()) {
                                if (assemblyClickListener instanceof RightShiftClickListener) {
                                    ((RightShiftClickListener) assemblyClickListener).rightShiftClick();
                                }
                                if (assemblyClickListener instanceof AllClickListener) {
                                    ((AllClickListener) assemblyClickListener).rightShiftClick();
                                }
                            } else {
                                if (assemblyClickListener instanceof RightClickListener) {
                                    ((RightClickListener) assemblyClickListener).rightClick();
                                }
                                if (assemblyClickListener instanceof AllClickListener) {
                                    ((AllClickListener) assemblyClickListener).rightClick();
                                }
                            }
                        }
                        isCan = !gui.isCan();
                    } else {
                        isCan = !gui.clickRegion(rslot, e.getClick(), e.getCurrentItem()); // 无事件禁止点击
                    }
                }
                e.setCancelled(isCan);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void test(InventoryCloseEvent e) {
            if (Gui.isGui(e.getInventory())) {
                Gui gui = Gui.getGui(e.getInventory());
                if (gui.closeEvent()) {
                    windowlist.remove(gui.getOwner());
                }
            }
        }
    }

    /**
     * 组件点击监听接口
     */
    public static interface LeftClickListener extends AssemblyClickListener {

        void leftClick();
    }

    /**
     * 组件点击监听接口
     */
    public static interface LeftShiftClickListener extends AssemblyClickListener {

        void leftShiftClick();
    }

    /**
     * 信息处理
     */
    public final static class Msg {

        /**
         * 获取插件名字
         */
        public static String getPluginName() {
            return pluginName;
        }

        /**
         * 获取服务器名字
         */
        public static String getServerName() {
            return serverName;
        }

        /**
         * 给后台发送红色消息
         */
        public static Msg sendConMsgFalse(String msg) {
            serverSender.sendMessage("§8[§a!§8] §c" + msg);
            return null;
        }

        /**
         * 给后台发送绿色消息
         */
        public static Msg sendConMsgTrue(String msg) {
            serverSender.sendMessage("§8[§a!§8] §a" + msg);
            return null;
        }

        /**
         * 以插件名义发送后台信息
         */
        public static Msg sendConPlugin(String msg) {
            APlugin.serverSender.sendMessage(pluginName + msg);
            return null;
        }

        /**
         * 给*发送测试信息
         */
        public static Msg sendCs(CommandSender sender, String msg) {
            if (sender != null) {
                sender.sendMessage("[测试]" + msg);
            }
            return null;
        }

        /**
         * 给*发送普通信息
         */
        public static Msg sendMessage(CommandSender sender, String msg) {
            if (sender != null) {
                sender.sendMessage("§a" + msg);
            }
            return null;
        }

        /**
         * 给*发送白色系统提示
         */
        public static Msg sendMsg(CommandSender sender, String msg) {
            if (sender != null) {
                sender.sendMessage("§8[§a!§8] §f" + msg);
            }
            return null;
        }

        /**
         * 给*发送红色系统提示
         */
        public static Msg sendMsgFalse(CommandSender sender, String msg) {
            if (sender != null) {
                sender.sendMessage("§8[§a!§8] §c§n" + msg);
            }
            return null;
        }

        /**
         * 给*发送绿色系统提示
         */
        public static Msg sendMsgTrue(CommandSender sender, String msg) {
            if (sender != null) {
                sender.sendMessage("§8[§a!§8] §a§n" + msg);
            }
            return null;
        }

        /**
         * 给*发送绿色json消息
         */
        public static Msg sendJsonTrue(Player sender, TextComponent... textComponents) {
            TextComponent baseTextComponent = new TextComponent("§8[§a!§8] §e");
            for (TextComponent component : textComponents) {
                baseTextComponent.addExtra(component);
                baseTextComponent.addExtra("§e");
            }
            sender.spigot().sendMessage(baseTextComponent);
            return null;
        }

        /**
         * 以插件名义发送公告
         */
        public static Msg sendPluginBroad(String msg) {
            Bukkit.broadcastMessage(pluginName + msg);
            return null;
        }

        /**
         * 以插件名义发送信息
         */
        public static Msg sendPluginMsg(CommandSender sender, String msg) {
            sender.sendMessage(pluginName + msg);
            return null;
        }

        /**
         * 以服务器名义发送公告
         */
        public static Msg sendServerBroad(String msg) {
            Bukkit.broadcastMessage(serverName + msg);
            return null;
        }
    }

    /**
     * 玩家相关变量容器
     */
    public static class PlayerCollection<T extends PlayerVariable> {

        private final String modelName;
        private final Map<String, T> map = new HashMap<>();

        /**
         * 取出前先使用has()判断玩家是不是存在 不存在需要先使用add()创建
         */
        public PlayerCollection(String modelName) {
            this.modelName = modelName;
        }

        /**
         * 将玩家变量添加到此容器内
         */
        public void add(T t) {
            map.put(t.getPlayerName(), t);
        }

        /**
         * 取出前先判断有没有 没有需要先创建
         */
        public T get(String playerName) {
            if (map.containsKey(playerName)) {
                return map.get(playerName);
            }
            return null;
        }

        public String getModelName() {
            return modelName;
        }

        /**
         * 判断玩家变量是否存在
         */
        public boolean has(String playerName) {
            return map.containsKey(playerName);
        }
    }

    /**
     * 玩家变量类 使用setValue(String path, Object value) 方式设置值时自动保存，单次更新数据使用此方法就可以了
     * 如需要密集更新数据建议直接对customConfig操作后调用update()保存数据
     */
    public static abstract class PlayerVariable extends AsxConfig {
        private String playerName;

        public PlayerVariable(String fileName) {
            super(fileName);
            playerName = fileName;
        }

        public void getIntValue(String path) {
            customConfig.getInt(path, 0);
        }

        public String getPlayerName() {
            return playerName;
        }

        /**
         * 获取值
         */
        public void getStringValue(String path) {
            customConfig.getString(path, "");
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        /**
         * 使用此方式设置值时自动保存，单次更新数据使用此方法就可以了 如需要密集更新数据建议直接对customConfig修改后调用update()保存数据
         */
        public void setValue(String path, Object value) {
            customConfig.set(path, value);
            update();
        }

        @Override
        protected void defaultValue() {

        }

        @Override
        protected void loadConfig(FileConfiguration config) {

        }

        @Override
        protected void saveConfig(FileConfiguration config) {

        }
    }

    /**
     * 组件点击监听接口
     */
    public static interface RightClickListener extends AssemblyClickListener {

        void rightClick();

    }

    /**
     * 组件点击监听接口
     */
    public static interface RightShiftClickListener extends AssemblyClickListener {

        void rightShiftClick();
    }

    /**
     * 工具类
     */
    public final static class Util {

        /**
         * 时间相关工具
         */
        public static class DateTool {

            @SuppressWarnings("deprecation")
            public static final String getDateString() {
                Date date = new Date();
                return String.format("%s%s%s", date.getYear(), date.getMonth(), date.getDate());
            }

        }

        /**
         * 计算工具
         */
        public final static class Math {

            /**
             * 小数保留两位
             */
            public final static double getFormat(double d) {
                return ((int) (d * 100D)) / 100D;
            }

            /**
             * 计算按百分比返回boolean
             */
            public final static boolean percentage(double base, double max) {
                return max * java.lang.Math.random() < base;
            }

            /**
             * 计算按百分比返回boolean
             */
            public final static boolean percentage(double base) {
                return 100D * java.lang.Math.random() < base;
            }
        }

        /**
         * 粒子特效类
         */
        public static abstract class Particle {
            LivingEntity livingEntity;
            protected double range;
            protected double hight;
            protected Effect effect;
            private int delay;

            public Particle(final LivingEntity livingEntity, double range, double hight, Effect effect, int delay) {
                this.livingEntity = livingEntity;
                this.range = range;
                this.effect = effect;
                this.delay = delay;
                this.hight = hight;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!livingEntity.isDead()) {
                            showEffect(livingEntity.getLocation());
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskLater(plugin, this.delay);
            }

            /**
             * 绘制粒子
             */
            protected void paintParticles(double x, double y, double z) {
                Location Loc = livingEntity.getLocation().add(x, y, z);
                Loc.getWorld().playEffect(Loc, effect, 0);
            }

            /**
             * 绘制烟花
             */
            protected void paintParticles(double x, double y, double z, Color ys) {
                Location Loc = livingEntity.getLocation().add(x, y, z);
                FireworkEffect.Builder builder = FireworkEffect.builder();
                builder.with(FireworkEffect.Type.BALL);
                builder.withColor(ys);
                FireworkEffect effect = builder.build();
                Firework firework = (Firework) Loc.getWorld().spawnEntity(Loc, EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffects(effect);
                meta.setPower(0);
                firework.setFireworkMeta(meta);
                firework.detonate();
            }

            protected abstract void showEffect(Location loc);
        }

        /**
         * 玩家操作工具
         */
        public final static class PlayerUtil {
            /**
             * 玩家执行命令工具
             */
            public final static boolean carryCommandOfConsole(final Player player, final List<String> cmds) {
                try {
                    for (String line : cmds) {
                        Bukkit.dispatchCommand(APlugin.serverSender, line.replaceAll("\\{player\\}", player.getName()));
                    }
                } catch (Exception e) {

                }
                return true;
            }

            /**
             * 玩家执行命令工具
             */
            public final static boolean carryCommandOfPlayer(final Player player, final List<String> cmds) {
                try {
                    for (String line : cmds) {
                        Bukkit.dispatchCommand(player, "/" + line.replaceAll("\\{player\\}", player.getName()));
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            /**
             * 执行后台命令
             */
            public final static boolean carryConsoleCommand(final String command) {
                try {
                    Bukkit.dispatchCommand(APlugin.serverSender, command);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            /**
             * 多个物品同时使用
             */
            public final static boolean costItem(final Player player, final Map<String, Integer> map) {
                Map<String, Integer> map2 = new HashMap<>();
                Inventory inventory = player.getInventory();
                ItemStack[] contents = inventory.getContents();
                for (ItemStack itemStack : contents) {
                    if (itemStack != null && itemStack.hasItemMeta()) {
                        ItemMeta meta = itemStack.getItemMeta();
                        if (meta.hasDisplayName()) {
                            if (map.containsKey(meta.getDisplayName())) {
                                if (itemStack.getAmount() > 0) {
                                    if (map2.containsKey(meta.getDisplayName())) {
                                        map2.put(meta.getDisplayName(),
                                                map2.get(meta.getDisplayName()) + itemStack.getAmount());
                                    } else {
                                        map2.put(meta.getDisplayName(), itemStack.getAmount());
                                    }
                                }
                            }
                        }
                    }
                }

                for (Entry<String, Integer> entry : map.entrySet()) {
                    if (map2.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                        return false;
                    }
                }

                for (int i = 0; i < 40; i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack != null && itemStack.hasItemMeta()) {
                        ItemMeta meta = itemStack.getItemMeta();
                        if (meta.hasDisplayName()) {
                            String displayName = meta.getDisplayName();
                            if (map.containsKey(displayName)) {
                                int num = map.get(displayName);
                                int rawNum = itemStack.getAmount();
                                if (num >= rawNum) {
                                    inventory.setItem(i, null);
                                    num -= rawNum;
                                } else {
                                    rawNum -= num;
                                    num = 0;
                                    itemStack.setAmount(rawNum);
                                    inventory.setItem(i, itemStack);
                                }
                                if (num == 0) {
                                    map.remove(displayName);
                                } else {
                                    map.put(displayName, num);
                                }
                            }
                        }
                    }
                }

                return true;
            }

            /**
             * 使用名字为的物品一定数量
             */
            public final static boolean costItem(final Player player, final String itemName, int num) {
                ItemStack[] contents = player.getInventory().getContents();
                int sy = 0;
                for (ItemStack itemStack : contents) {
                    if (itemStack != null && itemStack.hasItemMeta()) {
                        ItemMeta meta = itemStack.getItemMeta();
                        if (meta.hasDisplayName()) {
                            if (meta.getDisplayName().equals(itemName)) {
                                if (itemStack.getAmount() > 0) {
                                    sy += itemStack.getAmount();
                                }
                            }
                        }
                    }
                }
                if (sy >= num) {
                    for (ItemStack itemStack : contents) {
                        if (itemStack != null && itemStack.hasItemMeta()) {
                            ItemMeta meta = itemStack.getItemMeta();
                            if (meta.hasDisplayName()) {
                                if (meta.getDisplayName().equals(itemName)) {
                                    if (itemStack.getAmount() >= num) {
                                        itemStack.setAmount(itemStack.getAmount() - num);
                                        player.getInventory().setContents(contents);
                                        return true;
                                    } else if (itemStack.getAmount() > 0) {
                                        num -= itemStack.getAmount();
                                        itemStack.setAmount(0);
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }

            /**
             * 查看玩家背包剩余数量
             *
             * @return
             */
            public final static int getNullSoltNumber(final Player owner) {
                int slot = 0;
                for (ItemStack itemStack : owner.getInventory().getContents()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        slot++;
                    }
                }
                return slot;
            }
        }

        public final static class ToStringUtil {
            private static final Map<String, String> map = new HashMap<>();

            static {
                map.put("DROPPED_ITEM", "掉落物");
                map.put("EXPERIENCE_ORB", "经验球");
                map.put("LEASH_HITCH", "栓绳");
                map.put("PAINTING", "画");
                map.put("ARROW", "箭");
                map.put("SNOWBALL", "雪球");
                map.put("FIREBALL", "火球");
                map.put("SMALL_FIREBALL", "小火球");
                map.put("ENDER_PEARL", "末影珍珠");
                map.put("ENDER_SIGNAL", "末影龙信号");
                map.put("THROWN_EXP_BOTTLE", "丢出去的经验瓶");
                map.put("ITEM_FRAME", "物品展示框");
                map.put("WITHER_SKULL", "骷髅头");
                map.put("PRIMED_TNT", "燃着的TNT");
                map.put("FALLING_BLOCK", "下落的方块");
                map.put("FIREWORK", "烟花");
                map.put("MINECART_COMMAND", "命令方块");
                map.put("BOAT", "船");
                map.put("MINECART", "矿车");
                map.put("MINECART_CHEST", "箱子矿车");
                map.put("MINECART_FURNACE", "熔炉矿车");
                map.put("MINECART_TNT", "TNT矿车");
                map.put("MINECART_HOPPER", "漏斗矿车");
                map.put("MINECART_MOB_SPAWNER", "刷怪笼");
                map.put("CREEPER", "爬行者");
                map.put("SKELETON", "骷髅");
                map.put("SPIDER", "蜘蛛");
                map.put("GIANT", "巨人");
                map.put("ZOMBIE", "僵尸");
                map.put("SLIME", "史莱姆");
                map.put("GHAST", "恶魂");
                map.put("PIG_ZOMBIE", "猪僵尸");
                map.put("ENDERMAN", "末影人");
                map.put("CAVE_SPIDER", "洞穴蜘蛛");
                map.put("SILVERFISH", "蠹虫");
                map.put("BLAZE", "烈焰人");
                map.put("MAGMA_CUBE", "岩浆怪");
                map.put("ENDER_DRAGON", "末影龙");
                map.put("WITHER", "凋零");
                map.put("BAT", "蝙蝠");
                map.put("WITCH", "女巫");
                map.put("PIG", "猪");
                map.put("SHEEP", "羊");
                map.put("COW", "牛");
                map.put("CHICKEN", "鸡");
                map.put("SQUID", "鱿鱼");
                map.put("WOLF", "狼");
                map.put("MUSHROOM_COW", "蘑菇牛");
                map.put("SNOWMAN", "雪人");
                map.put("OCELOT", "豹猫");
                map.put("IRON_GOLEM", "铁傀儡");
                map.put("HORSE", "马");
                map.put("VILLAGER", "村民");
                map.put("ENDER_CRYSTAL", "末影水晶");
                map.put("SPLASH_POTION", "飞溅出去的药水");
                map.put("EGG", "鸡蛋");
                map.put("FISHING_HOOK", "钓鱼钩");
                map.put("LIGHTNING", "闪电");
                map.put("WEATHER", "天气");
                map.put("PLAYER", "玩家");
            }

            public static String getEntityTypeName(String typeString) {
                return map.getOrDefault(typeString, "天界奇物");
            }
        }
    }

    // 设置数据表
    public interface TableMap {
        Map<String, String> init(Map<String, String> map);
    }

    /**
     * 数据库初始化类
     */
    public static abstract class DataBase {

        private static String ip;
        private static String port;
        private static String dbName;
        private static String user;
        private static String password;
        private static Connection conn;
        private static BukkitRunnable br;
        private static String sql;
        private static String table;
        private static TableMap tableMap;

        @NotNull
        public static DataBase setIp(String ip) {
            DataBase.ip = ip;
            return null;
        }

        @NotNull
        public static DataBase setPort(String port) {
            DataBase.port = port;
            return null;
        }

        @NotNull
        public static DataBase setDBName(String dbName) {
            DataBase.dbName = dbName;
            return null;
        }

        @NotNull
        public static DataBase setUser(String user) {
            DataBase.user = user;
            return null;
        }

        @NotNull
        public static DataBase setTableMap(TableMap tableMap) {
            DataBase.tableMap = tableMap;
            return null;
        }

        @NotNull
        public static DataBase setPassword(String password) {
            DataBase.password = password;
            return null;
        }

        @NotNull
        public static DataBase setTable(String tableName) {
            DataBase.table = tableName;
            return null;
        }

        @NotNull
        public static boolean connection() {
            try {

                conn = DriverManager.getConnection(
                        String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                                , ip, port, dbName), user, password);

                sql = "SELECT table_name as TABLENAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
                Map<String, String> map = tableMap.init(new HashMap<>());
                if (br != null) {
                    br.cancel();
                }
                if (conn != null) {
                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setString(1, dbName);
                    ResultSet resultSet = st.executeQuery();
                    List<String> list = new ArrayList<>();
                    while (resultSet.next()) {
                        list.add(resultSet.getString("TABLENAME"));
                    }
                    for (Entry<String, String> entry : map.entrySet()) {
                        if (!list.contains(entry.getKey())) {
                            st.executeUpdate(entry.getValue());
                        }
                    }
                }
                br = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            if (conn != null) {
                                PreparedStatement st = conn.prepareStatement(sql);
                                st.setString(1, dbName);
                                ResultSet resultSet = st.executeQuery();
                                List<String> list = new ArrayList<>();
                                while (resultSet.next()) {
                                    list.add(resultSet.getString("TABLENAME"));
                                }
                                for (Entry<String, String> entry : map.entrySet()) {
                                    if (!list.contains(entry.getKey())) {
                                        st.executeUpdate(entry.getValue());
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            try {
                                conn = DriverManager.getConnection(
                                        String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                                                , ip, port, dbName), user, password);
                            } catch (SQLException ignored2) {
                                Msg.sendMsgFalse(APlugin.serverSender, "数据库连接已经断开,并且重连失败,等待6秒后尝试重连。");
                            }
                        }
                    }
                };
                br.runTaskTimerAsynchronously(APlugin.plugin, 1200, 1200);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class AsxDataBaseMap<T> {

        private static Gson gson = new Gson();

        private String tableName;
        private final Class<T> type;

        public AsxDataBaseMap(String tableName, Class<T> t) {
            this.tableName = tableName;
            this.type = t;
        }

        public String getTableName() {
            return tableName;
        }

        public boolean put(String key, T t) {
            if (containKey(key)) {
                return update(key, t);
            } else {
                return insert(key, t);
            }
        }

        public T get(String key) {
            String json = select(key);
            if (json != null && json.length() > 0) {
                return gson.fromJson(json, type);
            } else {
                return null;
            }
        }

        private String select(String key) {
            try {
                String sql = "SELECT asx_value AS ASXVALUE FROM " + tableName + " WHERE asx_key=?";
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                st.setString(1, key);
                ResultSet resultSet = st.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString("ASXVALUE");
                } else {
                    return "";
                }
            } catch (Exception e) {
                return "";
            }
        }

        public boolean containKey(String key) {
            try {
                String sql = "SELECT COUNT(0) AS HASKEY FROM " + tableName + " WHERE asx_key=?";
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                st.setString(1, key);
                ResultSet resultSet = st.executeQuery();
                resultSet.next();
                return 1 == resultSet.getInt("HASKEY");
            } catch (Exception e) {
                return false;
            }
        }

        private boolean update(String key, T t) {
            try {
                String sql = "UPDATE " + tableName + " SET asx_value=? WHERE asx_key=?";
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                st.setString(1, gson.toJson(t));
                st.setString(2, key);
                int resultSet = st.executeUpdate();
                return resultSet > 0;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean insert(String key, T t) {
            try {
                String sql = "INSERT INTO " + tableName + " (asx_key, asx_value) VALUES(?, ?)";
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                st.setString(1, key);
                st.setString(2, gson.toJson(t));
                int resultSet = st.executeUpdate();
                return resultSet > 0;
            } catch (Exception e) {
                return false;
            }
        }

        public Set<T> getItem() {
            Set<T> set = new HashSet<>();
            try {
                String sql = "SELECT asx_value AS JSON FROM " + tableName;
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    String json = resultSet.getString("JSON");
                    if (json != null && json.length() > 0) {
                        T t = gson.fromJson(json, type);
                        set.add(t);
                    }
                }
            } catch (Exception ignored) {

            }
            return set;
        }

        public boolean remove(String id) {
            try {
                String sql = "DELETE FROM " + tableName + " WHERE asx_key = ?";
                PreparedStatement st = DataBase.conn.prepareStatement(sql);
                st.setString(1, id);
                int result = st.executeUpdate();
                return result > 0;
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}