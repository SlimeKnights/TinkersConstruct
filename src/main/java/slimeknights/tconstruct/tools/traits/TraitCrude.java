package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitCrude extends AbstractTraitLeveled {

  public TraitCrude(int levels) {
    super("crude", 0x424242, 3, levels);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    boolean hasArmor = target.getTotalArmorValue() > 0;
    if(!hasArmor) {
      ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, "crude"));
      // 10% damage boost against unarmed targets!
      return newDamage + damage * 0.05f * data.level;
    }
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
