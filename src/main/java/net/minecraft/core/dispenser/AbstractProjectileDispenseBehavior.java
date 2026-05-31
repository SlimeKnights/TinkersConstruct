package slimeknights.tconstruct.compat.minecraft.core.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/** Compatibility implementation of the old projectile dispenser behavior. */
public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
  @Override
  public ItemStack execute(BlockSource source, ItemStack stack) {
    Level level = source.level();
    Direction direction = source.state().getValue(DispenserBlock.FACING);
    Position position = DispenserBlock.getDispensePosition(source);
    Projectile projectile = getProjectile(level, position, stack);
    projectile.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), getPower(), getUncertainty());
    level.addFreshEntity(projectile);
    stack.shrink(1);
    return stack;
  }

  protected float getUncertainty() {
    return 6.0F;
  }

  protected float getPower() {
    return 1.1F;
  }

  protected abstract Projectile getProjectile(Level level, Position position, ItemStack stack);
}
