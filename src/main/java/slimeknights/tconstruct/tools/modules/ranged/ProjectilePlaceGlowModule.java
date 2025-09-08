package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.mapping.SimpleRecordLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.List;

/** Module that places a glow after hitting the target */
public enum ProjectilePlaceGlowModule implements ModifierModule, ProjectileHitModifierHook {
  BLOCKS,
  ENTITIES,
  ANY;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ProjectilePlaceGlowModule>defaultHooks(ModifierHooks.PROJECTILE_HIT);
  public static final RecordLoadable<ProjectilePlaceGlowModule> LOADER = new SimpleRecordLoadable<>(new EnumLoadable<>(ProjectilePlaceGlowModule.class), "target", null, false);

  @Override
  public RecordLoadable<ProjectilePlaceGlowModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    TinkerCommons.glow.get().addGlow(projectile.level(), hit.getEntity().blockPosition(), Direction.DOWN);
    return false;
  }

  @Override
  public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    Direction direction = hit.getDirection();
    TinkerCommons.glow.get().addGlow(projectile.level(), hit.getBlockPos().relative(direction), direction.getOpposite());
    if (!projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)) {
      projectile.discard();
    }
  }
}
