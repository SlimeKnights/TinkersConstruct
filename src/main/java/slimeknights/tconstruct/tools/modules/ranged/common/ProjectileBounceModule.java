package slimeknights.tconstruct.tools.modules.ranged.common;

import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

/** Module causing arrows to bounce */
public record ProjectileBounceModule(LevelingInt bounces) implements ModifierModule, ProjectileHitModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ProjectileBounceModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_HIT_CLIENT);
  public static final RecordLoadable<ProjectileBounceModule> LOADER = RecordLoadable.create(LevelingInt.LOADABLE.directField(ProjectileBounceModule::bounces), ProjectileBounceModule::new);

  @Override
  public RecordLoadable<ProjectileBounceModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    // run later so other hooks can run before we cancel it all
    return 75;
  }

  @Override
  public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
    ResourceLocation key = modifier.getId();
    int bounces = persistentData.getInt(key);
    if (bounces < this.bounces.compute(modifier.getEffectiveLevel())) {
      Vec3 motion = projectile.getDeltaMovement();
      Axis axis = hit.getDirection().getAxis();
      double amount = axis.choose(motion.x, motion.y, motion.z);
      if (Math.abs(amount) > 0.3f) {
        motion = motion.scale(0.9f).with(axis, amount * -1f);
        projectile.setDeltaMovement(motion);
        projectile.setYRot((float)(Mth.atan2(motion.x, motion.z) * (180 / Math.PI)));
        projectile.setXRot((float)(Mth.atan2(motion.y, motion.horizontalDistance()) * (180 / Math.PI)));
        projectile.yRotO = projectile.getYRot();
        projectile.xRotO = projectile.getXRot();

        // mark a bounce as happened, block future modifiers
        persistentData.putInt(key, bounces + 1);
        if (!projectile.level().isClientSide) {
          projectile.playSound(Sounds.SLIMY_BOUNCE.getSound());
        }
        return true;
      }
    }
    return false;
  }
}
