package slimeknights.tconstruct.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.fluid.InvertedFluid;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.fluid.texture.FluidTextureCameraProvider;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.data.FluidBlockstateModelProvider;
import slimeknights.tconstruct.fluids.data.FluidBucketModelProvider;
import slimeknights.tconstruct.fluids.data.FluidTextureProvider;
import slimeknights.tconstruct.fluids.data.FluidTooltipProvider;
import slimeknights.tconstruct.fluids.fluids.PotionFluidType;
import slimeknights.tconstruct.fluids.fluids.SlimeFluid;
import slimeknights.tconstruct.fluids.item.BottleItem;
import slimeknights.tconstruct.fluids.item.ContainerFoodItem;
import slimeknights.tconstruct.fluids.item.ContainerFoodItem.FluidContainerFoodItem;
import slimeknights.tconstruct.fluids.item.MagmaBottleItem;
import slimeknights.tconstruct.fluids.item.PotionBucketItem;
import slimeknights.tconstruct.fluids.util.BottleBrewingRecipe;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoEmpty;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoWater;
import slimeknights.tconstruct.fluids.util.FillBottle;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.tools.network.FluidDataSerializer;
import slimeknights.tconstruct.world.TinkerWorld;

import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.fluids.block.BurningLiquidBlock.createBurning;
import static slimeknights.tconstruct.fluids.block.MobEffectLiquidBlock.createEffect;

/**
 * Contains all fluids used throughout the mod
 */
@SuppressWarnings("unused")
public final class TinkerFluids extends TinkerModule {
  public TinkerFluids() {
    ForgeMod.enableMilkFluid();
  }

