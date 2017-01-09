package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModReinforced extends ModifierTrait {

  private static final float chancePerLevel = 0.20f;
  public static final String TAG_UNBREAKABLE = "Unbreakable";

  public ModReinforced() {
    super("reinforced", 0x502e83, 5, 0);
  }

  private float getReinforcedChance(NBTTagCompound modifierTag) {
    ModifierNBT data = ModifierNBT.readTag(modifierTag);

    return (float) data.level * chancePerLevel;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    if(getReinforcedChance(modifierTag) >= 1f) {
      rootCompound.setBoolean(TAG_UNBREAKABLE, true);
    }
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    if(entity.getEntityWorld().isRemote) {
      return 0;
    }

    // get reinforced level
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);

    float chance = getReinforcedChance(tag);
    if(chance >= random.nextFloat()) {
      newDamage -= damage;
    }
    return Math.max(0, newDamage);
  }

  @Override
  public String getLocalizedDesc() {
    return String.format(super.getLocalizedDesc(), Util.dfPercent.format(chancePerLevel));
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    ModifierNBT data = ModifierNBT.readTag(modifierTag);
    if(data.level == maxLevel) {
      return Util.translate("modifier.%s.unbreakable", getIdentifier());
    }
    return super.getTooltip(modifierTag, detailed);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());

    if(I18n.canTranslate(loc)) {
      float chance = getReinforcedChance(modifierTag);
      String chanceStr = Util.dfPercent.format(chance);
      if(chance >= 1f) {
        chanceStr = Util.translate("modifier.%s.unbreakable", getIdentifier());
      }
      return ImmutableList.of(Util.translateFormatted(loc, chanceStr));
    }
    return super.getExtraInfo(tool, modifierTag);
  }
}
