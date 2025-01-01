package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

public class ReflectingModifier extends Modifier {
  public ReflectingModifier() {
    MinecraftForge.EVENT_BUS.addListener(this::projectileImpact);
  }

  private void projectileImpact(ProjectileImpactEvent event) {
    Entity entity = event.getEntity();
    // first, need a projectile that is hitting a living entity
    Level level = entity.level();
    if (!level.isClientSide) {
      Projectile projectile = event.getProjectile();

      // handle blacklist for projectiles
      // living entity must be using one of our shields
      HitResult hit = event.getRayTraceResult();
      if (!RegistryHelper.contains(TinkerTags.EntityTypes.REFLECTING_BLACKLIST, projectile.getType())
          && hit.getType() == Type.ENTITY && ((EntityHitResult) hit).getEntity() instanceof LivingEntity living && living.isUsingItem() && living != projectile.getOwner()) {
        ItemStack stack = living.getUseItem();
        if (stack.is(TinkerTags.Items.SHIELDS)) {
          ToolStack tool = ToolStack.from(stack);
          // make sure we actually have the modifier
          int reflectingLevel = tool.getModifierLevel(this);
          if (reflectingLevel > 0) {
            ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
            if (activeModifier != ModifierEntry.EMPTY) {
              GeneralInteractionModifierHook hook = activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
              int time = hook.getUseDuration(tool, activeModifier) - living.getUseItemRemainingTicks();
              // must be blocking, started blocking within the last 2*level seconds, and be within the block angle
              if (hook.getUseAction(tool, activeModifier) == UseAnim.BLOCK
                  && (time >= 5 && time < 40 * reflectingLevel)
                  && InteractionHandler.canBlock(living, projectile.position(), tool)) {

                // time to actually reflect, this code is strongly based on code from the Parry mod
                // take ownership of the projectile so it counts as a player kill, except in the case of fishing bobbers
                if (!RegistryHelper.contains(TinkerTags.EntityTypes.REFLECTING_PRESERVE_OWNER, projectile.getType())) {
                  // arrows are dumb and mutate their pickup status when owner is set, so disagree and set it back
                  if (projectile instanceof AbstractArrow arrow) {
                    Pickup pickup = arrow.pickup;
                    arrow.setOwner(living);
                    arrow.pickup = pickup;
                  } else {
                    projectile.setOwner(living);
                  }
                  projectile.leftOwner = true;
                }

                Vec3 reboundAngle = living.getLookAngle();
                // use the shield accuracy and velocity stats when reflecting
                float velocity = ConditionalStatModifierHook.getModifiedStat(tool, living, ToolStats.VELOCITY) * 1.1f;
                projectile.shoot(reboundAngle.x, reboundAngle.y, reboundAngle.z, velocity, ModifierUtil.getInaccuracy(tool, living));
                if (projectile instanceof AbstractHurtingProjectile hurting) {
                  hurting.xPower = reboundAngle.x * 0.1;
                  hurting.yPower = reboundAngle.y * 0.1;
                  hurting.zPower = reboundAngle.z * 0.1;
                }
                if (living.getType() == EntityType.PLAYER) {
                  TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(projectile), living);
                }
                level.playSound(null, living.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.5F + level.random.nextFloat() * 0.4F);
                event.setImpactResult(ImpactResult.SKIP_ENTITY);
              }
            }
          }
        }
      }
    }
  }
}
