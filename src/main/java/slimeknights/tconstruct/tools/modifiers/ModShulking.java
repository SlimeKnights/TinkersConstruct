package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class ModShulking extends ModifierTrait {

  public ModShulking() {
    super("shulking", 0xaaccff, 1, 50);
  }

  private int getDuration(ItemStack tool) {
    return getData(tool).current/2 + 10;
  }


  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    int duration = getDuration(tool);

    target.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, duration, 0));
  }
}
