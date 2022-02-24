package slimeknights.tconstruct.shared;

//TODO: FIX
public class AchievementEvents {
  
}

/*
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.harvest.TinkerHarvestTools;
import slimeknights.tconstruct.tools.tools.Mattock;
import slimeknights.tconstruct.tools.tools.Pickaxe;

public class AchievementEvents {

  @SubscribeEvent
  public void onCraft(PlayerEvent.ItemCraftedEvent event) {
    if(event.player == null || event.crafting.isEmpty()) {
      return;
    }
    Item item = event.crafting.getItem();
    // fire vanilla pickaxe crafting when crafting tinkers picks (hammers also count for completeness sake)
    if(item instanceof Pickaxe) {
      event.player.addStat(AchievementList.BUILD_PICKAXE);

      if(TagUtil.getToolStats(event.crafting).harvestLevel > 0) {
        event.player.addStat(AchievementList.BUILD_BETTER_PICKAXE);
      }
    }
    if(item instanceof Mattock) {
      event.player.addStat(AchievementList.BUILD_HOE);
    }
    // sword == basic weapon achievement. Any weapon besides hatchet
    if(item != TinkerHarvestTools.hatchet && item instanceof ToolCore && ((ToolCore) item).hasCategory(Category.WEAPON)) {
      event.player.addStat(AchievementList.BUILD_SWORD);
    }
  }

  //@SubscribeEvent
  public void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
    if(event.player == null || event.smelting.isEmpty()) {
      return;
    }

    Item item = event.smelting.getItem();
    if(item == Items.IRON_INGOT) {
      event.player.addStat(AchievementList.ACQUIRE_IRON);
    }
  }

  // todo: bow
}*/
