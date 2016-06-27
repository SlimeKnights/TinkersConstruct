package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitCrude extends AbstractTraitLeveled {

  public TraitCrude(int levels) {
    super("crude", 0x424242, 3, levels);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    boolean hasArmor = target.getTotalArmorValue() > 0;
    if(!hasArmor) {
      NBTTagCompound modifierTag = TinkerUtil.getModifierTag(tool, "crude");
      // 10% damage boost against unarmed targets!
      newDamage += damage * bonusModifier(modifierTag);
    }
    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  private float bonusModifier(NBTTagCompound modifierNBT) {
    ModifierNBT data = new ModifierNBT(modifierNBT);
    return 0.05f * data.level;
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());
    float bonus = bonusModifier(modifierTag);

    return ImmutableList.of(Util.translateFormatted(loc, Util.dfPercent.format(bonus)));
  }
}
