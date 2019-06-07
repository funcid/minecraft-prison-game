package menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import prison.PrisonMain;
import prison.PrisonMine;
import prison.PrisonPlayer;

import java.util.Arrays;

public class MineMenu {

    ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE);
    ItemStack none = new ItemStack(Material.STAINED_CLAY);
    ItemMeta nonemeta = none.getItemMeta();
    ItemStack infolist = new ItemStack(Material.PAPER);
    ItemMeta infometa = infolist.getItemMeta();

    public void openPlayerGUI (Player p) {
        if (PrisonMain.getInstance().getStats().containsKey(p.getUniqueId())) {
            nonemeta.setDisplayName("§c§lНЕДОСТУПНО");
            none.setItemMeta(nonemeta);

            PrisonPlayer prisonPlayer = PrisonMain.getInstance().getStats().get(p.getUniqueId());

            infometa.setDisplayName("§lИнформация §e§l" + p.getName());
            infometa.setLore(Arrays.asList("",
                    "§f* Уровень: §b§l" + prisonPlayer.getLevel(),
                    "§f* Денег: §a§l" + prisonPlayer.getGold() + "$",
                    "§f* Убийств: §c§l" + prisonPlayer.getKills(),
                    "§f* Блоков: §6§l" + prisonPlayer.getBlocksValue(),
                    "§f* Индентификатор: §l" + prisonPlayer.getId())
            );
            infolist.setItemMeta(infometa);

            //Инвентарь с живыми игроками
            Inventory i = Bukkit.createInventory(null, 54, "§b§lШахты");

            //Заполнение инвентаря стеклом и головами игроков
            for (int u = 0; u < 9; u++)
                i.setItem(u, empty);
            for (int u = 19; u < 26; u++)
                i.setItem(u, none);
            for (int j = 0; j < PrisonMain.getInstance().getMines().size(); j++) {
                if (j == 0 && prisonPlayer.isCanEnter() == 0) continue;
                PrisonMine prisonMine = PrisonMain.getInstance().getMines().get(j);
                if (prisonPlayer.getLevel() >= prisonMine.getMlevel()) {
                    ItemStack mine = new ItemStack(prisonMine.getMaterial());
                    ItemMeta minemeta = mine.getItemMeta();
                    minemeta.setDisplayName(prisonMine.getName());
                    String s;
                    if (prisonMine.getPvp()) s = "§c§lЕсть";
                    else s = "§a§lОтсутствует";
                    minemeta.setLore(Arrays.asList("",
                            "§f§l* Минимальный уровень: §b§l" + prisonMine.getMlevel(),
                            "§f§l* Наличте ПВП: " + s));
                    mine.setItemMeta(minemeta);

                    i.setItem(18 + j, mine);
                }
            }
            i.setItem(40, infolist);
            for (int u = 45; u < 54; u++)
                i.setItem(u, empty);
            p.openInventory(i);
        } else
            p.sendMessage(PrisonMain.getInstance().getErrorPrefix() + "Упс... Произошла ошибка номер 6, обратитесь к персоналу сообщив номер ошибки.");
    }

    public void menuHandler(Player p, ItemStack item) {
        Material type = item.getType();
        PrisonMain.getInstance().getMines().stream().filter(mine -> mine.getMaterial().equals(type)).forEach(mine -> PrisonMain.getInstance().teleport(p, mine.getMineLocation()));

        p.closeInventory();
    }
    public static MineMenu getInstance () {
        return new MineMenu();
    }
}
