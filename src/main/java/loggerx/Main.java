package loggerx;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase implements Listener {

    Config c;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        c = getConfig();
        new Logger(System.getProperty("user.dir") + c.getString("logFile", "/logs/events.log"));
        if (c.getBoolean("logLoggerStatus")) Logger.get.print("Logging started: Logger starting up", null);
    }

    public void onDisable() {
        if (c.getBoolean("logLoggerStatus")) Logger.get.print("Logging stopped: Logger shutting down", null);
    }

    @EventHandler
    public void logBreak(BlockBreakEvent e) {
        if (!e.isCancelled() && c.getBoolean("logBlockBreak")) {
            Logger.get.print(e.getPlayer().getName() + " broke block " + e.getBlock().getName() + " (" + e.getBlock().getId() + ":" + e.getBlock().getDamage() + ") at [l] [x] [y] [z]", e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void logPlace(BlockPlaceEvent e) {
        if (!e.isCancelled() && c.getBoolean("logBlockPlace")) {
            Logger.get.print(e.getPlayer().getName() + " placed block " + e.getBlock().getName() + " (" + e.getBlock().getId() + ":" + e.getBlock().getDamage() + ") at [l] [x] [y] [z]", e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void logDeath(PlayerDeathEvent e) {
        if (!e.isCancelled() && c.getBoolean("logPlayerDeath")) {
            Logger.get.print(TextFormat.clean(textFromContainer(e.getDeathMessage())) + " at [l] [x] [y] [z]", e.getEntity().getLocation());
        }
    }

    @EventHandler
    public void logJoin(PlayerJoinEvent e) {
        if (c.getBoolean("logPlayerJoin")) {
            Logger.get.print(e.getPlayer().getName() + " joined", null);
        }
    }

    @EventHandler
    public void logQuit(PlayerQuitEvent e) {
        if (c.getBoolean("logPlayerQuit")) {
            Logger.get.print(e.getPlayer().getName() + " disconnected: " + e.getReason(), null);
        }
    }

    @EventHandler
    public void logDrop(PlayerDropItemEvent e) {
        if (!e.isCancelled() && c.getBoolean("logItemDrop")) {
            Logger.get.print(e.getPlayer().getName() + " dropped item " + e.getItem().getName() + " (" + e.getItem().getId() + ") at [l] [z] [y] [z]", e.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void logFill(PlayerBucketFillEvent e) {
        if (!e.isCancelled() && c.getBoolean("logBucketFill")) {
            Logger.get.print(e.getPlayer().getName() + " filled bucket at [l] [x] [y] [z]", e.getBlockClicked().getLocation());
        }
    }

    @EventHandler
    public void logEmpty(PlayerBucketEmptyEvent e) {
        if (!e.isCancelled() && c.getBoolean("logBucketEmpty")) {
            Logger.get.print(e.getPlayer().getName() + " emptied bucket at [l] [x] [y] [z]", e.getBlockClicked().getLocation());
        }
    }

    @EventHandler
    public void logCommand(PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled() && c.getBoolean("logPlayerCommand")) {
            Logger.get.print(e.getPlayer().getName() + " ran command " + e.getMessage() + " at [l] [x] [y] [z]", e.getPlayer().getLocation());
        }
    }

    private String textFromContainer(TextContainer container) {
        if (container instanceof TranslationContainer) {
            return getServer().getLanguage().translateString(container.getText(), ((TranslationContainer) container).getParameters());
        } else {
            return container.getText();
        }
    }
}
