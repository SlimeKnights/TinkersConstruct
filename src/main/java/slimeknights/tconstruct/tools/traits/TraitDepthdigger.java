package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitDepthdigger extends AbstractTrait {

  public TraitDepthdigger() {
    super("depthdigger", 0xffffff);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    int y = event.getPos().getY();
    y = 72 - y; // actual sealevel is 64, we chose 72 because we're nice
    if(y > 0) {
      event.setNewSpeed(event.getNewSpeed() + (float) y / 30f);
    }
  }
}
