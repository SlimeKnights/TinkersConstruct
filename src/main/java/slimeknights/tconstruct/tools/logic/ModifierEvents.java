package slimeknights.tconstruct.tools.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.EffectImmunityModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.ranged.RestrictAngleModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Events to implement modifier specific behaviors, such as those defined by {@link TinkerDataKeys}. General hooks will typically be in {@link ToolEvents} */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class ModifierEvents {
  /** NBT key for items to preserve their slot in soulbound */
  private static final String SOULBOUND_SLOT = "tic_soulbound_slot";
  /** Multiplier for experience drops from events */
  private static final TinkerDataKey<Float> PROJECTILE_EXPERIENCE = TConstruct.createKey("projectile_experience");
  /** Volatile data flag making a modifier grant the tool soulbound */
  public static final ResourceLocation SOULBOUND = TConstruct.getResource("soulbound");

  @SubscribeEvent
  static void onKnockback(LivingKnockBackEvent event) {
    event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      float knockback = data.get(TinkerDataKeys.KNOCKBACK, 0f);
      if (knockback != 0) {
        // adds +20% knockback per level
        event.setStrength(event.getStrength() * (1 + knockback));
      }
      // apply crystalbound bonus
      int crystalbound = data.get(TinkerDataKeys.CRYSTALSTRIKE, 0);
      if (crystalbound > 0) {
        RestrictAngleModule.onKnockback(event, crystalbound);
      }
    });
  }

  /** Reduce fall distance for fall damage */
  @SubscribeEvent
  static void onLivingFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntity();
    float boost = ArmorStatModule.getStat(entity, TinkerDataKeys.JUMP_BOOST);
    if (boost > 0) {
      event.setDistance(Math.max(event.getDistance() - boost, 0));
    }
  }

  /** Called on jumping to boost the jump height of the entity */
  @SubscribeEvent
  public static void onLivingJump(LivingJumpEvent event) {
    LivingEntity entity = event.getEntity();
    float boost = ArmorStatModule.getStat(entity, TinkerDataKeys.JUMP_BOOST);
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
    if (!entity.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && entity instanceof Player player && !(player instanceof FakePlayer)) {
      // start with the hotbar, must be soulbound or soul belt
      boolean soulBelt = ArmorLevelModule.getLevel(player, TinkerDataKeys.SOUL_BELT) > 0;
      Inventory inventory = player.getInventory();
      int hotbarSize = Inventory.getSelectionSize();
      for (int i = 0; i < hotbarSize; i++) {
        ItemStack stack = inventory.getItem(i);
        if (!stack.isEmpty() && (soulBelt || ModifierUtil.checkVolatileFlag(stack, SOULBOUND))) {
          stack.getOrCreateTag().putInt(SOULBOUND_SLOT, i);
        }
      }
      // rest of the inventory, only check soulbound (no modifier that moves non-soulbound currently)
      // note this includes armor and offhand
      int totalSize = inventory.getContainerSize();
      for (int i = hotbarSize; i < totalSize; i++) {
        ItemStack stack = inventory.getItem(i);
        if (!stack.isEmpty() && ModifierUtil.checkVolatileFlag(stack, SOULBOUND)) {
          stack.getOrCreateTag().putInt(SOULBOUND_SLOT, i);
        }
      }
    }
  }


  /* Experience */

  /**
   * Boosts the original based on the level
   * @param original  Original amount
   * @param bonus     Bonus percent
   * @return  Boosted XP
   */
  private static int boost(int original, float bonus) {
    return (int) (original  * (1 + bonus));
  }

  @SubscribeEvent
  static void beforeBlockBreak(BreakEvent event) {
    float bonus = ArmorStatModule.getStat(event.getPlayer(), TinkerDataKeys.EXPERIENCE);
    if (bonus != 0) {
      event.setExpToDrop(boost(event.getExpToDrop(), bonus));
    }
  }

  @SubscribeEvent
  static void onExperienceDrop(LivingExperienceDropEvent event) {
    // always add armor boost, unfortunately no good way to stop shield stuff here
    float armorBoost = 0;
    Player player = event.getAttackingPlayer();
    if (player != null) {
      armorBoost = ArmorStatModule.getStat(player, TinkerDataKeys.EXPERIENCE);
    }
    // if the target was killed by an experienced arrow, use that level
    float projectileBoost = event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(PROJECTILE_EXPERIENCE)).orElse(-1f);
    if (projectileBoost > 0) {
      event.setDroppedExperience(boost(event.getDroppedExperience(), projectileBoost * 0.5f + armorBoost));
      // experienced being zero means it was our arrow but it was not modified, do not check the held item in that case
    } else if (projectileBoost != 0 && player != null) {
      // not an arrow, just use the player's experienced level
      ToolStack tool = Modifier.getHeldTool(player, ModifierLootingHandler.getLootingSlot(player));
      float boost = (tool != null ? tool.getModifier(ModifierIds.experienced).getEffectiveLevel() : 0) * 0.5f + armorBoost;
      if (boost > 0) {
        event.setDroppedExperience(boost(event.getDroppedExperience(), boost));
      }
    }
  }


  /* Soulbound */

  /** Called when the player dies to store the item in the original inventory */
  @SubscribeEvent
  static void onPlayerDropItems(LivingDropsEvent event) {
    // only care about real players with keep inventory off
    LivingEntity entity = event.getEntity();
    if (!entity.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && entity instanceof Player player && !(entity instanceof FakePlayer)) {
      Collection<ItemEntity> drops = event.getDrops();
      Iterator<ItemEntity> iter = drops.iterator();
      Inventory inventory = player.getInventory();
      List<ItemEntity> takenSlot = new ArrayList<>();
      while (iter.hasNext()) {
        ItemEntity itemEntity = iter.next();
        ItemStack stack = itemEntity.getItem();
        // find items with our soulbound tag set and move them back into the inventory, will move them over later
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(SOULBOUND_SLOT, Tag.TAG_ANY_NUMERIC)) {
          int slot = tag.getInt(SOULBOUND_SLOT);
          // return the tool to its requested slot if possible, remove from the drops
          if (inventory.getItem(slot).isEmpty()) {
            inventory.setItem(slot, stack);
          } else {
            // hold off on handling items that did not get the requested slot for now
            // want to make sure they don't get in the way of items that have not yet been seen
            takenSlot.add(itemEntity);
          }
          iter.remove();
          // don't clear the tag yet, we need it one last time for player clone
        }
      }
      // handle items that did not get their requested slot last, to ensure they don't take someone else's slot while being added to a default
      for (ItemEntity itemEntity : takenSlot) {
        ItemStack stack = itemEntity.getItem();
        if (!inventory.add(stack)) {
          // last resort, somehow we just cannot put the stack anywhere, so drop it on the ground
          // this should never happen, but better to be safe
          // ditch the soulbound slot tag, to prevent item stacking issues
          CompoundTag tag = stack.getTag();
          if (tag != null) {
            tag.remove(SOULBOUND_SLOT);
            if (tag.isEmpty()) {
              stack.setTag(null);
            }
          }
          drops.add(itemEntity);
        }
      }
    }
  }

  /** Called when the new player is created to fetch the soulbound item from the old */
  @SubscribeEvent
  static void onPlayerClone(PlayerEvent.Clone event) {
    if (!event.isWasDeath()) {
      return;
    }
    Player original = event.getOriginal();
    Player clone = event.getEntity();
    // inventory already copied
    if (clone.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || original.isSpectator()) {
      return;
    }
    // find items with the soulbound tag set and move them over
    Inventory originalInv = original.getInventory();
    Inventory cloneInv = clone.getInventory();
    int size = Math.min(originalInv.getContainerSize(), cloneInv.getContainerSize()); // not needed probably, but might as well be safe
    for(int i = 0; i < size; i++) {
      ItemStack stack = originalInv.getItem(i);
      if (!stack.isEmpty()) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(SOULBOUND_SLOT, Tag.TAG_ANY_NUMERIC)) {
          cloneInv.setItem(i, stack);
          // remove the slot tag, clear the tag if needed
          tag.remove(SOULBOUND_SLOT);
          if (tag.isEmpty()) {
            stack.setTag(null);
          }
        }
      }
    }
  }

  /** Boosts critical hit damage */
  @SubscribeEvent
  static void onCritical(CriticalHitEvent event) {
    if (event.getResult() != Result.DENY) {
      // force critical if not already critical and in the air
      LivingEntity living = event.getEntity();

      // boost critical hits based on the armor stat
      float criticalBoost = ArmorStatModule.getStat(living, TinkerDataKeys.CRITICAL_DAMAGE);
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
          event.setDamageModifier(event.getDamageModifier() + criticalBoost);
        }
      }
    }
  }

  @SubscribeEvent
  static void onPotionStart(MobEffectEvent.Added event) {
    MobEffectInstance newEffect = event.getEffectInstance();
    if (!newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntity();

      // use two different stats based on whether the effect is beneficial
      float boost = ArmorStatModule.getStat(event.getEntity(), newEffect.getEffect().isBeneficial() ? TinkerDataKeys.GOOD_EFFECT_DURATION : TinkerDataKeys.BAD_EFFECT_DURATION);
      if (boost != 0) {
        // adjust duration as requested
        int duration = (int)(newEffect.getDuration() * (1 + boost));
        if (duration < 0) {
          duration = 0;
        }
        newEffect.duration = duration;
      }
    }
  }

  /** Key to mark entities that were knocked back by an explosion, so we know to adjust their velocity */
  private final static TinkerDataKey<Boolean> WAS_KNOCKBACK = TConstruct.createKey("was_knockback");

  /** On explosion, checks if any blast protected entity is involved, if so marks them for knockback update next tick */
  @SubscribeEvent
  static void onExplosionDetonate(ExplosionEvent.Detonate event) {
    // TODO 1.20: this should no longer be needed
    Explosion explosion = event.getExplosion();
    Vec3 center = explosion.getPosition();
    float diameter = explosion.radius * 2;
    // search the entities for someone protection by blast protection
    for (Entity entity : event.getAffectedEntities()) {
      // explosion is valid as long as the entity's eye is not directly on the explosion
      double x = entity.getX() - center.x;
      double z = entity.getZ() - center.z;
      if (!entity.ignoreExplosion() && x != 0 || z != 0 || (entity.getEyeY() - center.y) != 0) {
        entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
          // if the entity has blast protection and the blast protection level is bigger than vanilla, time to process
          float explosionKnockback = data.get(TinkerDataKeys.EXPLOSION_KNOCKBACK, 0f);
          if (explosionKnockback != 0) {
            // we need two numbers to calculate the knockback: distance to explosion and block density
            double y = entity.getY() - center.y;
            double distance = Mth.sqrt((float)(x * x + y * y + z * z)) / diameter;
            if (distance <= 1) {
              data.put(WAS_KNOCKBACK, true);
            }
          }
        });
      }
    }
  }

  /** If the entity is marked for knockback update, adjust velocity */
  @SubscribeEvent
  static void livingTick(LivingTickEvent event) {
    LivingEntity living = event.getEntity();
    if (!living.level.isClientSide && !living.isSpectator()) {
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        if (data.get(WAS_KNOCKBACK, false)) {
          data.remove(WAS_KNOCKBACK);
          float explosionKnockback = data.get(TinkerDataKeys.EXPLOSION_KNOCKBACK, 0f);
          if (explosionKnockback != 0) {
            // due to MC-198809, vanilla does not actually reduce the knockback except on levels higher than obtainable in survival (blast prot VII)
            // thus, we only care about our own level for reducing
            double scale = 1 + explosionKnockback;
            if (scale <= 0) {
              living.setDeltaMovement(Vec3.ZERO);
            } else {
              living.setDeltaMovement(living.getDeltaMovement().multiply(scale, scale, scale));
            }
            living.hurtMarked = true;
          }
        }
      });
    }
  }
}
