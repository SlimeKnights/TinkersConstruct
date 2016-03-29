package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitCrumbling extends AbstractTrait {

  public TraitCrumbling() {
    super("crumbling", 0xff0000);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    if(event.getState().getBlock().getMaterial(event.getState()).isToolNotRequired()) {
      event.setNewSpeed(event.getNewSpeed() * (ToolHelper.getActualMiningSpeed(tool) * 0.5f));
    }
  }
}
