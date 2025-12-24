package slimeknights.tconstruct.tools.modules.ranged.common;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module that places a glow after hitting the target
 * @param damage     Damage to deal when placing a glow
 * @param blocks     If true, places glow on hitting a block
 * @param entities   If true, places glow at the feet of the hit entity
 */
public record ProjectilePlaceGlowModule(int damage, boolean blocks, boolean entities) implements ModifierModule, ProjectileHitModifierHook, ProjectileLaunchModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ProjectilePlaceGlowModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH);
  public static final RecordLoadable<ProjectilePlaceGlowModule> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.requiredField("tool_damage", ProjectilePlaceGlowModule::damage),
    BooleanLoadable.INSTANCE.requiredField("blocks", ProjectilePlaceGlowModule::blocks),
    BooleanLoadable.INSTANCE.requiredField("entities", ProjectilePlaceGlowModule::entities),
    ProjectilePlaceGlowModule::new);

  @Override
  public RecordLoadable<ProjectilePlaceGlowModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // deal damage to the bow if it added glowing to its arrow
    // don't damage fishing hooks though, we will do that on hit
    if (primary && damage > 0 && projectile.getType() != TinkerTools.fishingHook.get()) {
      ToolDamageUtil.damageAnimated(tool, damage, shooter, shooter.getUsedItemHand());
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (entities) {
      TinkerCommons.glow.get().addGlow(projectile.level(), hit.getEntity().blockPosition(), Direction.DOWN);
      if (damage > 0) {
        ModifierUtil.updateFishingRod(projectile, damage, false);
      }
    }
    return false;
  }

  @Override
  public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
    if (blocks) {
      Direction direction = hit.getDirection();
      TinkerCommons.glow.get().addGlow(projectile.level(), hit.getBlockPos().relative(direction), direction.getOpposite());
      if (!projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)) {
        ModifierUtil.updateFishingRod(projectile, damage, true);
        projectile.discard();
        return true;
      }
    }
    return false;
  }
}
