package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

/**
 * Increases mining speed for each harvest level the tool is above the required harvest level.
 * So a high level tool with this breaks low level blocks very fast.
 */
public class TraitUnnatural extends AbstractTrait {

  public TraitUnnatural() {
    super("unnatural", EnumChatFormatting.LIGHT_PURPLE);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    Block block = event.state.getBlock();
    int hlvl = tool.getItem().getHarvestLevel(tool, block.getHarvestTool(event.state));
    int dif = hlvl - block.getHarvestLevel(event.state);

    // 36% speed per harvest level above
    event.newSpeed += event.originalSpeed * dif * 0.36f;
  }

}
