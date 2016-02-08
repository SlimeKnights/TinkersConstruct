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
    if(ToolHelper.isToolEffective2(tool, event.state)) {
      event.newSpeed *= 0.9f;
    }
    else if(event.state.getBlock().getMaterial().isToolNotRequired()) {
      event.newSpeed *= ToolHelper.getActualMiningSpeed(tool) * 0.5f;
    }
  }
}
