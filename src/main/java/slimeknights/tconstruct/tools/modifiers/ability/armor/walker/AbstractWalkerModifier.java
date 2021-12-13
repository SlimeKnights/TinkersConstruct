package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public abstract class AbstractWalkerModifier extends SingleUseModifier implements IArmorWalkModifier {
  public AbstractWalkerModifier(int color) {
    super(color);
  }

  /** Gets the radius for this modifier */
  protected abstract float getRadius(IModifierToolStack tool, int level);

  /**
   * Called to modify a position
   * @param tool     Tool instance
   * @param level    Modifier level
   * @param living   Entity walking
   * @param world    World being walked in
   * @param target   Position target for effect
   * @param mutable  Mutable position you can freely modify
   */
  protected abstract void walkOn(IModifierToolStack tool, int level, LivingEntity living, World world, BlockPos target, Mutable mutable);

  @Override
  public void onWalk(IModifierToolStack tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (living.isOnGround() && !tool.isBroken() && !living.world.isRemote) {
      float radius = Math.min(16, getRadius(tool, level));
      Mutable mutable = new Mutable();
      World world = living.world;
      Vector3d posVec = living.getPositionVec();
      BlockPos center = new BlockPos(posVec.x, posVec.y + 0.5, posVec.z);
      for (BlockPos pos : BlockPos.getAllInBoxMutable(center.add(-radius, 0, -radius), center.add(radius, 0, radius))) {
        if (pos.withinDistance(living.getPositionVec(), radius)) {
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
