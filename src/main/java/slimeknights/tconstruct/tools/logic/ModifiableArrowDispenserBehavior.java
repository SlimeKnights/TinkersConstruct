package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.tools.entity.ModifiableArrow;

/** Dispenser behavior for a modifiable arrow item */
public class ModifiableArrowDispenserBehavior extends AbstractProjectileDispenseBehavior {
  public static final ModifiableArrowDispenserBehavior INSTANCE = new ModifiableArrowDispenserBehavior();

  private ModifiableArrowDispenserBehavior() {}

  @Override
  protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
    ModifiableArrow arrow = new ModifiableArrow(level, position.x(), position.y(), position.z());
    arrow.onCreate(stack, null);
    arrow.pickup = Pickup.ALLOWED;
    return arrow;
  }
}
