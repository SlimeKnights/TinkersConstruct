package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitStonebound extends AbstractTrait {

  public TraitStonebound() {
    super("stonebound", EnumChatFormatting.DARK_GRAY);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    if(ToolHelper.isToolEffective2(tool, event.state)) {
      int durability = ToolHelper.getCurrentDurability(tool);
      int maxDurability = ToolHelper.getDurabilityStat(tool);

      // old tcon stonebound formula
      double bonus = Math.log((maxDurability - durability) / 72d + 1d) * 2;
      event.newSpeed += bonus;
    }
  }
}
