package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitLightweight extends AbstractTrait {

  public TraitLightweight() {
    super("lightweight", 0x00ff00);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    // 10% bonus speed
    event.newSpeed *= 1.1f;
  }
}
