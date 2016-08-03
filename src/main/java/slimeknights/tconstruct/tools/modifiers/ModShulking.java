package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class ModShulking extends ModifierTrait {

  public ModShulking() {
    super("shulking", 0xaaccff, 1, 50);
  }

  private int getDuration(ItemStack tool) {
    return getData(tool).current / 2 + 10;
  }


  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    int duration = getDuration(tool);

    target.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, duration, 0));
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());
    float duration = getDuration(tool);
    duration /= 20f;

    return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(duration)));
  }
}
