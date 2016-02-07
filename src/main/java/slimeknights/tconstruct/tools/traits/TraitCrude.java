package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;

public class TraitCrude extends AbstractTraitLeveled {

  public TraitCrude(int levels) {
    super("crude", 0x424242, 3, levels);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    boolean hasArmor = target.getTotalArmorValue() > 0;
    if(!hasArmor) {
      // 10% damage boost against unarmed targets!
      return newDamage + damage * 0.1f;
    }
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
