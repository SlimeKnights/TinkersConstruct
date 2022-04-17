package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public abstract class AbstractWalkerModifier extends NoLevelsModifier implements IArmorWalkModifier {
  /** Gets the radius for this modifier */
  protected abstract float getRadius(IToolStackView tool, int level);

  /**
   * Called to modify a position
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param living   Entity walking
   * @param world    World being walked in
   * @param target   Position target for effect
   * @param mutable  Mutable position you can freely modify
   */
  protected abstract void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable);

  @Override
  public void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (living.isOnGround() && !tool.isBroken() && !living.level.isClientSide) {
      float radius = Math.min(16, getRadius(tool, level));
      MutableBlockPos mutable = new MutableBlockPos();
      Level world = living.level;
      Vec3 posVec = living.position();
      BlockPos center = new BlockPos(posVec.x, posVec.y + 0.5, posVec.z);
      for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))) {
        if (pos.closerToCenterThan(living.position(), radius)) {
          walkOn(tool, level, living, world, pos, mutable);
          if (tool.isBroken()) {
            break;
          }
        }
      }
    }
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorWalkModifier.class, this);
  }
}
