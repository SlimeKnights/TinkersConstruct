package tconstruct.library.weapons;

import net.minecraft.item.ItemStack;

import tconstruct.library.tools.TinkersTool;
import tconstruct.library.utils.TooltipBuilder;

public abstract class TinkerWeapon extends TinkersTool {

  @Override
  public String[] getInformation(ItemStack stack) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability();
    info.addAttack();

    return info.getTooltip();
  }
}
