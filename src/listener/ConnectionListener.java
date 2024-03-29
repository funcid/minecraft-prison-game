package listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import prison.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private ItemStack axe = new ItemStack(Material.WOOD_AXE);
    private ItemMeta meta = axe.getItemMeta();

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        p.getInventory().addItem(new ItemStack(Material.COMPASS));
        loadStats(p);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        saveStats(e.getPlayer());
        e.setQuitMessage(null);
    }
    public void saveStats (Player p) {
        PrisonPlayer prisonPlayer;
        if(((HashMap<UUID, PrisonPlayer>) PrisonVariables.PLAYER_STATS.getO()).containsKey(p.getUniqueId()))
            prisonPlayer = ((HashMap<UUID, PrisonPlayer>) PrisonVariables.PLAYER_STATS.getO()).get(p.getUniqueId());
        else {
            Bukkit.broadcastMessage(PrisonVariables.INFO.getO() + "Упс... Произошла ошибка номер 5, у " + p.getName() + ", если вы видете ЭТО значит, я что то сломал, простите.");
            return;
        }
        try {
            ResultSet rs = ((Statement)PrisonVariables.STATEMENT.getO()).executeQuery("SELECT * FROM `PrisonPlayers` WHERE uuid = '" + p.getUniqueId() + "';");
            if (rs.next())
                ((Statement)PrisonVariables.STATEMENT.getO()).executeUpdate("UPDATE `PrisonPlayers` SET blocks = '" + prisonPlayer.getBlocks() + "', gold = '" + prisonPlayer.getGold() + "', level = '" + prisonPlayer.getLevel() + "', kills = '" + prisonPlayer.getKills() + "', clan = '" + prisonPlayer.getPrisonClanName() + "', enter = '" + prisonPlayer.isCanEnter() + "' WHERE uuid = '" + p.getUniqueId() + "';");

            Bukkit.getLogger().info(p.getName() + " сохранен.");
        } catch (SQLException ex) {
            Bukkit.broadcastMessage(PrisonVariables.ERROR.getO() +
                    "Упс... Произошла ошибка номер 4, статистика " + p.getName() + " не сохранилась, пожалуйста отправте это сообщение администратору в полном виде." + prisonPlayer.getBlocks());
            saveStats(p);
        }
        ((HashMap<UUID, PrisonPlayer>) PrisonVariables.PLAYER_STATS.getO()).remove(p.getUniqueId());
    }
    public void loadStats (Player p) {
        try {
            ResultSet rs = ((Statement)PrisonVariables.STATEMENT.getO()).executeQuery("SELECT * FROM `PrisonPlayers` WHERE uuid = '" + p.getUniqueId() + "';");
            if (rs.next()) {
                //id	uuid	 gold	  level	   kills	deaths	  blocks
                ((HashMap<UUID, PrisonPlayer>) PrisonVariables.PLAYER_STATS.getO()).put(p.getUniqueId(), new PrisonPlayer(
                        rs.getInt("id"),
                        rs.getFloat("gold"),
                        rs.getInt("level"),
                        rs.getInt("kills"),
                        rs.getInt("deaths"),
                        rs.getString("blocks"),
                        PrisonFractions.getByName(rs.getString("clan")),
                        rs.getInt("enter"))
                );
            } else {
                ((Statement)PrisonVariables.STATEMENT.getO()).executeUpdate("INSERT INTO `PrisonPlayers` (uuid, gold, level, kills, deaths, blocks, clan, enter) VALUES('" + p.getUniqueId() + "', 0, 1, 0, 0, 'DIRT 0 SAND 0 GRAVEL 0 STONE 0 COBBLESTONE 0 COAL_ORE 0 IRON_ORE 0 LOG 0 LOG_2 0 GOLD_ORE 0 COAL_BLOCK 0 NETHERRACK 0 QUARTZ_ORE 0 SOUL_SAND 0 PRISMARINE 0 SANDSTONE 0 RED_SANDSTONE 0 EMERALD_BLOCK 0 ENDER_STONE 0 OBSIDIAN 0 DIAMOND_BLOCK 0 LAPIS_BLOCK 0 IRON_BLOCK 0 GOLD_BLOCK 0 RAT 0', '§7Заключенный', 0);");
                p.sendMessage(PrisonVariables.INFO.getO() + "Новый профиль создан");
                meta.setDisplayName("§f§lДеревянный топор 1 уровня");
                meta.spigot().setUnbreakable(true);
                axe.setItemMeta(meta);
                p.getInventory().addItem(axe);
                p.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 32));
                loadStats(p);
            }
            PrisonScoreboard.getInstance().setScoreboard(p);
            p.setMaxHealth(21 + ((HashMap<UUID, PrisonPlayer>) PrisonVariables.PLAYER_STATS.getO()).get(p.getUniqueId()).getLevel());
        } catch (Exception e) {
            p.sendMessage(PrisonVariables.ERROR.getO() + "Упс... Произошла ошибка номер 1, обратитесь к персоналу сообщив номер ошибки.");
        }
    }
    public static ConnectionListener getInstance () {
        return new ConnectionListener();
    }
}

