package listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import prison.PrisonMain;

public class RatKillListener implements Listener {

    @EventHandler
    public void killRatEvent (EntityDeathEvent e) {
        Entity dead = e.getEntity();
        if (((LivingEntity) dead).getKiller() instanceof Player && dead.getType().equals(EntityType.SILVERFISH)) {
            Player p = ((LivingEntity) dead).getKiller();
            PrisonMain.getInstance().getStats().get(p.getUniqueId()).incRat();
        }
    }
    @EventHandler
    public void enderTeleport (EntityTeleportEvent e) {
        e.setCancelled(true);
    }
}
