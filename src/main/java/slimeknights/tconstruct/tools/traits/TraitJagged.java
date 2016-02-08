package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitJagged extends AbstractTrait {

  public TraitJagged() {
    super("jagged", EnumChatFormatting.AQUA);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    int durability = ToolHelper.getCurrentDurability(tool);
    int maxDurability = ToolHelper.getDurabilityStat(tool);

    // old tcon jagged formula
    double bonus = Math.log((maxDurability - durability) / 72d + 1d) * 2;
    newDamage += bonus;

    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