  /** Creative tab for general items, or those that lack another tab */
  public static final RegistryObject<CreativeModeTab> tabFluids = CREATIVE_TABS.register(
    "fluids", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "fluids"))
                                   .icon(() -> new ItemStack(TinkerFluids.moltenIron))
                                   .displayItems(TinkerFluids::addTabItems)
                                   .withTabsBefore(TinkerSmeltery.tabSmeltery.getId())
                                   .withSearchBar()
                                   .build());

  // basic
  public static final FlowingFluidObject<ForgeFlowingFluid> venom = FLUIDS.register("venom").type(slime("venom").temperature(310)).bucket().block(createEffect(MapColor.QUARTZ, 0, () -> new MobEffectInstance(MobEffects.POISON, 5*20))).flowing();
  public static final ItemObject<Item> venomBottle = ITEMS.register("venom_bottle", () -> new FluidContainerFoodItem(new Item.Properties().food(TinkerFood.VENOM_BOTTLE).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE), () -> new FluidStack(venom.get(), FluidValues.BOTTLE)));
  public static final FluidObject<UnplaceableFluid> powderedSnow = FLUIDS.register("powdered_snow").bucket(() -> Items.POWDER_SNOW_BUCKET).type(powder("powdered_snow").temperature(270)).commonTag().unplacable();

  // slime -  note second name parameter is forge tag name
  public static final FlowingFluidObject<SlimeFluid> earthSlime = FLUIDS.registerSlime("earth_slime").type(slime("earth_slime").temperature(350)).bucket().block(createEffect(MapColor.GRASS, 0, () -> new MobEffectInstance(TinkerEffects.bouncy.get(), 5*20))).commonTag("slime").flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> skySlime   = FLUIDS.registerSlime("sky_slime"  ).type(slime("sky_slime"  ).temperature(310)).bucket().block(createEffect(MapColor.DIAMOND, 0, () -> new MobEffectInstance(TinkerEffects.ricochet.get(), 5*20))).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> enderSlime = FLUIDS.registerSlime("ender_slime").type(slime("ender_slime").temperature(370)).bucket().block(createEffect(MapColor.COLOR_PURPLE, 0, () -> new MobEffectInstance(TinkerEffects.enderference.get(), 5 * 20))).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> magma      = FLUIDS.registerSlime("magma").type(slime("magma").temperature(600).lightLevel(3)).bucket().commonTag().block(createBurning(MapColor.NETHER, 3, 8, 3f)).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<InvertedFluid> ichor   = FLUIDS.registerSlime("ichor").invertedType(slime("ichor").temperature(1000).density(-1600)).bucket().block(MapColor.COLOR_ORANGE, 0).invertedFlowing();
  public static final EnumObject<SlimeType, Fluid> slime = new EnumObject.Builder<SlimeType, Fluid>(SlimeType.class).put(SlimeType.EARTH, earthSlime).put(SlimeType.SKY, skySlime).put(SlimeType.ENDER, enderSlime).put(SlimeType.ICHOR, ichor).build();
  // bottles of slime
  public static final EnumObject<SlimeType, Item> slimeBottle = ITEMS.registerEnum(SlimeType.values(), "slime_bottle", type -> new FluidContainerFoodItem(
      new Item.Properties().food(TinkerFood.getBottle(type)).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE), () -> new FluidStack(slime.get(type), FluidValues.BOTTLE)));
  public static final ItemObject<Item> magmaBottle = ITEMS.register("magma_bottle", () -> new MagmaBottleItem(new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE), 15));

  // foods
  public static FlowingFluidObject<ForgeFlowingFluid> honey        = FLUIDS.registerSlime("honey").type(slime("honey").temperature(301)).bucket().block(createEffect(MapColor.COLOR_ORANGE, 0, () -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5*20))).commonTag().flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> beetrootSoup = FLUIDS.register("beetroot_soup").type(cool("beetroot_soup").temperature(400)).bucket().block(MapColor.COLOR_RED, 0).commonTag().flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> mushroomStew = FLUIDS.register("mushroom_stew").type(cool("mushroom_stew").temperature(400)).bucket().block(MapColor.DIRT, 0).commonTag().flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> rabbitStew   = FLUIDS.register("rabbit_stew").type(cool("rabbit_stew").temperature(400)).bucket().block(MapColor.PODZOL, 0).commonTag().flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> meatSoup     = FLUIDS.register("meat_soup").type(cool("meat_soup").temperature(400)).bucket().block(MapColor.CRIMSON_NYLIUM, 0).flowing();
  public static final ItemObject<Item> meatSoupBowl = ITEMS.register("meat_soup", () -> new ContainerFoodItem(new Item.Properties().food(TinkerFood.MEAT_SOUP).stacksTo(1).craftRemainder(Items.BOWL)));

  // potion
  public static final FluidObject<UnplaceableFluid> potion = FLUIDS.register("potion").type(() -> new PotionFluidType(cool().descriptionId("item.minecraft.potion.effect.empty").density(1100).viscosity(1100).temperature(315).sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY))).bucket(fluid -> new PotionBucketItem(fluid, RegistrationHelper.BUCKET_PROPS)).commonTag().unplacable();
  public static final ItemObject<Item> splashBottle = ITEMS.register("splash_bottle", () -> new BottleItem(Items.SPLASH_POTION, ITEM_PROPS));
  public static final ItemObject<Item> lingeringBottle = ITEMS.register("lingering_bottle", () -> new BottleItem(Items.LINGERING_POTION, ITEM_PROPS));

  // base molten fluids
  public static final FlowingFluidObject<ForgeFlowingFluid> searedStone   = FLUIDS.registerStone("seared_stone").type(hot("seared_stone").temperature(900).lightLevel(6)).block(createBurning(MapColor.DEEPSLATE, 6, 8, 2f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> scorchedStone = FLUIDS.registerStone("scorched_stone").type(hot("scorched_stone").temperature(800).lightLevel(4)).block(createBurning(MapColor.TERRACOTTA_BROWN, 4, 7, 2f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenClay    = FLUIDS.registerStone("molten_clay").type(hot("molten_clay").temperature(750).lightLevel(3)).block(createBurning(MapColor.COLOR_ORANGE, 3, 5, 2f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenGlass   = FLUIDS.registerGlass("molten_glass").type(hot("molten_glass").temperature(1050).lightLevel(1)).block(createBurning(MapColor.ICE, 1, 5, 2f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> liquidSoul    = FLUIDS.registerGlass("liquid_soul").type(hot("liquid_soul").temperature(700).lightLevel(2)).block(createEffect(MapColor.COLOR_BROWN, 2, () -> new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20))).bucket().flowing();
  // ceramics compat
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPorcelain = FLUIDS.registerStone("molten_porcelain").type(hot("molten_porcelain").temperature(1000).lightLevel(2)).block(createBurning(MapColor.QUARTZ, 2, 5, 2f)).bucket().flowing();
  // fancy molten fluids
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenObsidian = FLUIDS.registerStone("molten_obsidian").type(hot("molten_obsidian").temperature(1300).lightLevel(3)).block(createBurning(MapColor.COLOR_BLACK, 3, 12, 4f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEnder    = FLUIDS.registerStone("molten_ender").type(hot("molten_ender").temperature(777).lightLevel(5)).block(createEffect(MapColor.PLANT, 5, () -> new MobEffectInstance(TinkerEffects.enderference.get(), 5 * 20))).bucket().commonTag("ender").flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> blazingBlood   = FLUIDS.register("blazing_blood").type(hot("blazing_blood").temperature(1800).lightLevel(15).density(3500)).block(createBurning(MapColor.COLOR_ORANGE, 15, 15, 5f)).bucket().flowing();

  // ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEmerald  = FLUIDS.registerGem("molten_emerald").type(hot("molten_emerald").temperature(1234).lightLevel(9)).block(createBurning(MapColor.EMERALD, 9, 10, 6f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenQuartz   = FLUIDS.registerGem("molten_quartz").type(hot("molten_quartz").temperature(937).lightLevel(6)).block(createBurning(MapColor.QUARTZ, 6, 10, 5f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAmethyst = FLUIDS.registerGem("molten_amethyst").type(hot("molten_amethyst").temperature(1250).lightLevel(11)).block(createBurning(MapColor.COLOR_PURPLE, 11, 10, 5f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenDiamond  = FLUIDS.registerGem("molten_diamond").type(hot("molten_diamond").temperature(1750).lightLevel(13)).block(createBurning(MapColor.DIAMOND, 13, 10, 7f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenDebris   = FLUIDS.registerGem("molten_debris").type(hot("molten_debris").temperature(1475).lightLevel(14)).block(createBurning(MapColor.COLOR_BLACK, 14, 10, 8f)).bucket().flowing();
  // metal ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenIron   = FLUIDS.registerMetal("molten_iron").type(hot("molten_iron").temperature(1100).lightLevel(12)).block(createBurning(MapColor.RAW_IRON, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenGold   = FLUIDS.registerMetal("molten_gold").type(hot("molten_gold").temperature(1000).lightLevel(12)).block(createBurning(MapColor.GOLD, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenCopper = FLUIDS.registerMetal("molten_copper").type(hot("molten_copper").temperature(800).lightLevel(12)).block(createBurning(MapColor.COLOR_ORANGE, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenCobalt = FLUIDS.registerMetal("molten_cobalt").type(hot("molten_cobalt").temperature(1250).lightLevel(8)).block(createBurning(MapColor.WATER, 8, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSteel  = FLUIDS.registerMetal("molten_steel").type(hot("molten_steel").temperature(1250).lightLevel(13)).block(createBurning(MapColor.STONE, 13, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenKnightmetal = FLUIDS.registerMetal("molten_knightmetal").type(hot("molten_knightmetal").temperature(1600).lightLevel(10)).block(createBurning(MapColor.GRASS, 10, 10, 8f)).bucket().flowing();
  // alloys
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSlimesteel     = FLUIDS.registerMetal("molten_slimesteel").type(hot("molten_slimesteel").temperature(1200).lightLevel(10)).block(createBurning(MapColor.DIAMOND, 10, 10, 6f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAmethystBronze = FLUIDS.registerMetal("molten_amethyst_bronze").type(hot("molten_amethyst_bronze").temperature(1120).lightLevel(12)).block(createBurning(MapColor.COLOR_MAGENTA, 12, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRoseGold       = FLUIDS.registerMetal("molten_rose_gold").type(hot("molten_rose_gold").temperature(850).lightLevel(12)).block(createBurning(MapColor.COLOR_PINK, 12, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPigIron        = FLUIDS.registerMetal("molten_pig_iron").type(hot("molten_pig_iron").temperature(1111).lightLevel(10)).block(createBurning(MapColor.TERRACOTTA_WHITE, 10, 10, 6f)).bucket().flowing();

  public static final FlowingFluidObject<ForgeFlowingFluid> moltenManyullyn   = FLUIDS.registerMetal("molten_manyullyn").type(hot("molten_manyullyn").temperature(1500).lightLevel(11)).block(createBurning(MapColor.COLOR_PURPLE, 11, 10, 8f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenHepatizon   = FLUIDS.registerMetal("molten_hepatizon").type(hot("molten_hepatizon").temperature(1700).lightLevel(8)).block(createBurning(MapColor.TERRACOTTA_BLUE, 8, 10, 7f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<InvertedFluid>     moltenCinderslime = FLUIDS.registerMetal("molten_cinderslime").invertedType(hot("molten_cinderslime").temperature(1350).lightLevel(10).density(-2000)).burningBlock(MapColor.COLOR_RED, 10, 10, 7f).bucket().invertedFlowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenQueensSlime = FLUIDS.registerMetal("molten_queens_slime").type(hot("molten_queens_slime").temperature(1450).lightLevel(9)).block(createBurning(MapColor.COLOR_GREEN, 9, 10, 6f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSoulsteel   = FLUIDS.registerMetal("molten_soulsteel").type(hot("molten_soulsteel").temperature(1500).lightLevel(6)).block(createBurning(MapColor.COLOR_BROWN, 6, 10, 7f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenNetherite   = FLUIDS.registerMetal("molten_netherite").type(hot("molten_netherite").temperature(1550).lightLevel(14)).block(createBurning(MapColor.COLOR_BLACK, 14, 10, 10f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenKnightslime = FLUIDS.registerMetal("molten_knightslime").type(hot("molten_knightslime").temperature(1425).lightLevel(12)).block(createBurning(MapColor.COLOR_MAGENTA, 12, 10, 8f)).bucket().flowing();

  // compat ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenTin      = FLUIDS.registerMetal("molten_tin").type(hot("molten_tin").temperature(525).lightLevel(12)).block(createBurning(MapColor.COLOR_CYAN, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAluminum = FLUIDS.registerMetal("molten_aluminum").type(hot("molten_aluminum").temperature(725).lightLevel(12)).block(createBurning(MapColor.METAL, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenLead     = FLUIDS.registerMetal("molten_lead").type(hot("molten_lead").temperature(630).lightLevel(12)).block(createBurning(MapColor.TERRACOTTA_BLUE, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSilver   = FLUIDS.registerMetal("molten_silver").type(hot("molten_silver").temperature(1090).lightLevel(12)).block(createBurning(MapColor.METAL, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenNickel   = FLUIDS.registerMetal("molten_nickel").type(hot("molten_nickel").temperature(1250).lightLevel(12)).block(createBurning(MapColor.WOOD, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenZinc     = FLUIDS.registerMetal("molten_zinc").type(hot("molten_zinc").temperature(720).lightLevel(12)).block(createBurning(MapColor.TERRACOTTA_CYAN, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPlatinum = FLUIDS.registerMetal("molten_platinum").type(hot("molten_platinum").temperature(1270).lightLevel(12)).block(createBurning(MapColor.DIAMOND, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenTungsten = FLUIDS.registerMetal("molten_tungsten").type(hot("molten_tungsten").temperature(1250).lightLevel(12)).block(createBurning(MapColor.TERRACOTTA_BLACK, 12, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenOsmium   = FLUIDS.registerMetal("molten_osmium").type(hot("molten_osmium").temperature(1275).lightLevel(4)).block(createBurning(MapColor.CLAY, 4, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenUranium  = FLUIDS.registerMetal("molten_uranium").type(hot("molten_uranium").temperature(1130).lightLevel(15)).block(createBurning(MapColor.TERRACOTTA_GREEN, 15, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenChromium = FLUIDS.registerMetal("molten_chromium").type(hot("molten_chromium").temperature(1200).lightLevel(13)).block(createBurning(MapColor.COLOR_CYAN, 13, 10, 5f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenCadmium  = FLUIDS.registerMetal("molten_cadmium").type(hot("molten_cadmium").temperature(594).lightLevel(10)).block(createBurning(MapColor.COLOR_BROWN, 10, 10, 5f)).bucket().commonTag().flowing();

  // compat alloys
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenBronze     = FLUIDS.registerMetal("molten_bronze").type(hot("molten_bronze").temperature(1000).lightLevel(10)).block(createBurning(MapColor.TERRACOTTA_ORANGE, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenBrass      = FLUIDS.registerMetal("molten_brass").type(hot("molten_brass").temperature(905).lightLevel(10)).block(createBurning(MapColor.TERRACOTTA_YELLOW, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenElectrum   = FLUIDS.registerMetal("molten_electrum").type(hot("molten_electrum").temperature(1060).lightLevel(10)).block(createBurning(MapColor.GOLD, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenInvar      = FLUIDS.registerMetal("molten_invar").type(hot("molten_invar").temperature(1200).lightLevel(10)).block(createBurning(MapColor.GLOW_LICHEN, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenConstantan = FLUIDS.registerMetal("molten_constantan").type(hot("molten_constantan").temperature(1220).lightLevel(10)).block(createBurning(MapColor.TERRACOTTA_RED, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPewter     = FLUIDS.registerMetal("molten_pewter").type(hot("molten_pewter").temperature(700).lightLevel(10)).block(createBurning(MapColor.COLOR_GRAY, 10, 10, 6f)).bucket().commonTag().flowing();

  // mod-specific compat
  // thermal
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEnderium = FLUIDS.registerMetal("molten_enderium").type(hot("molten_enderium").temperature(1650).lightLevel(12)).block(createBurning(MapColor.COLOR_CYAN, 12, 10, 7f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenLumium   = FLUIDS.registerMetal("molten_lumium").type(hot("molten_lumium").temperature(1350).lightLevel(15)).block(createBurning(MapColor.GOLD, 15, 10, 7f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSignalum = FLUIDS.registerMetal("molten_signalum").type(hot("molten_signalum").temperature(1299).lightLevel(13)).block(createBurning(MapColor.FIRE, 13, 10, 7f)).bucket().commonTag().flowing();
  // mekanism
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRefinedGlowstone = FLUIDS.registerMetal("molten_refined_glowstone").type(hot("molten_refined_glowstone").temperature(1125).lightLevel(15)).block(createBurning(MapColor.COLOR_YELLOW, 15, 10, 7f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRefinedObsidian  = FLUIDS.registerMetal("molten_refined_obsidian").type(hot("molten_refined_obsidian").temperature(1775).lightLevel(7)).block(createBurning(MapColor.TERRACOTTA_BLUE, 7, 10, 7f)).bucket().commonTag().flowing();
  // cosmere metals
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenNicrosil = FLUIDS.registerMetal("molten_nicrosil").type(hot("molten_nicrosil").temperature(1400).lightLevel(14)).block(createBurning(MapColor.SNOW, 12, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenDuralumin = FLUIDS.registerMetal("molten_duralumin").type(hot("molten_duralumin").temperature(925).lightLevel(10)).block(createBurning(MapColor.COLOR_LIGHT_GREEN, 10, 10, 6f)).bucket().commonTag().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenBendalloy = FLUIDS.registerMetal("molten_bendalloy").type(hot("molten_bendalloy").temperature(400).lightLevel(9)).block(createBurning(MapColor.SNOW, 9, 10, 6f)).bucket().commonTag().flowing();
  // twilight
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSteeleaf = FLUIDS.registerMetal("molten_steeleaf").type(hot("molten_steeleaf").temperature(1234).lightLevel(10)).block(createBurning(MapColor.COLOR_GREEN, 10, 10, 6f)).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> fieryLiquid = FLUIDS.register("fiery_liquid").type(hot("fiery_liquid").temperature(1800).lightLevel(15)).block(createBurning(MapColor.CRIMSON_HYPHAE, 15, 20, 6f)).tickRate(30).bucket().flowing();

  // fluid data serializer
  public static final FluidDataSerializer FLUID_DATA_SERIALIZER = new FluidDataSerializer();
  public static final RegistryObject<EntityDataSerializer<?>> FLUID_DATA_SERIALIZER_REGISTRY = DATA_SERIALIZERS.register("fluid", () -> FLUID_DATA_SERIALIZER);

  /** Creates a builder for a cool fluid with sounds */
  private static FluidType.Properties cool() {
    return FluidType.Properties.create()
                               .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                               .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                               .motionScale(0.0023333333333333335D)
                               .canExtinguish(true);

  }

  /** Creates a builder for a cool fluid with sounds and description */
  private static FluidType.Properties cool(String name) {
    return cool().descriptionId(TConstruct.makeDescriptionId("fluid", name))
                 .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                 .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
  }

  /** Creates a builder for a cool fluid with sounds and description */
  private static FluidType.Properties slime(String name) {
    return cool(name).density(1600).viscosity(1600);
  }

  /** Creates a builder for a cool fluid with sounds and description */
  @SuppressWarnings("SameParameterValue")
  private static FluidType.Properties powder(String name) {
    return FluidType.Properties.create().descriptionId(TConstruct.makeDescriptionId("fluid", name))
                               .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW)
                               .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
  }

  /** Creates a builder for a hot with sounds and description */
  private static FluidType.Properties hot(String name) {
    return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000)
      .descriptionId(TConstruct.makeDescriptionId("fluid", name))
      .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
      .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
      // from forge lava type
      .motionScale(0.0023333333333333335D)
      .canSwim(false).canDrown(false)
      .pathType(BlockPathTypes.LAVA).adjacentPathType(null);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    boolean client = event.includeClient();
    generator.addProvider(client, new FluidTooltipProvider(packOutput));
    FluidTextureProvider textureProvider = new FluidTextureProvider(packOutput);
    generator.addProvider(client, textureProvider);
    generator.addProvider(client, new FluidTextureCameraProvider(packOutput, event.getExistingFileHelper(), textureProvider));
    generator.addProvider(client, new FluidBucketModelProvider(packOutput, TConstruct.MOD_ID));
    generator.addProvider(client, new FluidBlockstateModelProvider(packOutput, TConstruct.MOD_ID));
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      CauldronInteraction.WATER.put(splashBottle.get(), new FillBottle(Items.SPLASH_POTION));
      CauldronInteraction.WATER.put(lingeringBottle.get(), new FillBottle(Items.LINGERING_POTION));
      CauldronInteraction.WATER.put(Items.SPLASH_POTION,    new EmptyBottleIntoWater(splashBottle,    CauldronInteraction.WATER.get(Items.SPLASH_POTION)));
      CauldronInteraction.WATER.put(Items.LINGERING_POTION, new EmptyBottleIntoWater(lingeringBottle, CauldronInteraction.WATER.get(Items.LINGERING_POTION)));
      CauldronInteraction.EMPTY.put(Items.SPLASH_POTION,    new EmptyBottleIntoEmpty(splashBottle,    CauldronInteraction.EMPTY.get(Items.SPLASH_POTION)));
      CauldronInteraction.EMPTY.put(Items.LINGERING_POTION, new EmptyBottleIntoEmpty(lingeringBottle, CauldronInteraction.EMPTY.get(Items.LINGERING_POTION)));
      // brew bottles into each other, bit weird but feels better than shapeless
      BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Items.POTION, Items.SPLASH_POTION, new ItemStack(splashBottle)));
      BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(TinkerTags.Items.SPLASH_BOTTLE), Items.SPLASH_POTION, Items.LINGERING_POTION, new ItemStack(lingeringBottle)));
    });

    // dispense buckets
    DispenseItemBehavior dispenseBucket = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      @Override
      public ItemStack execute(BlockSource source, ItemStack stack) {
        DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        Level level = source.getLevel();
        if (container.emptyContents(null, level, blockpos, null, stack)) {
          container.checkExtraContent(null, level, stack, blockpos);
          return new ItemStack(Items.BUCKET);
        } else {
          return this.defaultDispenseItemBehavior.dispense(source, stack);
        }
      }
    };
    event.enqueueWork(() -> {
      // slime
      DispenserBlock.registerBehavior(venom, dispenseBucket);
      DispenserBlock.registerBehavior(earthSlime, dispenseBucket);
      DispenserBlock.registerBehavior(skySlime, dispenseBucket);
      DispenserBlock.registerBehavior(enderSlime, dispenseBucket);
      DispenserBlock.registerBehavior(magma, dispenseBucket);
      // foods
      DispenserBlock.registerBehavior(honey, dispenseBucket);
      DispenserBlock.registerBehavior(beetrootSoup, dispenseBucket);
      DispenserBlock.registerBehavior(mushroomStew, dispenseBucket);
      DispenserBlock.registerBehavior(rabbitStew, dispenseBucket);
      DispenserBlock.registerBehavior(meatSoup, dispenseBucket);
      // base molten fluids
      DispenserBlock.registerBehavior(searedStone, dispenseBucket);
      DispenserBlock.registerBehavior(scorchedStone, dispenseBucket);
      DispenserBlock.registerBehavior(moltenClay, dispenseBucket);
      DispenserBlock.registerBehavior(moltenGlass, dispenseBucket);
      DispenserBlock.registerBehavior(liquidSoul, dispenseBucket);
      DispenserBlock.registerBehavior(moltenPorcelain, dispenseBucket);
      DispenserBlock.registerBehavior(moltenObsidian, dispenseBucket);
      DispenserBlock.registerBehavior(moltenEnder, dispenseBucket);
      DispenserBlock.registerBehavior(blazingBlood, dispenseBucket);
      // ores
      DispenserBlock.registerBehavior(moltenEmerald, dispenseBucket);
      DispenserBlock.registerBehavior(moltenQuartz, dispenseBucket);
      DispenserBlock.registerBehavior(moltenAmethyst, dispenseBucket);
      DispenserBlock.registerBehavior(moltenDiamond, dispenseBucket);
      DispenserBlock.registerBehavior(moltenDebris, dispenseBucket);
      // metal ores
      DispenserBlock.registerBehavior(moltenIron, dispenseBucket);
      DispenserBlock.registerBehavior(moltenGold, dispenseBucket);
      DispenserBlock.registerBehavior(moltenCopper, dispenseBucket);
      DispenserBlock.registerBehavior(moltenCobalt, dispenseBucket);
      // alloys
      DispenserBlock.registerBehavior(moltenSlimesteel, dispenseBucket);
      DispenserBlock.registerBehavior(moltenAmethystBronze, dispenseBucket);
      DispenserBlock.registerBehavior(moltenRoseGold, dispenseBucket);
      DispenserBlock.registerBehavior(moltenPigIron, dispenseBucket);
      DispenserBlock.registerBehavior(moltenManyullyn, dispenseBucket);
      DispenserBlock.registerBehavior(moltenHepatizon, dispenseBucket);
      DispenserBlock.registerBehavior(moltenQueensSlime, dispenseBucket);
      DispenserBlock.registerBehavior(moltenSoulsteel, dispenseBucket);
      DispenserBlock.registerBehavior(moltenNetherite, dispenseBucket);
      DispenserBlock.registerBehavior(moltenKnightslime, dispenseBucket);
      // compat ores
      DispenserBlock.registerBehavior(moltenTin, dispenseBucket);
      DispenserBlock.registerBehavior(moltenAluminum, dispenseBucket);
      DispenserBlock.registerBehavior(moltenLead, dispenseBucket);
      DispenserBlock.registerBehavior(moltenSilver, dispenseBucket);
      DispenserBlock.registerBehavior(moltenNickel, dispenseBucket);
      DispenserBlock.registerBehavior(moltenZinc, dispenseBucket);
      DispenserBlock.registerBehavior(moltenPlatinum, dispenseBucket);
      DispenserBlock.registerBehavior(moltenTungsten, dispenseBucket);
      DispenserBlock.registerBehavior(moltenOsmium, dispenseBucket);
      DispenserBlock.registerBehavior(moltenUranium, dispenseBucket);
      DispenserBlock.registerBehavior(moltenChromium, dispenseBucket);
      DispenserBlock.registerBehavior(moltenCadmium, dispenseBucket);
      // compat alloys
      DispenserBlock.registerBehavior(moltenBronze, dispenseBucket);
      DispenserBlock.registerBehavior(moltenBrass, dispenseBucket);
      DispenserBlock.registerBehavior(moltenElectrum, dispenseBucket);
      DispenserBlock.registerBehavior(moltenInvar, dispenseBucket);
      DispenserBlock.registerBehavior(moltenConstantan, dispenseBucket);
      DispenserBlock.registerBehavior(moltenPewter, dispenseBucket);
      DispenserBlock.registerBehavior(moltenSteel, dispenseBucket);
      // mod-specific compat alloys
      DispenserBlock.registerBehavior(moltenEnderium, dispenseBucket);
      DispenserBlock.registerBehavior(moltenLumium, dispenseBucket);
      DispenserBlock.registerBehavior(moltenSignalum, dispenseBucket);
      DispenserBlock.registerBehavior(moltenRefinedGlowstone, dispenseBucket);
      DispenserBlock.registerBehavior(moltenRefinedObsidian, dispenseBucket);
      DispenserBlock.registerBehavior(moltenNicrosil, dispenseBucket);
      DispenserBlock.registerBehavior(moltenDuralumin, dispenseBucket);
      DispenserBlock.registerBehavior(moltenBendalloy, dispenseBucket);
      DispenserBlock.registerBehavior(moltenSteeleaf, dispenseBucket);
      DispenserBlock.registerBehavior(fieryLiquid, dispenseBucket);

      // brew congealed slime into bottles to get slime bottles, easy melting
      for (SlimeType slime : SlimeType.values()) {
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(TinkerWorld.congealedSlime.get(slime)), new ItemStack(TinkerFluids.slimeBottle.get(slime))));
      }
      BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.MAGMA_BLOCK), new ItemStack(TinkerFluids.magmaBottle)));
    });
  }

  /** Adds all relevant items to the creative tab, called by smeltery */
  @SuppressWarnings("deprecation")
  private static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
    // containers
    output.accept(splashBottle);
    output.accept(lingeringBottle);
    // slime
    output.accept(earthSlime);
    output.accept(skySlime);
    output.accept(ichor);
    output.accept(enderSlime);
    accept(output, slimeBottle);
    output.accept(magma);
    output.accept(magmaBottle);
    output.accept(venom);
    output.accept(venomBottle);

    // food
    output.accept(honey);
    output.accept(beetrootSoup);
    output.accept(mushroomStew);
    output.accept(rabbitStew);
    output.accept(meatSoup);
    output.accept(meatSoupBowl);

    // stone
    output.accept(searedStone);
    output.accept(scorchedStone);
    output.accept(moltenClay);
    if (ModList.get().isLoaded("ceramics")) {
      output.accept(moltenPorcelain);
    }
    output.accept(moltenGlass);
    output.accept(moltenObsidian);
    output.accept(liquidSoul);
    output.accept(moltenEnder);
    output.accept(blazingBlood);

    // ores
    output.accept(moltenEmerald);
    output.accept(moltenQuartz);
    output.accept(moltenAmethyst);
    output.accept(moltenDiamond);
    output.accept(moltenDebris);
    // metal ores
    output.accept(moltenCopper);
    output.accept(moltenIron);
    output.accept(moltenGold);
    output.accept(moltenCobalt);
    output.accept(moltenSteel);

    // overworld alloys
    output.accept(moltenSlimesteel);
    output.accept(moltenAmethystBronze);
    output.accept(moltenRoseGold);
    output.accept(moltenPigIron);
    // nether alloys
    output.accept(moltenCinderslime);
    output.accept(moltenQueensSlime);
    output.accept(moltenManyullyn);
    output.accept(moltenHepatizon);
    output.accept(moltenNetherite);
    output.accept(moltenKnightmetal);
    // future: soulsteel
    // future: knightslime

    // compat ores
    acceptMolten(output, moltenTin);
    acceptMolten(output, moltenAluminum);
    acceptMolten(output, moltenLead);
    acceptMolten(output, moltenSilver);
    acceptMolten(output, moltenNickel);
    acceptMolten(output, moltenZinc);
    acceptMolten(output, moltenPlatinum);
    acceptMolten(output, moltenTungsten);
    acceptMolten(output, moltenOsmium);
    acceptMolten(output, moltenUranium);
    acceptMolten(output, moltenChromium);
    acceptMolten(output, moltenCadmium);
    // compat alloys
    acceptMolten(output, moltenBronze, "tin");
    acceptMolten(output, moltenBrass, "zinc");
    acceptMolten(output, moltenElectrum, "silver");
    acceptMolten(output, moltenInvar, "nickel");
    acceptMolten(output, moltenConstantan, "nickel");
    acceptCompat(output, moltenPewter, "pewter", "tin", "lead");
    acceptMolten(output, moltenEnderium);
    acceptMolten(output, moltenLumium);
    acceptMolten(output, moltenSignalum);
    acceptMolten(output, moltenRefinedGlowstone);
    acceptMolten(output, moltenRefinedObsidian);
    acceptMolten(output, moltenNicrosil);
    acceptMolten(output, moltenDuralumin);
    acceptMolten(output, moltenBendalloy);
    acceptMolten(output, moltenSteeleaf);
    acceptCompat(output, fieryLiquid, "fiery");
    BuiltInRegistries.POTION.holders().filter(holder -> {
      Potion potion = holder.get();
      return potion != Potions.EMPTY && potion != Potions.WATER;
    }).forEachOrdered(holder -> {
      output.accept(PotionFluidType.potionBucket(holder.key()));
    });

    // add copper cans, tanks, and lanterns for all the fluids
    CopperCanItem.addFilledVariants(output::accept);
    TankItem.addFilledVariants(output::accept);
  }

  /** Accepts the given item if any of the listed ingots are present */
  private static void acceptCompat(CreativeModeTab.Output output, ItemLike item, String... ingots) {
    for (String ingot : ingots) {
      if (acceptIfTag(output, item, ItemTags.create(commonResource("ingots/" + ingot)))) {
        break;
      }
    }
  }

  /** Accepts the given item if the ingot named after the fluid is present */
  private static void acceptMolten(CreativeModeTab.Output output, FluidObject<?> fluid) {
    acceptCompat(output, fluid, withoutMolten(fluid));
  }

  /** Accepts the given item if the ingot named after the fluid or the passed ingot name is present */
  private static void acceptMolten(CreativeModeTab.Output output, FluidObject<?> fluid, String ingot) {
    acceptCompat(output, fluid, withoutMolten(fluid), ingot);
  }

  /** Length of the molten prefix */
  private static final int MOLTEN_LENGTH = "molten_".length();

  /** Removes the "molten_" prefix from the fluids ID */
  public static String withoutMolten(FluidObject<?> fluid) {
    return fluid.getId().getPath().substring(MOLTEN_LENGTH);
  }
}
