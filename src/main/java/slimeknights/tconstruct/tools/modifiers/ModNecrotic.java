package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModNecrotic extends ModifierTrait {

  public ModNecrotic() {
    super("necrotic", 0x5e0000, 10, 0);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(wasHit) {
      float amount = damageDealt * lifesteal(TinkerUtil.getModifierTag(tool, getModifierIdentifier()));
      if(amount > 0) {
        player.heal(amount);
      }
    }
  }

  private float lifesteal(NBTTagCompound modifierNBT) {
    ModifierNBT data = new ModifierNBT(modifierNBT);
    return 0.10f * data.level;
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());
    float amount = lifesteal(modifierTag);

    return ImmutableList.of(Util.translateFormatted(loc, Util.dfPercent.format(amount)));
  }
}
