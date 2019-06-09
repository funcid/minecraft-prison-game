package listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import prison.PrisonBosses;
import prison.PrisonMain;
import prison.PrisonPlayer;

import java.util.Random;

public class DamageListener implements Listener {

    @EventHandler
    public void playerDamagePlayer (EntityDamageByEntityEvent e) {
        Random random = new Random();
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (PrisonMain.getInstance().getStats().containsKey(e.getEntity().getUniqueId()) && PrisonMain.getInstance().getStats().containsKey(e.getDamager().getUniqueId())) {
                PrisonPlayer player = PrisonMain.getInstance().getStats().get(e.getEntity().getUniqueId());
                PrisonPlayer damager = PrisonMain.getInstance().getStats().get(e.getDamager().getUniqueId());
                if (player.getPrisonClansLocation() != null && damager.getPrisonClansLocation() != null && player.getPrisonClans().equals(damager.getPrisonClans()))
                    e.setCancelled(true);
            }
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player) {
            if (PrisonMain.getInstance().getStats().containsKey(e.getEntity().getUniqueId()) && PrisonMain.getInstance().getStats().containsKey(((Player) ((Arrow) e.getDamager()).getShooter()).getUniqueId())) {
                PrisonPlayer player = PrisonMain.getInstance().getStats().get(e.getEntity().getUniqueId());
                PrisonPlayer damager = PrisonMain.getInstance().getStats().get(((Player) ((Arrow) e.getDamager()).getShooter()).getUniqueId());
                if (player.getPrisonClansLocation() != null && damager.getPrisonClansLocation() != null && player.getPrisonClans().equals(damager.getPrisonClans()))
                    e.setCancelled(true);
            }
        } else if (e.getEntity() instanceof Spider && e.getDamager() instanceof Player && !(e.getEntity() instanceof CaveSpider)) {
            Spider spider = (Spider) e.getEntity();
            if (spider.getHealth() < 75) {
                spider.setHealth(spider.getHealth() + 12);
                if (spider.getHealth() > 0) spider.setCustomName(spider.getCustomName().split("§lHP: ")[0] + "§lHP: §c§l" + (int) spider.getHealth());
                CaveSpider caveSpider = (CaveSpider) PrisonMain.getInstance().getWorld().spawnEntity(spider.getLocation(), EntityType.CAVE_SPIDER);
                caveSpider.setCustomName("§f§lПрислужник матки");

                spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 3));
                caveSpider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 3));
                spider.setTarget((Player)e.getDamager());
                caveSpider.setTarget((Player)e.getDamager());
                if (random.nextInt(2) == 1) PrisonMain.getInstance().dropPlayerAround((Player) e.getDamager(), spider.getLocation(), 0.12);
            }
        } else if (e.getEntity() instanceof Spider && e.getDamager() instanceof Arrow && !(e.getEntity() instanceof CaveSpider)) {
            Spider spider = (Spider) e.getEntity();
            if (spider.getHealth() < 75) {
                spider.setHealth(spider.getHealth() + 12);

                CaveSpider caveSpider = (CaveSpider) PrisonMain.getInstance().getWorld().spawnEntity(spider.getLocation(), EntityType.CAVE_SPIDER);
                caveSpider.setCustomName("§f§lПрислужник матки");

                spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 3));
                caveSpider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 3));
                spider.setTarget((Player)((Arrow) e.getDamager()).getShooter());
                caveSpider.setTarget((Player)((Arrow) e.getDamager()).getShooter());
            }
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Fireball) {
            e.setDamage(PrisonBosses.BLAZE.getDamage());
        } else if (e.getEntity() instanceof IronGolem && e.getDamager() instanceof Player) {
            IronGolem ironGolem = (IronGolem) e.getEntity();
            if (ironGolem.getHealth() < 300 && ironGolem.getHealth() > 290)
                PrisonMain.getInstance().dropPlayerAround((Player) e.getDamager(), ironGolem.getLocation(), 0.18);
            else if (ironGolem.getHealth() < 300) {
                ironGolem.setHealth(ironGolem.getHealth() + 10);

                ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 2));
                ironGolem.setTarget((Player)e.getDamager());
                if (random.nextInt(9) == 1) PrisonMain.getInstance().dropPlayerAround((Player) e.getDamager(), ironGolem.getLocation(), 0.12);
            }
        } else if (e.getEntity() instanceof Enderman && e.getDamager() instanceof Player) {
            if (random.nextInt(5) == 1) {
                Player p = (Player) e.getDamager();
                ItemStack itemInHand = p.getItemInHand();
                int i = random.nextInt(35) + 1;
                ItemStack random_idem = p.getInventory().getContents()[i];
                ItemStack pool = itemInHand;
                p.setItemInHand(random_idem);
                p.getInventory().setItem(i, pool);
                p.updateInventory();
            }
        } else if (e.getEntity() instanceof Player && PrisonBosses.getBossByEntityType(e.getDamager().getType()) != null) {
            e.setDamage(PrisonBosses.getBossByEntityType(e.getDamager().getType()).getDamage());
        }
        if (PrisonBosses.getBossByEntityType(e.getEntity().getType()) != null && e.getDamager() instanceof Player || PrisonBosses.getBossByEntityType(e.getEntity().getType()) != null && e.getDamager() instanceof Arrow) {
            LivingEntity entity = (LivingEntity) e.getEntity();
            if (entity.getHealth() > 0) entity.setCustomName(entity.getCustomName().split("§lHP: ")[0] + "§lHP: §c§l" + (int) entity.getHealth());
        }
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (PrisonBosses.getBossByEntityType(entity.getType()) != null && entity.getKiller() != null && entity.getKiller() instanceof Player && PrisonMain.getInstance().getStats().containsKey(entity.getKiller().getUniqueId())) {
            PrisonBosses boss = PrisonBosses.getBossByEntityType(entity.getType());
            if (boss.equals(PrisonBosses.SPIDER))
                for (Entity spiders : PrisonMain.getInstance().getWorld().getEntities())
                    if (spiders.getType().equals(EntityType.CAVE_SPIDER))
                        spiders.remove();

            PrisonPlayer prisonPlayer = PrisonMain.getInstance().getStats().get(entity.getKiller().getUniqueId());
            prisonPlayer.setGold(prisonPlayer.getGold() + boss.getReward());
            entity.getKiller().sendMessage(PrisonMain.getInstance().getInfoPrefix() + "Вы получили награду за убийство босса в размере " + boss.getReward() + "$.");
            PrisonMain.getInstance().getBosses().remove(entity);
            Bukkit.broadcastMessage(PrisonMain.getInstance().getInfoPrefix() + "Босс " + boss.getName() + " §7был уничтожен игроком " + entity.getKiller().getName() + ".");
        } else if (PrisonBosses.getBossByEntityType(entity.getType()) != null)
            PrisonMain.getInstance().getBosses().remove(entity);
    }
}
