package slimeknights.tconstruct.library.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.utils.ToolHelper;

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
    float damaged = (float)tool.getItemDamage() / (float) ToolHelper.getDurability(tool);

    event.newSpeed = Math.max(0f, event.newSpeed + damaged*5.0f);
  }
}
