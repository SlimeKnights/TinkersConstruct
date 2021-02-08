package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.shared.block.ConsecratedSoilBlock;
import slimeknights.tconstruct.shared.block.GraveyardSoilBlock;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;
import slimeknights.tconstruct.tools.modifiers.ExtraModifier;
import slimeknights.tconstruct.tools.modifiers.LevelDamageModifier;
import slimeknights.tconstruct.tools.modifiers.ReinforcedModifier;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.free.WorldboundModifier;
import slimeknights.tconstruct.tools.modifiers.traits.BonusDamageModifier;
import slimeknights.tconstruct.tools.modifiers.traits.CultivatedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DwarfishModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ExperiencedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.HeavyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.LightweightModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier2;
import slimeknights.tconstruct.tools.modifiers.traits.OvercastModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OvergrowthModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OverlordModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SearingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SturdyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.DiamondModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.EmeraldModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.NetheriteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SilkyModifier;

import java.util.function.Supplier;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  protected static final Supplier<IForgeRegistry<Modifier>> MODIFIER_REGISTRY = MODIFIERS.makeRegistry("modifiers", () -> new RegistryBuilder<Modifier>().setType(Modifier.class).setDefaultKey(Util.getResource("empty")));

  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", GENERIC_GEM_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM);
  // soil
  public static final ItemObject<GraveyardSoilBlock> graveyardSoil = BLOCKS.register("graveyard_soil", () -> new GraveyardSoilBlock(GENERIC_SAND_BLOCK), GENERAL_TOOLTIP_BLOCK_ITEM);
  public static final ItemObject<ConsecratedSoilBlock> consecratedSoil = BLOCKS.register("consecrated_soil", () -> new ConsecratedSoilBlock(GENERIC_SAND_BLOCK), GENERAL_TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> widthExpander = ITEMS.register("width_expander", GENERAL_PROPS);
  public static final ItemObject<Item> heightExpander = ITEMS.register("height_expander", GENERAL_PROPS);
  public static final ItemObject<Item> reinforcement = ITEMS.register("reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", GENERAL_PROPS);
  public static final ItemObject<Item> silkyJewel = ITEMS.register("silky_jewel", GENERAL_PROPS);
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", GENERAL_PROPS);
  public static final ItemObject<Item> moss = ITEMS.register("moss", GENERAL_PROPS);
  public static final ItemObject<Item> mendingMoss = ITEMS.register("mending_moss", GENERAL_PROPS);
  public static final ItemObject<Item> creativeModifier = ITEMS.register("creative_modifier", GENERAL_PROPS);
  public static final EnumObject<SlimeType, Item> slimeCrystal = ITEMS.registerEnum(SlimeType.TRUE_SLIME, "slime_crystal", (type) -> new Item(GENERAL_PROPS));

  /*
   * Modifiers
   */
  public static final RegistryObject<EmptyModifier> empty = MODIFIERS.register("empty", EmptyModifier::new);

  // upgrades - tier 2
  public static final RegistryObject<EmeraldModifier> emerald = MODIFIERS.register("emerald", EmeraldModifier::new);

  // upgrades - tier 3
  public static final RegistryObject<DiamondModifier> diamond = MODIFIERS.register("diamond", DiamondModifier::new);
  public static final RegistryObject<SilkyModifier> silky = MODIFIERS.register("silky", SilkyModifier::new);

  // upgrades - tier 4
  public static final RegistryObject<WorldboundModifier> worldbound = MODIFIERS.register("worldbound", () -> new WorldboundModifier(0x7E6059));
  public static final RegistryObject<NetheriteModifier> netherite = MODIFIERS.register("netherite", NetheriteModifier::new);

  // slotless
  public static final RegistryObject<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);
  public static final RegistryObject<ExtraModifier> writable = MODIFIERS.register("writable", () -> new ExtraModifier(0xffffff, true));
  public static final RegistryObject<ExtraModifier> recapitated = MODIFIERS.register("recapitated", () -> new ExtraModifier(0xa6dc9d, true));
  public static final RegistryObject<ExtraModifier> harmonious = MODIFIERS.register("harmonious", () -> new ExtraModifier(0xffd800, true));

  // traits - tier 1
  public static final RegistryObject<CultivatedModifier> cultivated = MODIFIERS.register("cultivated", CultivatedModifier::new);
  public static final RegistryObject<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0x696969, 0.01f));
  public static final RegistryObject<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(0x999999, -0.01f));
  public static final RegistryObject<LevelDamageModifier> fractured = MODIFIERS.register("fractured", () -> new LevelDamageModifier(0xede6bf, 1.5f));
  // traits - tier 2
  public static final RegistryObject<ReinforcedModifier> reinforced = MODIFIERS.register("reinforced", ReinforcedModifier::new);
  public static final RegistryObject<SearingModifier> searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final RegistryObject<DwarfishModifier> dwarfish = MODIFIERS.register("dwarfish", DwarfishModifier::new);
  public static final RegistryObject<OvergrowthModifier> overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  // traits - tier 3
  public static final RegistryObject<OvercastModifier> overcast = MODIFIERS.register("overcast", OvercastModifier::new);
  public static final RegistryObject<MaintainedModifier> wellMaintained = MODIFIERS.register("maintained", MaintainedModifier::new);
  public static final RegistryObject<ExtraModifier> enhanced = MODIFIERS.register("enhanced", () -> new ExtraModifier(0xffdbcc, false));

  public static final RegistryObject<LightweightModifier> lightweight = MODIFIERS.register("lightweight", LightweightModifier::new);
  // traits - tier 4
  public static final RegistryObject<OverlordModifier> overlord = MODIFIERS.register("overlord", OverlordModifier::new);

  // traits - mod compat tier 2
  public static final RegistryObject<HeavyModifier> heavy = MODIFIERS.register("heavy", HeavyModifier::new);
  public static final RegistryObject<BonusDamageModifier> smite = MODIFIERS.register("smite", () -> new BonusDamageModifier(0xd1ecf6, CreatureAttribute.UNDEAD));
  public static final RegistryObject<ExperiencedModifier> experienced = MODIFIERS.register("experienced", ExperiencedModifier::new);
  // traits - mod compat tier 3
  public static final RegistryObject<SturdyModifier> sturdy = MODIFIERS.register("sturdy", SturdyModifier::new);
  public static final RegistryObject<MaintainedModifier2> wellMaintained2 = MODIFIERS.register("maintained_2", MaintainedModifier2::new);

  /*
   * Recipes
   */
  public static final RegistryObject<ModifierRecipe.Serializer> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final RegistryObject<OverslimeModifierRecipe.Serializer> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
}
