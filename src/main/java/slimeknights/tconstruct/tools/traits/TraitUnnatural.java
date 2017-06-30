package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed for each harvest level the tool is above the required harvest level.
 * So a high level tool with this breaks low level blocks very fast.
 */
public class TraitUnnatural extends AbstractTrait {

  public TraitUnnatural() {
    super("unnatural", TextFormatting.LIGHT_PURPLE);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    Block block = event.getState().getBlock();
    int hlvl = tool.getItem().getHarvestLevel(tool, block.getHarvestTool(event.getState()), event.getEntityPlayer(), event.getState());
    int dif = hlvl - block.getHarvestLevel(event.getState());

    // 1 speed per harvest level above
    if(dif > 0) {
      event.setNewSpeed(event.getNewSpeed() + dif);
    }
  }

}
