package slimeknights.tconstruct.tools.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import slimeknights.mantle.item.TooltipItem;

/** Explosion immune tooltip item */
public class DragonScaleItem extends TooltipItem {
  public DragonScaleItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canBeHurtBy(DamageSource damageSource) {
    return !damageSource.is(DamageTypeTags.IS_EXPLOSION);
  }
}
