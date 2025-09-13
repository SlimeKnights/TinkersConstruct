package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;

/** Dispenser behavior for a modifiable shuriken item */
public class ModifiableShurikenDispenserBehavior extends AbstractProjectileDispenseBehavior {
  public static final ModifiableShurikenDispenserBehavior INSTANCE = new ModifiableShurikenDispenserBehavior();

  private ModifiableShurikenDispenserBehavior() {}

  @Override
  protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
    ThrownShuriken arrow = new ThrownShuriken(level, position.x(), position.y(), position.z());
    arrow.onCreate(stack, null);
    return arrow;
  }
}
