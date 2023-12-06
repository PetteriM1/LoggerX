package loggerx;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase implements Listener {

    private Config c;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        c = getConfig();
        new Logger(System.getProperty("user.dir") + c.getString("logFile", "/plugins/LoggerX/events.log"),
                System.getProperty("user.dir") + c.getString("archiveLocation", "/plugins/LoggerX/archive"),
                c.getBoolean("archiveOldLogs"));
        if (c.getInt("configVersion") != 5) getLogger().warning("Outdated config! Please delete the old config file to use new features");
        if (c.getBoolean("logLoggerStatus")) Logger.get.print("Logging started: Logger starting up");
    }

    public void onDisable() {
        if (c.getBoolean("logLoggerStatus")) Logger.get.print("Logging stopped: Logger shutting down");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logBreak(BlockBreakEvent e) {
        if (c.getBoolean("logBlockBreak")) {
            Logger.get.print(e.getPlayer().getName() + " broke block " + e.getBlock().getId() + " (" + e.getBlock().getId() + ":" + e.getBlock().getDamage() + ") at [l] [x] [y] [z]", e.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logPlace(BlockPlaceEvent e) {
        if (c.getBoolean("logBlockPlace")) {
            Logger.get.print(e.getPlayer().getName() + " placed block " + e.getBlock().getId() + " (" + e.getBlock().getId() + ":" + e.getBlock().getDamage() + ") at [l] [x] [y] [z]", e.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logDeath(PlayerDeathEvent e) {
        if (c.getBoolean("logPlayerDeath")) {
            Logger.get.print(TextFormat.clean(textFromContainer(e.getDeathMessage())) + " at [l] [x] [y] [z]", e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logJoin(PlayerJoinEvent e) {
        if (c.getBoolean("logPlayerJoin")) {
            Player p = e.getPlayer();
            Logger.get.print(p.getName() + " joined [" + getOS(p) + '|' + p.getLoginChainData().getGameVersion() + '|' + p.getLoginChainData().getLanguageCode() + ']');
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logQuit(PlayerQuitEvent e) {
        if (c.getBoolean("logPlayerQuit")) {
            Logger.get.print(e.getPlayer().getName() + " disconnected: " + e.getReason());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logDrop(PlayerDropItemEvent e) {
        if (c.getBoolean("logItemDrop")) {
            Logger.get.print(e.getPlayer().getName() + " dropped item " + e.getItem().getName() + " (" + e.getItem().getId() + ':' + e.getItem().getDamage() + ':' + e.getItem().getCount() + ") at [l] [x] [y] [z]", e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logFill(PlayerBucketFillEvent e) {
        if (c.getBoolean("logBucketFill")) {
            Logger.get.print(e.getPlayer().getName() + " filled " + e.getBucket().getName() + " at [l] [x] [y] [z]", e.getBlockClicked());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logEmpty(PlayerBucketEmptyEvent e) {
        if (c.getBoolean("logBucketEmpty")) {
            Logger.get.print(e.getPlayer().getName() + " emptied " + e.getBucket().getName() + " at [l] [x] [y] [z]", e.getBlockClicked());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logCommand(PlayerCommandPreprocessEvent e) {
        if (c.getBoolean("logPlayerCommand")) {
            Logger.get.print(e.getPlayer().getName() + " ran command " + e.getMessage() + " at [l] [x] [y] [z]", e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logFire(BlockIgniteEvent e) {
        if (BlockIgniteEvent.BlockIgniteCause.FLINT_AND_STEEL == e.getCause()) {
            if (c.getBoolean("logFire")) {
                Logger.get.print(e.getEntity().getName() + " made a fire at [l] [x] [y] [z]", e.getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logChestOpen(InventoryOpenEvent e) {
        if (e.getInventory() instanceof ContainerInventory) {
            if (c.getBoolean("logContainerOpen")) {
                InventoryHolder holder = e.getInventory().getHolder();
                Location loc;
                if (holder instanceof BlockEntity) {
                    loc = ((BlockEntity) holder).getLocation();
                } else if (holder instanceof Entity) {
                    loc = ((Entity) holder).getLocation();
                } else {
                    return;
                }
                Logger.get.print(e.getPlayer().getName() + " opened " + e.getInventory().getType() + " at [l] [x] [y] [z]", loc);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void logItemFrameDrop(ItemFrameDropItemEvent e) {
        if (c.getBoolean("logItemFrameDrop")) {
            loggerx.Logger.get.print(e.getPlayer().getName() + " dropped " + e.getItem().getName() + " (" + e.getItem().getId() + ':' + e.getItem().getDamage() + ':' + e.getItem().getCount() + ") from item frame at [l] [x] [y] [z]", e.getBlock());
        }
    }

    private String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return getServer().getLanguage().translateString(container.getText(), ((TranslationContainer) container).getParameters());
        } else {
            return container.getText();
        }
    }

    private static String getOS(Player player) {
        switch (player.getLoginChainData().getDeviceOS()) {
            case 1:
                return "Android";
            case 2:
                return "iOS";
            case 3:
                return "macOS";
            case 4:
                return "Fire OS";
            case 5:
                return "Gear VR";
            case 6:
                return "HoloLens";
            case 7:
                return "Windows 10";
            case 8:
                return "Windows";
            case 9:
                return "Dedicated";
            case 10:
                return "tvOS";
            case 11:
                return "PlayStation";
            case 12:
                return "Switch";
            case 13:
                return "Xbox";
            case 14:
                return "Windows Phone";
            default:
                return "Unknown";
        }
    }
}
