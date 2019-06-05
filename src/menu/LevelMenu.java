package menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import prison.PrisonMain;
import prison.PrisonPlayer;
import prison.PrisonScoreboard;
import prison.PrisonUpgrade;

import java.util.Arrays;

public class LevelMenu {
    private ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE);
    private ItemStack star = new ItemStack(Material.NETHER_STAR);
    private ItemMeta starmeta = star.getItemMeta();

    public void openPlayerGUI (Player p) {
        if (PrisonMain.getInstance().getStats().containsKey(p.getUniqueId())) {

            PrisonPlayer prisonPlayer = PrisonMain.getInstance().getStats().get(p.getUniqueId());

            starmeta.setDisplayName("§f" + prisonPlayer.getLevel() + " >> §b§l" + (prisonPlayer.getLevel()+1) + "§7 >> " + (prisonPlayer.getLevel()+2));
            starmeta.setLore(Arrays.asList("§f§lЦена: §6§l" + PrisonMain.getInstance().getConfig().getInt("level." + (prisonPlayer.getLevel() + 1) + ".price") + "$.",
                                           "§7У вас денег: " + prisonPlayer.getGold() + "$.",
                                           "§f§lБлоков необходимо: §6§l" + PrisonMain.getInstance().getConfig().getInt("level." + (prisonPlayer.getLevel() + 1) + ".blocks") + ".",
                                           "§7У вас блоков: " + prisonPlayer.getBlocksValue() + "."));
            star.setItemMeta(starmeta);

            //Инвентарь с живыми игроками
            Inventory i = Bukkit.createInventory(null, 27, "§b§lУлучшение уровня");

            //Заполнение инвентаря стеклом и головами игроков
            for (int u = 0; u < 9; u++)
                i.setItem(u, empty);
            i.setItem(13, star);
            for (int u = 18; u < 27; u++)
                i.setItem(u, empty);
            //Открытие игрока
            p.openInventory(i);
        } else
            p.sendMessage(PrisonMain.getInstance().getErrorPrefix() + "Упс... Произошла ошибка номер 7, обратитесь к персоналу сообщив номер ошибки.");
    }

    public void menuHandler(Player p) {
        //Обработка возможных предметов
        try {
            PrisonPlayer prisonPlayer = PrisonMain.getInstance().getStats().get(p.getUniqueId());
            int price = PrisonMain.getInstance().getConfig().getInt("level." + (prisonPlayer.getLevel() + 1) + ".price");
            int blocks = PrisonMain.getInstance().getConfig().getInt("level." + (prisonPlayer.getLevel() + 1) + ".blocks");
            if (prisonPlayer.getGold() < price) {
                p.sendMessage(PrisonMain.getInstance().getErrorPrefix() + "Вам не хватает " + (price - prisonPlayer.getGold()) + "$.");
                return;
            }
            if (prisonPlayer.getBlocksValue() < blocks) {
                p.sendMessage(PrisonMain.getInstance().getErrorPrefix() + "Вам не хватает " + (blocks - prisonPlayer.getBlocksValue()) + " блоков.");
                return;
            }
            prisonPlayer.setGold(prisonPlayer.getGold() - price);
            prisonPlayer.setLevel(prisonPlayer.getLevel() + 1);
            p.sendMessage(PrisonMain.getInstance().getInfoPrefix() + "Теперь у вас §b" + prisonPlayer.getLevel() + "§7 уровень");
        } catch (Exception e) {
            p.sendMessage(PrisonMain.getInstance().getErrorPrefix() + "Упс... Произошла ошибка номер 8, обратитесь к персоналу сообщив номер ошибки.");
        }
        p.closeInventory();
    }
    public static LevelMenu getInstance () {
        return new LevelMenu();
    }
}