package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModAntiMonsterType extends ModifierTrait {

  protected final EnumCreatureAttribute type;

  private final float dmgPerItem;

  public ModAntiMonsterType(String identifier, int color, int maxLevel, int countPerLevel, EnumCreatureAttribute type) {
    super(identifier, color, maxLevel, countPerLevel);
    this.type = type;

    dmgPerItem = 7f / (float) countPerLevel;
  }

  protected float calcIncreasedDamage(NBTTagCompound modifierTag, float baseDamage) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    return baseDamage + (float) data.current * dmgPerItem;
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if(target.getCreatureAttribute() == type) {
      NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
      return calcIncreasedDamage(tag, newDamage);
    }
    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());

    if(I18n.canTranslate(loc)) {
      float dmg = calcIncreasedDamage(modifierTag, 0);
      return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(dmg)));
    }
    return super.getExtraInfo(tool, modifierTag);
  }
}
