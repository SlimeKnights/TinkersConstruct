package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.Item;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.effect.TinkerEffect;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
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
  //protected static final Supplier<IForgeRegistry<Modifier>> MODIFIER_REGISTRY = MODIFIERS.makeRegistry("modifiers", () -> new RegistryBuilder<Modifier>().setType(Modifier.class).setDefaultKey(Util.getResource("empty")));

  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", GENERIC_GEM_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);

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
  public static final EnumObject<SlimeType, Item> slimeCrystal = ITEMS.registerEnum(SlimeType.TRUE_SLIME, "slime_crystal", (type) -> new Item(GENERAL_PROPS));

  /*
   * Modifiers
   */
  public static final EmptyModifier empty = MODIFIERS.register("empty", EmptyModifier::new);

  // durability
  public static final ReinforcedModifier reinforced = MODIFIERS.register("reinforced", ReinforcedModifier::new);
  public static final EmeraldModifier emerald = MODIFIERS.register("emerald", EmeraldModifier::new);
  public static final DiamondModifier diamond = MODIFIERS.register("diamond", DiamondModifier::new);
  public static final WorldboundModifier worldbound = MODIFIERS.register("worldbound", () - new WorldboundModifier(0x7E6059));
  public static final SoulboundModifier soulbound = MODIFIERS.register("soulbound", SoulboundModifier::new);
  public static final NetheriteModifier netherite = MODIFIERS.register("netherite", NetheriteModifier::new);
  public static final OverslimeModifier overslime = MODIFIERS.register("overslime", OverslimeModifier::new);

  // general effects
  public static final ExperiencedModifier experienced = MODIFIERS.register("experienced", ExperiencedModifier::new);
  public static final MagneticModifier magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final HasteModifier haste = MODIFIERS.register("haste", HasteModifier::new);

  // weapon
  public static final KnockbackModifier knockback = MODIFIERS.register("knockback", KnockbackModifier::new);
  public static final FieryModifier fiery = MODIFIERS.register("fiery", FieryModifier::new);
  public static final NecroticModifier necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  public static final BeheadingModifier beheading = MODIFIERS.register("beheading", BeheadingModifier::new);

  // damage boost
  public static final ScaledTypeDamageModifier smite = MODIFIERS.register("smite", () - new ScaledTypeDamageModifier(0xCC9720, EntityGroup.UNDEAD));
  public static final BaneOfArthropodsModifier baneOfArthropods = MODIFIERS.register("bane_of_arthropods", BaneOfArthropodsModifier::new);
  public static final ScaledTypeDamageModifier antiaquatic = MODIFIERS.register("antiaquatic",  new ScaledTypeDamageModifier(0xD58520, EntityGroup.AQUATIC));
  public static final CoolingModifier cooling = MODIFIERS.register("cooling", CoolingModifier::new);
  public static final SharpnessModifier sharpness = MODIFIERS.register("sharpness", SharpnessModifier::new);

  // abilities
  public static final LuckModifier luck = MODIFIERS.register("luck", LuckModifier::new);
  public static final SilkyModifier silky = MODIFIERS.register("silky", SilkyModifier::new);
  public static final AutosmeltModifier autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final ExpanderModifier expanded = MODIFIERS.register("expanded", ExpanderModifier::new);

  // bonus modifier slots
  public static final ExtraModifier writable = MODIFIERS.register("writable",  new ExtraModifier(0xffffff));
  public static final ExtraModifier recapitated = MODIFIERS.register("recapitated",  new ExtraModifier(0x67d755));
  public static final ExtraModifier harmonious = MODIFIERS.register("harmonious",  new ExtraModifier(0xffd800));
  public static final ExtraModifier resurrected = MODIFIERS.register("resurrected",  new ExtraModifier(0xbe95d4));
  public static final ExtraModifier gilded = MODIFIERS.register("gilded",  new ExtraModifier(0xeccb45, ExtraType.UPGRADE, ModifierSource.MULTI_USE, 2));
  public static final ExtraModifier draconic = MODIFIERS.register("draconic",  new ExtraModifier(0x707070, ExtraType.ABILITY, ModifierSource.SINGLE_USE));
  // creative
  public static final ExtraModifier creativeUpgrade = MODIFIERS.register("creative_upgrade",  new ExtraModifier(0xCCBA47, ExtraType.UPGRADE, ModifierSource.MULTI_USE));
  public static final ExtraModifier creativeAbility = MODIFIERS.register("creative_ability",  new ExtraModifier(0xB8A0FF, ExtraType.ABILITY, ModifierSource.MULTI_USE));

  // traits - tier 1
  public static final CultivatedModifier cultivated = MODIFIERS.register("cultivated", CultivatedModifier::new);
  public static final DamageSpeedTradeModifier jagged = MODIFIERS.register("jagged",  new DamageSpeedTradeModifier(0x696969, 0.01f));
  public static final DamageSpeedTradeModifier stonebound = MODIFIERS.register("stonebound",  new DamageSpeedTradeModifier(0x999999, -0.01f));
  public static final LevelDamageModifier fractured = MODIFIERS.register("fractured",  new LevelDamageModifier(0xede6bf, 0.5f));
  // traits - tier 2
  // reinforced is also an upgrade
  public static final SearingModifier searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final DwarvenModifier dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  public static final OvergrowthModifier overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  // traits - tier 3
  public static final OvercastModifier overcast = MODIFIERS.register("overcast", OvercastModifier::new);
  public static final LaceratingModifier lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final MaintainedModifier wellMaintained = MODIFIERS.register("maintained", MaintainedModifier::new);
  public static final ExtraModifier enhanced = MODIFIERS.register("enhanced",  new ExtraModifier(0xffdbcc, ExtraType.UPGRADE, ModifierSource.TRAIT));
  public static final TastyModifier tasty = MODIFIERS.register("tasty", TastyModifier::new);

  public static final LightweightModifier lightweight = MODIFIERS.register("lightweight", LightweightModifier::new);
  // traits - tier 4
  public static final OverlordModifier overlord = MODIFIERS.register("overlord", OverlordModifier::new);
  public static final MomentumModifier momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final InsatibleModifier insatiable = MODIFIERS.register("insatiable", InsatibleModifier::new);

  // traits - mod compat tier 2
  public static final HeavyModifier heavy = MODIFIERS.register("heavy", HeavyModifier::new);
  public static final TypeDamageModifier holy = MODIFIERS.register("holy",  new TypeDamageModifier(0xd1ecf6, EntityGroup.UNDEAD));
  // experienced is also an upgrade
  // traits - mod compat tier 3
  public static final SturdyModifier sturdy = MODIFIERS.register("sturdy", SturdyModifier::new);
  public static final MaintainedModifier2 wellMaintained2 = MODIFIERS.register("maintained_2", MaintainedModifier2::new);
  public static final TemperateModifier temperate = MODIFIERS.register("temperate", TemperateModifier::new);

  /*
   * Internal effects
   */
  private static final IntFunction<Supplier<TinkerEffect>> MARKER_EFFECT = color ->  new TinkerEffect(StatusEffectType.BENEFICIAL, color, false);
  public static BleedingEffect bleeding = POTIONS.register("bleeding", BleedingEffect::new);
  public static MagneticEffect magneticEffect = POTIONS.register("magnetic", MagneticEffect::new);
  public static TinkerEffect momentumEffect = POTIONS.register("momentum", MARKER_EFFECT.apply(0x60496b));
  public static TinkerEffect insatiableEffect = POTIONS.register("insatiable", MARKER_EFFECT.apply(0x9261cc));

  /*
   * Recipes
   */
  public static final ModifierRecipe.Serializer modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final IncrementalModifierRecipe.Serializer incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", IncrementalModifierRecipe.Serializer::new);
  public static final OverslimeModifierRecipe.Serializer overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
  public static final BeheadingRecipe.Serializer beheadingSerializer = RECIPE_SERIALIZERS.register("beheading", BeheadingRecipe.Serializer::new);
  public static final SpecialRecipeSerializer<PlayerBeheadingRecipe> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading",  new SpecialRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final ModifierLootModifier.Serializer modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", ModifierLootModifier.Serializer::new);
}
