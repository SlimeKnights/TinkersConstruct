package slimeknights.tconstruct.tools.modifiers;

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
    float damaged = (float)tool.getItemDamage() / (float) ToolHelper.getDurability(tool);

    event.newSpeed = Math.max(0f, event.newSpeed + damaged*5.0f);
  }
}
