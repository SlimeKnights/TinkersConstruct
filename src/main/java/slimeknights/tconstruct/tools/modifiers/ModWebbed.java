package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class ModWebbed extends ModifierTrait {

  public ModWebbed() {
    super("webbed", 0xffffff, 3, 0);
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    int duration = getData(tool).level * 20;
    target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, 1));
  }
}
