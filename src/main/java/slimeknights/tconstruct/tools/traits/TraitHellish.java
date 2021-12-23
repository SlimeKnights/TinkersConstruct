package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;

import java.util.List;

public class TraitHellish extends AbstractTrait {

  private static final float bonusDamage = 4f;

  public TraitHellish() {
    super("hellish", 0xff0000);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if (!target.isImmuneToFire()) {
      newDamage += bonusDamage;
    }
    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(bonusDamage)));
  }
}
