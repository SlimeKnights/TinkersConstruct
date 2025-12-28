package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.events.teleport.EnderportingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileFuseModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.TeleportHelper;

import javax.annotation.Nullable;

public class EnderportingModifier extends NoLevelsModifier implements PlantHarvestModifierHook, ProjectileHitModifierHook, ProjectileLaunchModifierHook, BlockHarvestModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, ProjectileFuseModifierHook {
  private static final ResourceLocation SECONDARY_ARROW = TConstruct.getResource("enderporting_secondary");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PLANT_HARVEST, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.BLOCK_HARVEST, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_FUSE);
  }

  @Override
  public int getPriority() {
    return 45;
  }

  /** Attempts to teleport to the given location */
  private static boolean tryTeleport(ModifierEntry modifier, LivingEntity living, double x, double y, double z) {
    Level world = living.getCommandSenderWorld();
    // should never happen with the hooks, but just in case
    if (world.isClientSide) {
      return false;
    }
    // this logic is cloned from suffocation damage logic
    float scaledWidth = living.getBbWidth() * 0.8F;
    float eyeHeight = living.getEyeHeight();
    AABB aabb = AABB.ofSize(new Vec3(x, y + (eyeHeight / 2), z), scaledWidth, eyeHeight, scaledWidth);

    boolean didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();

    // if we collided, try again 1 block down, means mining the top of 2 blocks is valid
    if (didCollide && living.getBbHeight() > 1) {
      // try again 1 block down
      aabb = aabb.move(0, -1, 0);
      didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();
      y -= 1;
    }

    // as long as no collision now, we can teleport
    if (!didCollide) {
      return TeleportHelper.tryTeleport(new EnderportingTeleportEvent(living, x, y, z, modifier));
    }
    return false;
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      LivingEntity target = context.getLivingTarget();
      // if the entity is dead now
      if (target != null) {
        LivingEntity attacker = context.getAttacker();
        Vec3 oldPosition = attacker.position();
        if (tryTeleport(modifier, attacker, target.getX(), target.getY(), target.getZ())) {
          tryTeleport(modifier, target, oldPosition.x, oldPosition.y, oldPosition.z);
          ToolDamageUtil.damageAnimated(tool, 2, attacker, context.getSlotType());
        }
      }
    }
  }

  @Override
  public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
    if (harvested > 0 && context.canHarvest()) {
      BlockPos pos = context.getPos();
      LivingEntity living = context.getLiving();
      if (tryTeleport(modifier, living, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) {
        ToolDamageUtil.damageAnimated(tool, 2, living);
      }
    }
  }

  @Override
  public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    // only teleport to the center block
    if (context.getClickedPos().equals(pos)) {
      LivingEntity living = context.getPlayer();
      if (living != null && tryTeleport(modifier, living, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) {
        ToolDamageUtil.damageAnimated(tool, 2, living, context.getHand());
      }
    }
  }

  /** Checks if the given projectile allows teleporting */
  private static boolean canTeleport(ModDataNBT persistentData) {
    return !persistentData.getBoolean(SECONDARY_ARROW);
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (attacker != null && attacker != target && canTeleport(persistentData)) {
      Entity hitEntity = hit.getEntity();
      Vec3 oldPosition = attacker.position();
      if (attacker.level() == projectile.level() && tryTeleport(modifier, attacker, hitEntity.getX(), hitEntity.getY(), hitEntity.getZ()) && target != null) {
        tryTeleport(modifier, target, oldPosition.x, oldPosition.y, oldPosition.z);
      }
    }
    return false;
  }

  @Override
  public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    if (attacker != null && canTeleport(persistentData)) {
      BlockPos target = hit.getBlockPos().relative(hit.getDirection());
      // attempt the teleport, if successful and the projectile is not reusable then discard it
      if (attacker.level() == projectile.level() && tryTeleport(modifier, attacker, target.getX() + 0.5f, target.getY(), target.getZ() + 0.5f) && !projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)) {
        projectile.discard();
      }
    }
  }

  @Override
  public void onProjectileFuseFinish(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow) {
    if (canTeleport(persistentData) && projectile.getOwner() instanceof LivingEntity attacker) {
      // no need to discard, fuse did that for us
      if (attacker.level() == projectile.level()) {
        // teleport to the expired projectile
        Vec3 target = projectile.position();
        tryTeleport(modifier, attacker, target.x, target.y, target.z);
      }
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (primary) {
      // damage on shoot as we won't have tool context once the arrow lands
      ToolDamageUtil.damageAnimated(tool, 10, shooter, shooter.getUsedItemHand());
    } else {
      persistentData.putBoolean(SECONDARY_ARROW, true);
    }
  }
}
