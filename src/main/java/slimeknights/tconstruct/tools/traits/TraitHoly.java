package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitHoly extends AbstractTrait {

  private static float bonusDamage = 3f;

  public TraitHoly() {
    super("holy", 0xffffff);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if(target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
      newDamage += bonusDamage;
    }

    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = Util.translate(LOC_Extra, getIdentifier());
    return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(bonusDamage)));
  }
}
