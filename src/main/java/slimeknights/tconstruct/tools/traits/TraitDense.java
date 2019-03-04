package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

// chance to use less durability if pretty damaged.
// chance scales quadratically with how much is missing
public class TraitDense extends AbstractTrait {

  public TraitDense() {
    super("dense", 0xffffff);
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    float durability = ToolHelper.getCurrentDurability(tool);
    float maxDurability = ToolHelper.getMaxDurability(tool);

    float chance = 0.75f * (1f - durability / maxDurability);
    chance = chance * chance * chance;

    if(chance > random.nextFloat()) {
      newDamage -= Math.max(damage / 2, 1);
    }

    return super.onToolDamage(tool, damage, newDamage, entity);
  }
}
