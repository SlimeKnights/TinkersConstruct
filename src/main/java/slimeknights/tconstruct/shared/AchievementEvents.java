package slimeknights.tconstruct.shared;

import net.minecraft.item.Item;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.Mattock;
import slimeknights.tconstruct.tools.item.Pickaxe;

public class AchievementEvents {
  @SubscribeEvent
  public void onCraft(PlayerEvent.ItemCraftedEvent event) {
    if(event.player == null || event.crafting == null) {
      return;
    }
    Item item = event.crafting.getItem();
    // fire vanilla pickaxe crafting when crafting tinkers picks (hammers also count for completeness sake)
    if(item instanceof Pickaxe) {
      event.player.triggerAchievement(AchievementList.buildPickaxe);

      if(TagUtil.getToolStats(event.crafting).harvestLevel > 0) {
        event.player.triggerAchievement(AchievementList.buildBetterPickaxe);
      }
    }
    if(item instanceof Mattock) {
      event.player.triggerAchievement(AchievementList.buildHoe);
    }
    // sword == basic weapon achievement. Any weapon besides hatchet
    if(item != TinkerTools.hatchet && item instanceof ToolCore && ((ToolCore) item).hasCategory(Category.WEAPON)) {
      event.player.triggerAchievement(AchievementList.buildSword);
    }
  }

  // todo: picking up iron ingot or block from casting basin/table
  // todo: bow
}
