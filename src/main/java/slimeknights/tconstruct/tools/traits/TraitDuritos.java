package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.library.traits.AbstractTrait;

// This basically is Reinforced 3 with a twist.
// It has 50% chance to do nothing, 10% chance to use double durability and 40% chance to use no durability
// equalling to 70% durability used on average = 30% unbreakable
// the name totally is chylex fault.
public class TraitDuritos extends AbstractTrait {

  public TraitDuritos() {
    super("duritos", TextFormatting.LIGHT_PURPLE);
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    float r = random.nextFloat();
    if(r < 0.1f) {
      // double durability used, add the damage again
      return newDamage + damage;
    }
    else if(r < 0.5f) {
      // no durability used, substract the durability
      return Math.max(0, newDamage - damage);
    }
    else {
      return super.onToolDamage(tool, damage, newDamage, entity);
    }
  }
}
