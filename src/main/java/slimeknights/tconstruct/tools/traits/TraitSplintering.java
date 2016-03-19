package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.potion.TinkerPotion;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitSplintering extends AbstractTrait {

  public static TinkerPotion Splinter = new TinkerPotion(Util.getResource("splinter"), true, false);

  public TraitSplintering() {
    super("splintering", TextFormatting.WHITE);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {

    PotionEffect effect = target.getActivePotionEffect(Splinter);
    if(effect != null) {
      newDamage += 0.3f * (effect.getAmplifier() + 1);
    }

    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    int level = 0;

    PotionEffect old = target.getActivePotionEffect(Splinter);
    if(old != null) {
      level = Math.min(5, old.getAmplifier() + 1);
    }

    // apply splinter effect
    Splinter.apply(target, 40, level);
    super.afterHit(tool, player, target, damageDealt, wasCritical, wasHit);
  }
}
