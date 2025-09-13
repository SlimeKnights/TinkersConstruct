package slimeknights.tconstruct.shared;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.AttributeDeferredRegister;
import slimeknights.tconstruct.TConstruct;

public class TinkerAttributes {
  private static final AttributeDeferredRegister ATTRIBUTES = new AttributeDeferredRegister(TConstruct.MOD_ID);

  public TinkerAttributes() {
    ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  // booleans
  /** If true, the entity will bounce. Used to implement slime boots */
  public static final RegistryObject<Attribute> BOUNCY = ATTRIBUTES.registerPercent("generic.bouncy", 0f, true);

  // stat replacements
  /** Changes the speed debuff percentage when the player moves while using an item */
  public static final RegistryObject<Attribute> USE_ITEM_SPEED = ATTRIBUTES.registerPercent("player.use_item_speed", 0.2f, true);
  /** Changes the speed debuff when the player moves while using an item */
  public static final RegistryObject<Attribute> PROTECTION_CAP = ATTRIBUTES.register("generic.protection_cap", 0.8, 0, 0.95f, true);
  /** Percentage boost to critical hits for any airborne attacker, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#dragonborn} */
  public static final RegistryObject<Attribute> CRITICAL_DAMAGE = ATTRIBUTES.register("player.critical_damage", 1.5f, 0, 100, false);

  // stat bonuses
  /** Bonus jump height in blocks */
  public static final RegistryObject<Attribute> JUMP_BOOST = ATTRIBUTES.register("generic.jump_boost", 0, 0, 100, true);
  /** Distance you can safely fall without damage */
  public static final RegistryObject<Attribute> SAFE_FALL_DISTANCE = ATTRIBUTES.register("generic.safe_fall_distance", 0, -10, 100, true);
  /** Number of jumps the player may perform, used by the double jump modifier. */
  public static final RegistryObject<Attribute> JUMP_COUNT = ATTRIBUTES.register("player.jump_count", 1, 1, 100, true);

  // stat multipliers
  /** Multiplier for knockback this entity takes. Similar to {@link net.minecraft.world.entity.ai.attributes.Attributes#KNOCKBACK_RESISTANCE} but can be used to increase knockback */
  public static final RegistryObject<Attribute> KNOCKBACK_MULTIPLIER = ATTRIBUTES.registerMultiplier("generic.knockback_multiplier", true);
  /** Player modifier data key for mining speed multiplier as an additive percentage boost on mining speed. Used for armor haste. */
  public static final RegistryObject<Attribute> MINING_SPEED_MULTIPLIER = ATTRIBUTES.registerMultiplier("player.mining_speed_multiplier", true);
  /** Attribute for experience from all sources */
  public static final RegistryObject<Attribute> EXPERIENCE_MULTIPLIER = ATTRIBUTES.registerMultiplier("player.experience_multiplier", false);
  /** Percentage boost to damage while crouching, used by {@link slimeknights.tconstruct.tools.data.ModifierIds#shulking} */
  public static final RegistryObject<Attribute> CROUCH_DAMAGE_MULTIPLIER = ATTRIBUTES.registerMultiplier("generic.crouch_damage_multiplier", false);
  // effect durations
  /** Percentage boost to positive potion effects */
  public static final RegistryObject<Attribute> GOOD_EFFECT_DURATION = ATTRIBUTES.registerMultiplier("generic.good_effect_duration_multiplier", false);
  /** Percentage boost to negative potion effects, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#magicProtection} */
  public static final RegistryObject<Attribute> BAD_EFFECT_DURATION = ATTRIBUTES.registerMultiplier("generic.bad_effect_duration_multiplier", false);


  @SubscribeEvent
  void addAttributes(EntityAttributeModificationEvent event) {
    // player attributes
    event.add(EntityType.PLAYER, USE_ITEM_SPEED.get());
    event.add(EntityType.PLAYER, CRITICAL_DAMAGE.get());
    event.add(EntityType.PLAYER, MINING_SPEED_MULTIPLIER.get());
    event.add(EntityType.PLAYER, EXPERIENCE_MULTIPLIER.get());
    event.add(EntityType.PLAYER, JUMP_COUNT.get());
    // general attributes
    addToAll(event, BOUNCY);
    addToAll(event, PROTECTION_CAP);
    addToAll(event, JUMP_BOOST);
    addToAll(event, SAFE_FALL_DISTANCE);
    addToAll(event, CROUCH_DAMAGE_MULTIPLIER);
    addToAll(event, KNOCKBACK_MULTIPLIER);
    addToAll(event, GOOD_EFFECT_DURATION);
    addToAll(event, BAD_EFFECT_DURATION);
  }

  /** Adds an attribute to all entities */
  private static void addToAll(EntityAttributeModificationEvent event, RegistryObject<Attribute> attribute, double defaultValue) {
    Attribute attr = attribute.get();
    for (EntityType<? extends LivingEntity> entity : event.getTypes()) {
      event.add(entity, attr, defaultValue);
    }
  }

  /** Adds an attribute to all entities */
  private static void addToAll(EntityAttributeModificationEvent event, RegistryObject<Attribute> attribute) {
    addToAll(event, attribute, attribute.get().getDefaultValue());
  }
}
