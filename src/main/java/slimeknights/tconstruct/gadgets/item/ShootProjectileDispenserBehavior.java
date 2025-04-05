package slimeknights.tconstruct.gadgets.item;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;

/** Entity type based projectile shooting dispenser behavior */
@RequiredArgsConstructor
public class ShootProjectileDispenserBehavior extends DefaultDispenseItemBehavior {
  private final EntityType<? extends ThrowableItemProjectile> entity;
  private final float power;
  private final float inaccuracy;

  public ShootProjectileDispenserBehavior(EntityType<? extends ThrowableItemProjectile> entity) {
    this(entity, 1.1f, 6.0f);
  }

  @Override
  public ItemStack execute(BlockSource source, ItemStack stack) {
    Level level = source.getLevel();
    ThrowableItemProjectile projectile = entity.create(level);
    if (projectile != null) {
      Position position = DispenserBlock.getDispensePosition(source);
      Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
      projectile.setPos(position.x(), position.y(), position.z());
      projectile.setItem(stack);
      projectile.shoot(direction.getStepX(), ((float)direction.getStepY() + 0.1F), direction.getStepZ(), power, inaccuracy);
      level.addFreshEntity(projectile);
      stack.shrink(1);
    }
    return stack;
  }

  @Override
  protected void playSound(BlockSource pSource) {
    pSource.getLevel().levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, pSource.getPos(), 0);
  }
}
