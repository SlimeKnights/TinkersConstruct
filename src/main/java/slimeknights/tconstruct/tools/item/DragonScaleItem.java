package slimeknights.tconstruct.tools.item;

import net.minecraft.util.DamageSource;
import slimeknights.mantle.item.TooltipItem;

/** Explosion immune tooltip item */
public class DragonScaleItem extends TooltipItem {
  public DragonScaleItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isDamageable(DamageSource damageSource) {
    return !damageSource.isExplosion();
  }
}
