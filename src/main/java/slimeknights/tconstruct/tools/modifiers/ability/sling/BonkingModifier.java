package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingAngleModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

/** Add velocity to the target away from yourself */
public class BonkingModifier extends SlingModifier implements MeleeHitModifierHook, MeleeDamageModifierHook {
  private static final float RANGE = 5F;
  /** Temporary boolean in persistent data. Means bonking is in progress, suppresses knockback and boosts damage. */
  public static final ResourceLocation IS_BONKING = TConstruct.getResource("is_bonking");

  @Override
  protected void registerHooks(Builder builder) {
    super.registerHooks(builder);
    builder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MELEE_DAMAGE);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      // apply use item speed always
      float speed = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED);
      // for melee weapons, also multiply in attack speed
      if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
        speed *= tool.getStats().get(ToolStats.ATTACK_SPEED);
      }
      tool.getPersistentData().putInt(GeneralInteractionModifierHook.KEY_DRAWTIME, (int)Math.ceil(30f / speed));
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (tool.getPersistentData().getBoolean(IS_BONKING)) {
      return 0;
    }
    return knockback;
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    if (tool.getPersistentData().getBoolean(IS_BONKING)) {
      damage *= 1.5f;
    }
    return damage;
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    Level level = entity.level();
    if (!level.isClientSide && (entity instanceof Player player)) {
      float charge = getCharge(tool, modifier, timeLeft);
      if (charge > 0) {
        Vec3 start = player.getEyePosition(1F);
        Vec3 look = player.getLookAngle();
        Vec3 direction = start.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);
        AABB bb = player.getBoundingBox().expandTowards(look.x * RANGE, look.y * RANGE, look.z * RANGE).expandTowards(1, 1, 1);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, start, direction, bb, (e) -> e instanceof LivingEntity);
        if (hit != null) {
          LivingEntity target = (LivingEntity)hit.getEntity();
          double targetDist = start.distanceToSqr(target.getEyePosition(1F));

          // cancel if there's a block in the way
          BlockHitResult mop = ModifiableItem.blockRayTrace(level, player, ClipContext.Fluid.NONE);
          if (mop.getType() != HitResult.Type.BLOCK || targetDist < mop.getBlockPos().distToCenterSqr(start)) {
            // melee tools also do damage as a treat
            boolean didBonk = false;
            if (tool.hasTag(TinkerTags.Items.MELEE) && !tool.getVolatileData().getBoolean(EntityInteractionModifierHook.NO_MELEE) && ToolAttackUtil.isAttackable(entity, target)) {
              didBonk = true;
              ModDataNBT data = tool.getPersistentData();
              data.putBoolean(IS_BONKING, true);
              InteractionHand hand = player.getUsedItemHand();
              ToolAttackContext.Builder builder = ToolAttackContext.attacker(entity).target(target).hand(hand).cooldown(Math.min(1, charge)).extraAttack();
              if (hand == InteractionHand.MAIN_HAND) {
                builder.applyAttributes();
              } else {
                builder.toolAttributes(tool);
              }
              ToolAttackUtil.performAttack(tool, builder.build());
              data.remove(IS_BONKING);
            }

            // send it flying
            float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
            RandomSource random = player.getRandom();
            float multiplier = charge * 3;
            float force = SlingForceModifierHook.modifySlingForce(tool, entity, target, modifier, getPower(tool, player) * multiplier, multiplier);
            Vec3 angle = Vec3.ZERO;
            if (force > 0) {
              // skip the bonk if we lack force, but the attack is fine
              angle = SlingAngleModifierHook.modifySlingAngle(tool, entity, target, modifier, force, multiplier, new Vec3(
                (-look.x + random.nextGaussian() * inaccuracy),
                0,
                (-look.z + random.nextGaussian() * inaccuracy)
              ));
              target.knockback(force, angle.x, angle.z);
              if (target instanceof ServerPlayer playerMP) {
                TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(target), playerMP);
              }
              didBonk = true;
            }

            // if we dealt damage or knockback, apply bonk effects
            if (didBonk) {
              // spawn the bonk particle
              ToolAttackUtil.spawnAttackParticle(TinkerTools.bonkAttackParticle.get(), player, 0.6d);

              // modifier callbacks
              SlingLaunchModifierHook.afterSlingLaunch(tool, entity, target, modifier, force, multiplier, angle);

              // cooldowns and stuff
              level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 0.5f);
              player.causeFoodExhaustion(0.2F);
              player.getCooldowns().addCooldown(tool.getItem(), 3);
              ToolDamageUtil.damageAnimated(tool, 1, entity);
              return;
            }
          }
        }
      }
      // play failure sound
      if (isActive(tool, modifier, activeModifier)) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 1f);
      }
    }
  }
}
