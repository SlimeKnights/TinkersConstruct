package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.object.ItemEnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.effect.TinkerEffect;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.modifiers.ability.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.LuckModifier;
import slimeknights.tconstruct.tools.modifiers.ability.SilkyModifier;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.free.WorldboundModifier;
import slimeknights.tconstruct.tools.modifiers.shared.ExperiencedModifier;
import slimeknights.tconstruct.tools.modifiers.shared.ExtraModifier;
import slimeknights.tconstruct.tools.modifiers.shared.ExtraModifier.ExtraType;
import slimeknights.tconstruct.tools.modifiers.shared.ExtraModifier.ModifierSource;
import slimeknights.tconstruct.tools.modifiers.shared.LevelDamageModifier;
import slimeknights.tconstruct.tools.modifiers.shared.ReinforcedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.CultivatedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DwarvenModifier;
import slimeknights.tconstruct.tools.modifiers.traits.HeavyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.InsatibleModifier;
import slimeknights.tconstruct.tools.modifiers.traits.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.LightweightModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier2;
import slimeknights.tconstruct.tools.modifiers.traits.MomentumModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OvercastModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OvergrowthModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OverlordModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SearingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SturdyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.TastyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.TemperateModifier;
import slimeknights.tconstruct.tools.modifiers.traits.TypeDamageModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.BaneOfArthropodsModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.BeheadingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.CoolingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.DiamondModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.EmeraldModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ExpanderModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.FieryModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.HasteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.KnockbackModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.MagneticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.NetheriteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ScaledTypeDamageModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SharpnessModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SoulboundModifier;
import slimeknights.tconstruct.tools.recipe.PlayerBeheadingRecipe;

