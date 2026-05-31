package slimeknights.tconstruct.shared;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import slimeknights.mantle.compat.neoforged.neoforge.registries.ForgeRegistries;
import slimeknights.mantle.compat.neoforged.neoforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.PotionDeferredRegister;
import slimeknights.mantle.registration.deferred.PotionDeferredRegister.PotionType;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.shared.effect.AntigravityEffect;
import slimeknights.tconstruct.shared.effect.ReturningEffect;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.effect.RepulsiveEffect;
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier.SelfDestructiveEffect;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;

/** Handles registration for all status effects and potions in the mod */
public class TinkerEffects extends TinkerModule {
  private static final PotionDeferredRegister POTIONS = new PotionDeferredRegister(TConstruct.MOD_ID);

  // slimy potions
  public static final RegistryObject<TinkerEffect> experienced = MOB_EFFECTS.register("experienced", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0x82c873, true).addAttributeModifier(TinkerAttributes.EXPERIENCE_MULTIPLIER, "ccffb654-9988-451e-9539-f74934274df1", 0.25f, Operation.ADD_MULTIPLIED_BASE));
  public static final RegistryObject<TinkerEffect> ricochet = MOB_EFFECTS.register("ricochet", () -> new TinkerEffect(MobEffectCategory.NEUTRAL, 0x01cbcd, true).addAttributeModifier(TinkerAttributes.KNOCKBACK_MULTIPLIER, "58a4bc13-366f-4f76-82f5-705451498c24", 0.5f, Operation.ADD_MULTIPLIED_BASE));
  public static final RegistryObject<TinkerEffect> enderference = MOB_EFFECTS.register("enderference", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xD37CFF, true));
  /** Projectile persistent data key to allow ranged modifiers to hit endermen. */
  public static final ResourceLocation ENDERFERENCE_KEY = enderference.getId();

  // slimy cakes
  public static final RegistryObject<TinkerEffect> bouncy = MOB_EFFECTS.register("bouncy", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0x71AC63, true).addAttributeModifier(TinkerAttributes.BOUNCY, "5de036ed-bc47-4965-9348-64c3ab5c8ae8", 1, Operation.ADD_VALUE));
  public static final RegistryObject<TinkerEffect> doubleJump = MOB_EFFECTS.register("double_jump", () -> new TinkerEffect(MobEffectCategory.BENEFICIAL, 0xA99B87, true).addAttributeModifier(TinkerAttributes.JUMP_COUNT, "9863601a-9d4a-4708-b348-4bf9fe6c0bbd", 1, Operation.ADD_VALUE));
  public static final RegistryObject<AntigravityEffect> antigravity = MOB_EFFECTS.register("antigravity", AntigravityEffect::new);
  public static final RegistryObject<ReturningEffect> returning = MOB_EFFECTS.register("returning", ReturningEffect::new);

  // modifier effects
  public static final RegistryObject<BleedingEffect> bleeding = MOB_EFFECTS.register("bleeding", BleedingEffect::new);
  public static final RegistryObject<MagneticEffect> magnetic = MOB_EFFECTS.register("magnetic", MagneticEffect::new);
  public static final RegistryObject<TinkerEffect> selfDestructing = MOB_EFFECTS.register("self_destructing", SelfDestructiveEffect::new);
  public static final RegistryObject<RepulsiveEffect> repulsive = MOB_EFFECTS.register("repulsive", RepulsiveEffect::new);
  public static final RegistryObject<TinkerEffect> pierce = MOB_EFFECTS.register("pierce", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xD1D37A, true).addAttributeModifier(Attributes.ARMOR, "cd45be7c-c86f-4a7e-813b-42a44a054f44", -1, Operation.ADD_VALUE));
  // damage boost
  public static final RegistryObject<TinkerEffect> conductive = MOB_EFFECTS.register("conductive", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xF2D500, true));
  public static final RegistryObject<TinkerEffect> venom = MOB_EFFECTS.register("venom", () -> new TinkerEffect(MobEffectCategory.HARMFUL, 0xA2935E, true));

  // potions
  public static final EnumObject<PotionType,Potion> experiencedPotion = POTIONS.registerTypes(experienced).withStrong().withLong().build();
  public static final EnumObject<PotionType,Potion> ricochetPotion = POTIONS.registerTypes(ricochet).withStrong().withLong().build();
  public static final EnumObject<PotionType,Potion> levitationPotion = POTIONS.registerTypes("levitation", () -> MobEffects.LEVITATION.value(), 15 * 20, 0).withStrong().withLong(40 * 20, 0).build();
  public static final EnumObject<PotionType,Potion> enderferencePotion = POTIONS.registerTypes(enderference, 90 * 20, 0).withLong().build();

  @SuppressWarnings("removal")
  public TinkerEffects() {
    POTIONS.register(slimeknights.tconstruct.TConstruct.getModBus());
    NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
  }

  void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
    brewing(event, experiencedPotion,  Potions.AWKWARD, Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.EARTH)));
    brewing(event, ricochetPotion,     Potions.AWKWARD, Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.SKY)));
    brewing(event, levitationPotion,   Potions.AWKWARD, Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.ICHOR)));
    brewing(event, enderferencePotion, Potions.AWKWARD, Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.ENDER)));
  }

  /** Registers recipes for brewing, longer and stronger potions for the given object */
  private static void brewing(RegisterBrewingRecipesEvent event, EnumObject<PotionType,Potion> potion, Holder<Potion> base, Ingredient ingredient) {
    Potion normal = potion.get(PotionType.NORMAL);
    event.getBuilder().addMix(base, ingredient.getItems()[0].getItem(), holder(normal));
    Potion longer = potion.getOrNull(PotionType.LONG);
    if (longer != null) {
      event.getBuilder().addMix(holder(normal), Items.REDSTONE, holder(longer));
    }
    Potion strong = potion.getOrNull(PotionType.STRONG);
    if (strong != null) {
      event.getBuilder().addMix(holder(normal), Items.GLOWSTONE_DUST, holder(strong));
    }
  }

  /** Gets a holder for a registered potion. */
  private static Holder<Potion> holder(Potion potion) {
    return BuiltInRegistries.POTION.wrapAsHolder(potion);
  }

  /** Gets a holder for a registered effect. */
  public static Holder<MobEffect> holder(RegistryObject<? extends MobEffect> effect) {
    return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect.get());
  }

  /** Checks if the given entity can be hit considering enderman enderference */
  public static boolean canHitWithProjectile(@Nullable LivingEntity living) {
    return living == null || living.getType() != EntityType.ENDERMAN || living.hasEffect(holder(enderference));
  }

  /** Checks if the given entity needs special casing for enderference */
  public static boolean needsEnderferenceOverride(@Nullable Entity entity) {
    return entity != null && entity.getType() == EntityType.ENDERMAN && entity instanceof LivingEntity living && living.hasEffect(holder(enderference));
  }

  /** Checks if the given entity needs special casing for enderference */
  public static boolean needsEnderferenceOverride(@Nullable LivingEntity living) {
    return living != null && living.getType() == EntityType.ENDERMAN && living.hasEffect(holder(enderference));
  }
}
