package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

/** Add velocity to the target away from yourself */
public class BonkingModifier extends SlingModifier implements MeleeHitModifierHook, MeleeDamageModifierHook {
  private static final float RANGE = 5F;
  /** If true, bonking is in progress, suppresses knockback and boosts damage */
  private static boolean isBonking = false;

  @Override
  protected void registerHooks(Builder builder) {
    super.registerHooks(builder);
    builder.addHook(this, ModifierHooks.MELEE_HIT);
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
    if (isBonking) {
      knockback = 0;
    }
    return knockback;
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    if (isBonking) {
      damage *= 1.5f;
    }
    return damage;
  }

  @Override
  public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    super.onStoppedUsing(tool, modifier, entity, timeLeft);
    if (!entity.level.isClientSide && (entity instanceof Player player)) {
      float f = getForce(tool, modifier, player, timeLeft, true);
      if (f > 0) {
        Vec3 start = player.getEyePosition(1F);
        Vec3 look = player.getLookAngle();
        Vec3 direction = start.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);
        AABB bb = player.getBoundingBox().expandTowards(look.x * RANGE, look.y * RANGE, look.z * RANGE).expandTowards(1, 1, 1);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player.level, player, start, direction, bb, (e) -> e instanceof LivingEntity);
        if (hit != null) {
          LivingEntity target = (LivingEntity)hit.getEntity();
          double targetDist = start.distanceToSqr(target.getEyePosition(1F));

          // cancel if there's a block in the way
          BlockHitResult mop = ModifiableItem.blockRayTrace(player.level, player, ClipContext.Fluid.NONE);
          if (mop.getType() != HitResult.Type.BLOCK || targetDist < mop.getBlockPos().distToCenterSqr(start)) {
            // melee tools also do damage as a treat
            if (tool.hasTag(TinkerTags.Items.MELEE)) {
              isBonking = true;
              InteractionHand hand = player.getUsedItemHand();
              ToolAttackUtil.attackEntity(tool, entity, hand, target, () -> Math.min(1, f), true);
              isBonking = false;
            }

            // send it flying
            float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
            RandomSource random = player.getRandom();
            target.knockback(f * 3, -look.x + random.nextGaussian() * inaccuracy, -look.z + random.nextGaussian() * inaccuracy);

            // spawn the bonk particle
            ToolAttackUtil.spawnAttackParticle(TinkerTools.bonkAttackParticle.get(), player, 0.6d);
            if (player instanceof ServerPlayer playerMP) {
              TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(player), playerMP);
            }

            // cooldowns and stuff
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 0.5f);
            player.causeFoodExhaustion(0.2F);
            player.getCooldowns().addCooldown(tool.getItem(), 3);
            ToolDamageUtil.damageAnimated(tool, 1, entity);
            return;
          }
        }
      }
      player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 1f);
    }
  }
}
