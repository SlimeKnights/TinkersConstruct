package slimeknights.tconstruct.tools.logic;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.mantle.MantleEvents;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.EffectImmunityModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.ranged.RestrictAngleModule;

import java.util.List;
import java.util.Optional;

/** Events to implement modifier specific behaviors, such as those defined by {@link TinkerDataKeys}. General hooks will typically be in {@link ToolEvents} */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class ModifierEvents {
  /** Multiplier for experience drops from events */
  private static final TinkerDataKey<Float> PROJECTILE_EXPERIENCE = TConstruct.createKey("projectile_experience");
  /** Volatile data flag making a modifier grant the tool soulbound */
  public static final ResourceLocation SOULBOUND = TConstruct.getResource("soulbound");
  /** Volatile data int for making a modifier on a shield grant reflecting */
  public static final ResourceLocation REFLECTING = TConstruct.getResource("reflecting");

  @SuppressWarnings("removal")
  @SubscribeEvent
  static void onKnockback(LivingKnockBackEvent event) {
    LivingEntity entity = event.getEntity();
    Optional<TinkerDataCapability.Holder> dataCap = entity.getCapability(TinkerDataCapability.CAPABILITY).resolve();
    double knockback = entity.getAttributeValue(TinkerAttributes.KNOCKBACK_MULTIPLIER.get())
                     + dataCap.map(data -> data.get(TinkerDataKeys.KNOCKBACK)).orElse(0f);
    if (knockback != 1) {
      event.setStrength((float) (event.getStrength() * knockback));
    }
    // handle crystalstrike
    dataCap.ifPresent(data -> {
      // apply crystalbound bonus
      int crystalbound = data.get(TinkerDataKeys.CRYSTALSTRIKE, 0);
      if (crystalbound > 0) {
        RestrictAngleModule.onKnockback(event, crystalbound);
      }
    });
  }

  /** Reduce fall distance for fall damage */
  @SuppressWarnings("removal")
  @SubscribeEvent
  static void onLivingFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntity();
    double boost = entity.getAttributeValue(TinkerAttributes.SAFE_FALL_DISTANCE.get()) + ArmorStatModule.getStat(entity, TinkerDataKeys.JUMP_BOOST);
    if (boost != 0) {
      event.setDistance((float) Math.max(event.getDistance() - boost, 0));
    }
  }

  /** Called on jumping to boost the jump height of the entity */
  @SuppressWarnings("removal")
  @SubscribeEvent
  public static void onLivingJump(LivingJumpEvent event) {
    LivingEntity entity = event.getEntity();
    double boost = entity.getAttributeValue(TinkerAttributes.JUMP_BOOST.get()) + ArmorStatModule.getStat(entity, TinkerDataKeys.JUMP_BOOST);
    if (boost > 0) {
      entity.setDeltaMovement(entity.getDeltaMovement().add(0, boost * 0.1, 0));
    }
  }

  /** Prevents effects on the entity */
  @SubscribeEvent
  static void isPotionApplicable(MobEffectEvent.Applicable event) {
    event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      if (data.computeIfAbsent(EffectImmunityModule.EFFECT_IMMUNITY).contains(event.getEffectInstance().getEffect())) {
        event.setResult(Result.DENY);
      }
    });
  }

  /** Called when the player dies to store the item in the original inventory */
  @SubscribeEvent
  static void onLivingDeath(LivingDeathEvent event) {
    // if a projectile kills the target, mark the projectile level
    DamageSource source = event.getSource();
    if (source != null && source.getDirectEntity() instanceof Projectile projectile) {
      ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);
      if (!modifiers.isEmpty()) {
        event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(PROJECTILE_EXPERIENCE, modifiers.getEntry(ModifierIds.experienced).getEffectiveLevel()));
      }
    }
    // this is the latest we can add slot markers to the items so we can return them to slots
    LivingEntity entity = event.getEntity();
    if (!entity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && entity instanceof Player player && !(player instanceof FakePlayer)) {
      // start with the hotbar, must be soulbound or soul belt
      boolean soulBelt = ArmorLevelModule.getLevel(player, TinkerDataKeys.SOUL_BELT) > 0;
      Inventory inventory = player.getInventory();
      int hotbarSize = Inventory.getSelectionSize();
      for (int i = 0; i < hotbarSize; i++) {
        ItemStack stack = inventory.getItem(i);
        if (!stack.isEmpty() && (soulBelt || ModifierUtil.checkVolatileFlag(stack, SOULBOUND))) {
          stack.getOrCreateTag().putInt(MantleEvents.SOULBOUND_SLOT, i);
        }
      }
      // rest of the inventory, only check soulbound (no modifier that moves non-soulbound currently)
      // note this includes armor and offhand
      int totalSize = inventory.getContainerSize();
      for (int i = hotbarSize; i < totalSize; i++) {
        ItemStack stack = inventory.getItem(i);
        if (!stack.isEmpty() && ModifierUtil.checkVolatileFlag(stack, SOULBOUND)) {
          stack.getOrCreateTag().putInt(MantleEvents.SOULBOUND_SLOT, i);
        }
      }
    }
  }


  /* Experience */

  @SuppressWarnings("removal")
  @SubscribeEvent
  static void beforeBlockBreak(BreakEvent event) {
    Player player = event.getPlayer();
    // directly use modifier for held to ensure the correct hand applies
    // TODO: can we make that datapack configurable?
    double bonus = player.getAttributeValue(TinkerAttributes.EXPERIENCE_MULTIPLIER.get())
                 + ModifierUtil.getModifierLevel(player.getMainHandItem(), ModifierIds.experienced) * 0.5f
                 + ArmorStatModule.getStat(player, TinkerDataKeys.EXPERIENCE);
    event.setExpToDrop((int)(event.getExpToDrop() * bonus));
  }

  @SuppressWarnings("removal")
  @SubscribeEvent
  static void onExperienceDrop(LivingExperienceDropEvent event) {
    // boost entity experience if they are under the effects of experienced
    LivingEntity entity = event.getEntity();
    MobEffectInstance instance = entity.getEffect(TinkerEffects.experienced.get());
    double armorMultiplier = 1 + (instance != null ? instance.getAmplifier() : 0);

    // always add armor boost, unfortunately no good way to stop shield stuff here
    Player player = event.getAttackingPlayer();
    if (player != null) {
      armorMultiplier *= player.getAttributeValue(TinkerAttributes.EXPERIENCE_MULTIPLIER.get()) + ArmorStatModule.getStat(player, TinkerDataKeys.EXPERIENCE);
    }
    // if the target was killed by an experienced arrow, use that level
    float projectileBoost = entity.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(PROJECTILE_EXPERIENCE)).orElse(-1f);
    if (projectileBoost >= 0) {
      event.setDroppedExperience((int) (event.getDroppedExperience() * armorMultiplier + projectileBoost * 0.5));
      // experienced being zero means it was our arrow, but it was not modified with experienced. Being -1 means no projectile was involved, so boost by hand
    } else if (player != null) {
      // not an arrow, just use the player's experienced level
      ToolStack tool = Modifier.getHeldTool(player, ModifierLootingHandler.getLootingSlot(player));
      double multiplier = armorMultiplier + (tool != null ? tool.getModifier(ModifierIds.experienced).getEffectiveLevel() : 0) * 0.5;
      event.setDroppedExperience((int) (event.getDroppedExperience() * multiplier));
    }
  }

  /** Boosts critical hit damage */
  @SuppressWarnings("removal")
  @SubscribeEvent
  static void onCritical(CriticalHitEvent event) {
    if (event.getResult() != Result.DENY) {
      // force critical if not already critical and in the air
      LivingEntity living = event.getEntity();

      // critical boost is defined where the base value is 150%, setting smaller amounts can reduce the critical damage
      // this event however is defined in terms of adding or subtracting critical, so just treat it as additive
      Attribute attribute = TinkerAttributes.CRITICAL_DAMAGE.get();
      double criticalBoost = living.getAttributeValue(attribute) - attribute.getDefaultValue() + ArmorStatModule.getStat(living, TinkerDataKeys.CRITICAL_DAMAGE);
      if (criticalBoost > 0) {
        // make it critical if we meet our simpler conditions, note this does not boost attack damage
        boolean isCritical = event.isVanillaCritical() || event.getResult() == Result.ALLOW;
        if (!isCritical && TinkerPredicate.AIRBORNE.matches(living)) {
          isCritical = true;
          event.setResult(Result.ALLOW);
        }

        // if we either were or became critical, time to boost
        if (isCritical) {
          // adds +5% critical hit per level
          event.setDamageModifier((float) (event.getDamageModifier() + criticalBoost));
        }
      }
    }
  }

  @SuppressWarnings("removal")
  @SubscribeEvent
  static void onPotionStart(MobEffectEvent.Added event) {
    MobEffectInstance newEffect = event.getEffectInstance();
    if (!newEffect.getCurativeItems().isEmpty()) {
      // use two different stats based on whether the effect is beneficial
      boolean beneficial = newEffect.getEffect().isBeneficial();
      LivingEntity entity = event.getEntity();
      double multiplier = entity.getAttributeValue(beneficial ? TinkerAttributes.GOOD_EFFECT_DURATION.get() : TinkerAttributes.BAD_EFFECT_DURATION.get())
                        + ArmorStatModule.getStat(entity, beneficial ? TinkerDataKeys.GOOD_EFFECT_DURATION : TinkerDataKeys.BAD_EFFECT_DURATION);
      if (multiplier != 1) {
        // adjust duration as requested
        int duration = (int)(newEffect.getDuration() * multiplier);
        if (duration < 0) {
          duration = 0;
        }
        newEffect.duration = duration;
      }
    }
  }

  /** Called when an entity lands to handle bouncing */
  @SubscribeEvent
  static void bounceOnFall(LivingFallEvent event) {
    LivingEntity living = event.getEntity();
    // using fall distance as the event distance could be reduced by jump boost
    if (living == null || (living.getDeltaMovement().y > -0.3 && living.fallDistance < 3)) {
      return;
    }
    // can the entity bounce?
    if (living.getAttributeValue(TinkerAttributes.BOUNCY.get()) < 1) {
      return;
    }

    // reduced fall damage when crouching
    if (living.isSuppressingBounce()) {
      event.setDamageMultiplier(0.5f);
      return;
    } else {
      event.setDamageMultiplier(0.0f);
    }

    // server players behave differently than non-server players, they have no velocity during the event, so we need to reverse engineer it
    Vec3 motion = living.getDeltaMovement();
    if (living instanceof ServerPlayer) {
      // velocity is lost on server players, but we dont have to defer the bounce
      double gravity = living.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
      double time = Math.sqrt(living.fallDistance / gravity);
      double velocity = gravity * time;
      living.setDeltaMovement(motion.x / 0.975f, velocity, motion.z / 0.975f);
      living.hurtMarked = true;

      // preserve momentum
      SlimeBounceHandler.addBounceHandler(living);
    } else {
      // for non-players, need to defer the bounce
      // only slow down half as much when bouncing
      float factor = living.fallDistance < 2 ? -0.7f : -0.9f;
      living.setDeltaMovement(motion.x / 0.975f, motion.y * factor, motion.z / 0.975f);
      SlimeBounceHandler.addBounceHandler(living, living.getDeltaMovement());
    }
    // update airborn status
    event.setDistance(0.0F);
    if (!living.level().isClientSide) {
      living.hasImpulse = true;
      event.setCanceled(true);
      living.setOnGround(false); // need to be on ground for server to process this event
    }
    living.playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
  }

  @SubscribeEvent
  static void onProjectile(LivingGetProjectileEvent event) {
    // the held projectile method is not stack sensitive, so use this instead
    ItemStack bow = event.getProjectileWeaponItemStack();
    ItemStack ammo = event.getProjectileItemStack();
    // if the bow supports it, and we currently have arrows or nothing, we have a chance to swap the ammo
    // skip if the b
    if (bow.is(TinkerTags.Items.BALLISTAS) && ModifierUtil.checkVolatileFlag(bow, ModifiableBowItem.KEY_BALLISTA) && (ammo.isEmpty() || ammo.is(ItemTags.ARROWS))) {
      // check active flag
      int flag = ModifierUtil.getPersistentInt(bow, ModifiableBowItem.KEY_BALLISTA, 0);

      // if requesting a held ballista or haven't decided, find it in either hand
      if (flag <= ModifiableBowItem.FLAG_BALLISTA_HELD) {
        // try both hands, but don't return the bow itself
        LivingEntity entity = event.getEntity();
        ItemStack check = entity.getOffhandItem();
        if (check != bow && check.is(TinkerTags.Items.BALLISTA_AMMO)) {
          event.setProjectileItemStack(check);
        }
        check = entity.getMainHandItem();
        if (check != bow && check.is(TinkerTags.Items.BALLISTA_AMMO)) {
          event.setProjectileItemStack(check);
        }
      // if requesting a ballista from the quiver, cancel whatever stack we got from inventory
      } else if (flag == ModifiableBowItem.FLAG_BALLISTA_QUIVER) {
        event.setProjectileItemStack(ItemStack.EMPTY);
      }
    }
  }

  @SuppressWarnings("removal") // lets us work with Neo 1.20 for now
  @SubscribeEvent(priority = EventPriority.LOW) // lower priority so general modifier hook runs first
  static void projectileImpact(ProjectileImpactEvent event) {
    Entity entity = event.getEntity();
    Level level = entity.level();
    Projectile projectile = event.getProjectile();
    HitResult hit = event.getRayTraceResult();
    if (hit.getType() == Type.ENTITY && ((EntityHitResult) hit).getEntity() instanceof LivingEntity target) {
      // reflecting //
      // handle blacklist for projectiles
      if (!level.isClientSide && !RegistryHelper.contains(TinkerTags.EntityTypes.REFLECTING_BLACKLIST, projectile.getType()) && target != projectile.getOwner() && target.isUsingItem()) {
        ItemStack stack = target.getUseItem();
        // living entity must be using one of our shields
        if (stack.is(TinkerTags.Items.SHIELDS)) {
          ToolStack tool = ToolStack.from(stack);
          // make sure we actually have the modifier
          int reflectingTime = tool.getVolatileData().getInt(REFLECTING);
          if (reflectingTime > 0) {
            ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
            if (activeModifier != ModifierEntry.EMPTY) {
              GeneralInteractionModifierHook hook = activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
              int time = hook.getUseDuration(tool, activeModifier) - target.getUseItemRemainingTicks();
              // must be blocking, started blocking within the last 2*level seconds, and be within the block angle
              if (hook.getUseAction(tool, activeModifier) == UseAnim.BLOCK
                && (time >= 5 && time < reflectingTime)
                && InteractionHandler.canBlock(target, projectile.position(), tool)) {

                // time to actually reflect, this code is strongly based on code from the Parry mod
                // take ownership of the projectile so it counts as a player kill, except in the case of fishing bobbers
                if (!RegistryHelper.contains(TinkerTags.EntityTypes.REFLECTING_PRESERVE_OWNER, projectile.getType())) {
                  // arrows are dumb and mutate their pickup status when owner is set, so disagree and set it back
                  if (projectile instanceof AbstractArrow arrow) {
                    Pickup pickup = arrow.pickup;
                    arrow.setOwner(target);
                    arrow.pickup = pickup;
                  } else {
                    projectile.setOwner(target);
                  }
                  projectile.leftOwner = true;
                }

                Vec3 reboundAngle = target.getLookAngle();
                // use the shield accuracy and velocity stats when reflecting
                float velocity = ConditionalStatModifierHook.getModifiedStat(tool, target, ToolStats.VELOCITY) * 1.1f;
                projectile.shoot(reboundAngle.x, reboundAngle.y, reboundAngle.z, velocity, ModifierUtil.getInaccuracy(tool, target));
                if (projectile instanceof AbstractHurtingProjectile hurting) {
                  hurting.xPower = reboundAngle.x * 0.1;
                  hurting.yPower = reboundAngle.y * 0.1;
                  hurting.zPower = reboundAngle.z * 0.1;
                }
                if (target.getType() == EntityType.PLAYER) {
                  TinkerNetwork.getInstance().sendVanillaPacket(new ClientboundSetEntityMotionPacket(projectile), target);
                }
                level.playSound(null, target.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.5F + level.random.nextFloat() * 0.4F);
                event.setImpactResult(ImpactResult.SKIP_ENTITY);
                // damage the shield, and stop using it if needed
                if (ToolDamageUtil.damageAnimated(tool, 3, target)) {
                  target.stopUsingItem();
                  entity.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + entity.level().random.nextFloat() * 0.4F);
                }
              }
            }
          }
        }
      }

      // enderference //
      // endermen are hardcoded to not take arrow damage, so disagree by reimplementing arrow damage right here
      // blacklist lets us not run on tridents or thrown tools. Former does not work with enderference, latter does so internally
      if (TinkerEffects.needsEnderferenceOverride(target) && !projectile.getType().is(TinkerTags.EntityTypes.ENDERFERENCE_ARROW_BLACKLIST) && projectile instanceof AbstractArrow arrow) {
        // first, give up if we reached pierce capacity, and ensure list are created
        if (arrow.getPierceLevel() > 0) {
          if (arrow.piercingIgnoreEntityIds == null) {
            arrow.piercingIgnoreEntityIds = new IntOpenHashSet(5);
          }
          if (arrow.piercedAndKilledEntities == null) {
            arrow.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
          }
          if (arrow.piercingIgnoreEntityIds.size() >= arrow.getPierceLevel() + 1) {
            arrow.discard();
            event.setCanceled(true);
            return;
          }
          arrow.piercingIgnoreEntityIds.add(target.getId());
        }

        // calculate damage, bonus on crit
        int damage = Mth.ceil(Mth.clamp(arrow.getDeltaMovement().length() * arrow.getBaseDamage(), 0.0D, Integer.MAX_VALUE));
        if (arrow.isCritArrow()) {
          damage = (int) Math.min(target.getRandom().nextInt(damage / 2 + 2) + (long) damage, Integer.MAX_VALUE);
        }

        // create damage source, don't use projectile sources as that makes endermen ignore it
        Entity owner = arrow.getOwner();
        DamageSource damageSource = CombatHelper.damageSource(TinkerDamageTypes.MELEE_ARROW, projectile, owner);
        LivingEntity livingOwner = ModifierUtil.asLiving(owner);
        if (livingOwner != null) {
          livingOwner.setLastHurtMob(target);
        }

        // handle fire
        int remainingFire = target.getRemainingFireTicks();
        if (arrow.isOnFire()) {
          target.setSecondsOnFire(5);
        }

        // hurt the enderman
        if (target.hurt(damageSource, (float) damage)) {
          if (!level.isClientSide && arrow.getPierceLevel() <= 0) {
            target.setArrowCount(target.getArrowCount() + 1);
          }

          // knockback from punch
          int knockback = arrow.getKnockback();
          if (knockback > 0) {
            Vec3 knockbackVec = arrow.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(knockback * 0.6D);
            if (knockbackVec.lengthSqr() > 0.0D) {
              target.push(knockbackVec.x, 0.1D, knockbackVec.z);
            }
          }

          if (!level.isClientSide && livingOwner != null) {
            EnchantmentHelper.doPostHurtEffects(target, livingOwner);
            EnchantmentHelper.doPostDamageEffects(livingOwner, target);
          }

          arrow.doPostHurtEffects(target);

          if (!target.isAlive() && arrow.piercedAndKilledEntities != null) {
            arrow.piercedAndKilledEntities.add(target);
          }

          if (!level.isClientSide && arrow.shotFromCrossbow() && owner instanceof ServerPlayer player) {
            if (arrow.piercedAndKilledEntities != null) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, arrow.piercedAndKilledEntities);
            } else if (!target.isAlive()) {
              CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(player, List.of(target));
            }
          }

          arrow.playSound(arrow.soundEvent, 1.0F, 1.2F / (target.getRandom().nextFloat() * 0.2F + 0.9F));
          if (arrow.getPierceLevel() <= 0) {
            arrow.discard();
          }
        } else {
          // reset fire and drop the arrow
          target.setRemainingFireTicks(remainingFire);
          arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-0.1D));
          arrow.setYRot(arrow.getYRot() + 180.0F);
          arrow.yRotO += 180.0F;
          if (!level.isClientSide && arrow.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
              arrow.spawnAtLocation(arrow.getPickupItem(), 0.1F);
            }

            arrow.discard();
          }
        }
        // cancel event so arrow does not bounce
        event.setCanceled(true);
      }
    }
  }

  @SubscribeEvent
  static void onTeleport(EntityTeleportEvent event) {
    if (event.getEntity() instanceof LivingEntity living && living.hasEffect(TinkerEffects.enderference.get())) {
      event.setCanceled(true);
    }
  }
}
