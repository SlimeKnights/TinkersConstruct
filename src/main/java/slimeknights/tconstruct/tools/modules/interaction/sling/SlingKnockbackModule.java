package slimeknights.tconstruct.tools.modules.interaction.sling;

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
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
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
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

/**
 * Add velocity to the target away from yourself
 * @param forceMultiplier     Base force multiplier to apply.
 * @param drawtimeMultiplier  Multiplier on the drawspeed to apply.
 * @param damageMultiplier    Multiplier on damage dealt to entities if the tool is melee capable. If 0, melee is skipped.
 */
public record SlingKnockbackModule(float forceMultiplier, float drawtimeMultiplier, float damageMultiplier, IJsonPredicate<LivingEntity> target, ModifierCondition<IToolStackView> condition) implements SlingModule, MeleeHitModifierHook, MeleeDamageModifierHook {
  private static final float RANGE = 5F;
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SlingKnockbackModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.MELEE_HIT, ModifierHooks.MELEE_DAMAGE);
  public static final RecordLoadable<SlingKnockbackModule> LOADER = RecordLoadable.create(
    FORCE_FIELD, DRAWTIME_FIELD,
    FloatLoadable.FROM_ZERO.requiredField("damage_multiplier", SlingKnockbackModule::damageMultiplier),
    TARGET_FIELD, ModifierCondition.TOOL_FIELD,
    SlingKnockbackModule::new);
  /** Temporary boolean in persistent data. Means bonking is in progress, suppresses knockback and boosts damage. */
  public static final ResourceLocation IS_BONKING = TConstruct.getResource("is_bonking");

  @Override
  public RecordLoadable<SlingKnockbackModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
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
    if (damageMultiplier != 1 && tool.getPersistentData().getBoolean(IS_BONKING)) {
      damage *= damageMultiplier;
    }
    return damage;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    // override to add in attack speed consideration
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK && condition.matches(tool, modifier)) {
      // apply use item speed always
      float speed = ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED);
      // for melee weapons, also multiply in attack speed
      if (tool.hasTag(TinkerTags.Items.MELEE_WEAPON)) {
        speed *= tool.getStats().get(ToolStats.ATTACK_SPEED);
      }
      tool.getPersistentData().putInt(GeneralInteractionModifierHook.KEY_DRAWTIME, (int)Math.ceil(20f * drawtimeMultiplier / speed));
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    Level level = entity.level();
    if (!level.isClientSide && condition.matches(tool, modifier) && entity instanceof Player player) {
      float charge = getCharge(tool, modifier, timeLeft);
      if (charge > 0) {
        Vec3 start = player.getEyePosition(1F);
        Vec3 look = player.getLookAngle();
        Vec3 direction = start.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);
        AABB bb = player.getBoundingBox().expandTowards(look.x * RANGE, look.y * RANGE, look.z * RANGE).expandTowards(1, 1, 1);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, start, direction, bb, e -> e instanceof LivingEntity);
        if (hit != null) {
          LivingEntity target = (LivingEntity)hit.getEntity();
          if (this.target.matches(target)) {
            double targetDist = start.distanceToSqr(target.getEyePosition(1F));

            // cancel if there's a block in the way
            BlockHitResult mop = ModifiableItem.blockRayTrace(level, player, ClipContext.Fluid.NONE);
            if (mop.getType() != HitResult.Type.BLOCK || targetDist < mop.getBlockPos().distToCenterSqr(start)) {
              // melee tools also do damage as a treat
              boolean didBonk = false;
              if (damageMultiplier > 0 && ToolAttackUtil.isAttackable(entity, target) && EntityInteractionModifierHook.isMelee(tool)) {
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
              float multiplier = charge * forceMultiplier;
              float force = SlingForceModifierHook.modifySlingForce(tool, entity, target, modifier, SlingModule.getPower(tool, player) * multiplier, multiplier);
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
      }
      // play failure sound
      if (ModifierUtil.isActiveModifier(tool, modifier, activeModifier)) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.BONK.getSound(), player.getSoundSource(), 1, 1f);
      }
    }
  }
}
