package slimeknights.tconstruct.library.modifiers.modules.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Implementation of the standard radius walk behavior used by most implementations
 */
public interface ArmorWalkRadiusModule<T> extends ArmorWalkModifierHook, ModifierModule {
  List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BOOT_WALK);

  @Override
  default List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /**
   * Gets the radius for this modifier
   */
  float getRadius(IToolStackView tool, ModifierEntry modifier);

  /**
   * Called to modify a position
   * @param tool    Tool instance
   * @param entry   Modifier level
   * @param living  Entity walking
   * @param world   World being walked in
   * @param target  Position target for effect
   * @param mutable Mutable position you can freely modify
   * @param context Extra data context used by the modifier
   */
  void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, T context);

  /**
   * Creates additional context to pass into the on walk method
   */
  @SuppressWarnings("ConstantConditions") // it won't be null if you actually intend to use it
  default T getContext(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    return null;
  }

  @Override
  default void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (living.isOnGround() && !tool.isBroken() && !living.level.isClientSide) {
      T context = getContext(tool, modifier, living, prevPos, newPos);
      float radius = Math.min(16, getRadius(tool, modifier));
      MutableBlockPos mutable = new MutableBlockPos();
      Level world = living.level;
      Vec3 posVec = living.position();
      BlockPos center = new BlockPos(posVec.x, posVec.y + 0.5, posVec.z);
      for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, 0, radius))) {
        if (pos.closerToCenterThan(living.position(), radius)) {
          walkOn(tool, modifier, living, world, pos, mutable, context);
          if (tool.isBroken()) {
            break;
          }
        }
      }
    }
  }
}
