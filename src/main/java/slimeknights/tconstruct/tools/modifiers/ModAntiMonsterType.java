package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModAntiMonsterType extends ModifierTrait {

  protected final EnumCreatureAttribute type;

  public ModAntiMonsterType(String identifier, int color, int maxLevel, int countPerLevel, EnumCreatureAttribute type) {
    super(identifier, color, maxLevel, countPerLevel);
    this.type = type;
  }

  protected float calcIncreasedDamage(ItemStack tool, float baseDamage) {
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);

    float level = (float)data.current / (float)data.max;
    return baseDamage + level * 7f;
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if(target.getCreatureAttribute() == type) {
      return calcIncreasedDamage(tool, newDamage);
    }
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
