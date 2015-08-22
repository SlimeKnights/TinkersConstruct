package slimeknights.tconstruct.library.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class StoneboundTrait extends AbstractTrait {

  public StoneboundTrait() {
    super("Stonebound");
  }

  @Override
  public int getMaxCount() {
    return 2;
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    event.newSpeed = Math.max(0f, event.newSpeed * 9 / 10);
  }
}