import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", getGenericGemBlock(), GENERAL_TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> ichorExpander = ITEMS.register("ichor_expander", TOOLTIP_ITEM);
  public static final ItemObject<Item> enderExpander = ITEMS.register("ender_expander", TOOLTIP_ITEM);
  public static final ItemObject<Item> reinforcement = ITEMS.register("reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", GENERAL_PROPS);
  public static final ItemObject<Item> silkyJewel = ITEMS.register("silky_jewel", GENERAL_PROPS);
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", GENERAL_PROPS);
  public static final ItemObject<Item> creativeUpgradeItem = ITEMS.register("creative_upgrade", TOOLTIP_ITEM);
  public static final ItemObject<Item> creativeAbilityItem = ITEMS.register("creative_ability", TOOLTIP_ITEM);
  public static final ItemEnumObject<SlimeType, Item> slimeCrystal = ITEMS.registerEnum(SlimeType.TRUE_SLIME, "slime_crystal", (type) -> new Item(GENERAL_PROPS));

  /*
   * Modifiers
   */

  // durability
  public static final ReinforcedModifier reinforced = Registry.register(TinkerRegistries.MODIFIERS, id("reinforced"), new ReinforcedModifier());
  public static final EmeraldModifier emerald = Registry.register(TinkerRegistries.MODIFIERS, id("emerald"), new EmeraldModifier());
  public static final DiamondModifier diamond = Registry.register(TinkerRegistries.MODIFIERS, id("diamond"), new DiamondModifier());
  public static final WorldboundModifier worldbound = Registry.register(TinkerRegistries.MODIFIERS, id("worldbound"),  new WorldboundModifier(0x7E6059));
  public static final SoulboundModifier soulbound = Registry.register(TinkerRegistries.MODIFIERS, id("soulbound"), new SoulboundModifier());
  public static final NetheriteModifier netherite = Registry.register(TinkerRegistries.MODIFIERS, id("netherite"), new NetheriteModifier());
  public static final OverslimeModifier overslime = Registry.register(TinkerRegistries.MODIFIERS, id("overslime"), new OverslimeModifier());

  // general effects
  public static final ExperiencedModifier experienced = Registry.register(TinkerRegistries.MODIFIERS, id("experienced"), new ExperiencedModifier());
  public static final MagneticModifier magnetic = Registry.register(TinkerRegistries.MODIFIERS, id("magnetic"), new MagneticModifier());
  public static final HasteModifier haste = Registry.register(TinkerRegistries.MODIFIERS, id("haste"), new HasteModifier());

  // weapon
  public static final KnockbackModifier knockback = Registry.register(TinkerRegistries.MODIFIERS, id("knockback"), new KnockbackModifier());
  public static final FieryModifier fiery = Registry.register(TinkerRegistries.MODIFIERS, id("fiery"), new FieryModifier());
  public static final NecroticModifier necrotic = Registry.register(TinkerRegistries.MODIFIERS, id("necrotic"), new NecroticModifier());
  public static final BeheadingModifier beheading = Registry.register(TinkerRegistries.MODIFIERS, id("beheading"), new BeheadingModifier());

  // damage boost
  public static final ScaledTypeDamageModifier smite = Registry.register(TinkerRegistries.MODIFIERS, id("smite"), new ScaledTypeDamageModifier(0xCC9720, EntityGroup.UNDEAD));
  public static final BaneOfArthropodsModifier baneOfArthropods = Registry.register(TinkerRegistries.MODIFIERS, id("bane_of_arthropods"), new BaneOfArthropodsModifier());
  public static final ScaledTypeDamageModifier antiaquatic = Registry.register(TinkerRegistries.MODIFIERS, id("antiaquatic"), new ScaledTypeDamageModifier(0xD58520, EntityGroup.AQUATIC));
  public static final CoolingModifier cooling = Registry.register(TinkerRegistries.MODIFIERS, id("cooling"), new CoolingModifier());
  public static final SharpnessModifier sharpness = Registry.register(TinkerRegistries.MODIFIERS, id("sharpness"), new SharpnessModifier());

  // abilities
  public static final LuckModifier luck = Registry.register(TinkerRegistries.MODIFIERS, id("luck"), new LuckModifier());
  public static final SilkyModifier silky = Registry.register(TinkerRegistries.MODIFIERS, id("silky"), new SilkyModifier());
  public static final AutosmeltModifier autosmelt = Registry.register(TinkerRegistries.MODIFIERS, id("autosmelt"), new AutosmeltModifier());
  public static final ExpanderModifier expanded = Registry.register(TinkerRegistries.MODIFIERS, id("expanded"), new ExpanderModifier());

  // bonus modifier slots
  public static final ExtraModifier writable = Registry.register(TinkerRegistries.MODIFIERS, id("writable"), new ExtraModifier(0xffffff));
  public static final ExtraModifier recapitated = Registry.register(TinkerRegistries.MODIFIERS, id("recapitated"), new ExtraModifier(0x67d755));
  public static final ExtraModifier harmonious = Registry.register(TinkerRegistries.MODIFIERS, id("harmonious"), new ExtraModifier(0xffd800));
  public static final ExtraModifier resurrected = Registry.register(TinkerRegistries.MODIFIERS, id("resurrected"), new ExtraModifier(0xbe95d4));
  public static final ExtraModifier gilded = Registry.register(TinkerRegistries.MODIFIERS, id("gilded"), new ExtraModifier(0xeccb45, ExtraType.UPGRADE, ModifierSource.MULTI_USE, 2));
  public static final ExtraModifier draconic = Registry.register(TinkerRegistries.MODIFIERS, id("draconic"), new ExtraModifier(0x707070, ExtraType.ABILITY, ModifierSource.SINGLE_USE));
  // creative
  public static final ExtraModifier creativeUpgrade = Registry.register(TinkerRegistries.MODIFIERS, id("creative_upgrade"), new ExtraModifier(0xCCBA47, ExtraType.UPGRADE, ModifierSource.MULTI_USE));
  public static final ExtraModifier creativeAbility = Registry.register(TinkerRegistries.MODIFIERS, id("creative_ability"), new ExtraModifier(0xB8A0FF, ExtraType.ABILITY, ModifierSource.MULTI_USE));

  // traits - tier 1
  public static final CultivatedModifier cultivated = Registry.register(TinkerRegistries.MODIFIERS, id("cultivated"), new CultivatedModifier());
  public static final DamageSpeedTradeModifier jagged = Registry.register(TinkerRegistries.MODIFIERS, id("jagged"), new DamageSpeedTradeModifier(0x696969, 0.01f));
  public static final DamageSpeedTradeModifier stonebound = Registry.register(TinkerRegistries.MODIFIERS, id("stonebound"), new DamageSpeedTradeModifier(0x999999, -0.01f));
  public static final LevelDamageModifier fractured = Registry.register(TinkerRegistries.MODIFIERS, id("fractured"), new LevelDamageModifier(0xede6bf, 0.5f));
  // traits - tier 2
  // reinforced is also an upgrade
  public static final SearingModifier searing = Registry.register(TinkerRegistries.MODIFIERS, id("searing"), new SearingModifier());
  public static final DwarvenModifier dwarven = Registry.register(TinkerRegistries.MODIFIERS, id("dwarven"), new DwarvenModifier());
  public static final OvergrowthModifier overgrowth = Registry.register(TinkerRegistries.MODIFIERS, id("overgrowth"), new OvergrowthModifier());
  // traits - tier 3
  public static final OvercastModifier overcast = Registry.register(TinkerRegistries.MODIFIERS, id("overcast"), new OvercastModifier());
  public static final LaceratingModifier lacerating = Registry.register(TinkerRegistries.MODIFIERS, id("lacerating"), new LaceratingModifier());
  public static final MaintainedModifier wellMaintained = Registry.register(TinkerRegistries.MODIFIERS, id("maintained"), new MaintainedModifier());
  public static final ExtraModifier enhanced = Registry.register(TinkerRegistries.MODIFIERS, id("enhanced"), new ExtraModifier(0xffdbcc, ExtraType.UPGRADE, ModifierSource.TRAIT));
  public static final TastyModifier tasty = Registry.register(TinkerRegistries.MODIFIERS, id("tasty"), new TastyModifier());

  public static final LightweightModifier lightweight = Registry.register(TinkerRegistries.MODIFIERS, id("lightweight"), new LightweightModifier());
  // traits - tier 4
  public static final OverlordModifier overlord = Registry.register(TinkerRegistries.MODIFIERS, id("overlord"), new OverlordModifier());
  public static final MomentumModifier momentum = Registry.register(TinkerRegistries.MODIFIERS, id("momentum"), new MomentumModifier());
  public static final InsatibleModifier insatiable = Registry.register(TinkerRegistries.MODIFIERS, id("insatiable"), new InsatibleModifier());

  // traits - mod compat tier 2
  public static final HeavyModifier heavy = Registry.register(TinkerRegistries.MODIFIERS, id("heavy"), new HeavyModifier());
  public static final TypeDamageModifier holy = Registry.register(TinkerRegistries.MODIFIERS, id("holy"),  new TypeDamageModifier(0xd1ecf6, EntityGroup.UNDEAD));
  // experienced is also an upgrade
  // traits - mod compat tier 3
  public static final SturdyModifier sturdy = Registry.register(TinkerRegistries.MODIFIERS, id("sturdy"), new SturdyModifier());
  public static final MaintainedModifier2 wellMaintained2 = Registry.register(TinkerRegistries.MODIFIERS, id("maintained_2"), new MaintainedModifier2());
  public static final TemperateModifier temperate = Registry.register(TinkerRegistries.MODIFIERS, id("temperate"), new TemperateModifier());

  /*
   * Internal effects
   */
  private static final IntFunction<Supplier<TinkerEffect>> MARKER_EFFECT = color -> () -> new TinkerEffect(StatusEffectType.BENEFICIAL, color, false);
  public static BleedingEffect bleeding = Registry.register(Registry.STATUS_EFFECT, id("bleeding"), new BleedingEffect());
  public static MagneticEffect magneticEffect = Registry.register(Registry.STATUS_EFFECT, id("magnetic"), new MagneticEffect());
  public static TinkerEffect momentumEffect = Registry.register(Registry.STATUS_EFFECT, id("momentum"), MARKER_EFFECT.apply(0x60496b).get());
  public static TinkerEffect insatiableEffect = Registry.register(Registry.STATUS_EFFECT, id("insatiable"), MARKER_EFFECT.apply(0x9261cc).get());

  /*
   * Recipes
   */
  public static final ModifierRecipe.Serializer modifierSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("modifier"), new ModifierRecipe.Serializer());
  public static final IncrementalModifierRecipe.Serializer incrementalModifierSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("incremental_modifier"), new IncrementalModifierRecipe.Serializer());
  public static final OverslimeModifierRecipe.Serializer overslimeSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("overslime_modifier"), new OverslimeModifierRecipe.Serializer());
  public static final BeheadingRecipe.Serializer beheadingSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("beheading"), new BeheadingRecipe.Serializer());
  public static final SpecialRecipeSerializer<PlayerBeheadingRecipe> playerBeheadingSerializer = Registry.register(Registry.RECIPE_SERIALIZER, id("player_beheading"),  new SpecialRecipeSerializer<>(PlayerBeheadingRecipe::new));

  @Override
  public void onInitialize() {

  }
}
