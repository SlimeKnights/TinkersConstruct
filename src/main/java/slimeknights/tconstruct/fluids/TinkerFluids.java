package slimeknights.tconstruct.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.registration.ItemProperties;
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
import slimeknights.tconstruct.fluids.item.PotionBucketItem;
import slimeknights.tconstruct.fluids.util.BottleBrewingRecipe;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoEmpty;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoWater;
import slimeknights.tconstruct.fluids.util.FillBottle;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.TinkerFood;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.network.FluidDataSerializer;
import slimeknights.tconstruct.world.TinkerWorld;

/**
 * Contains all fluids used throughout the mod
 */
public final class TinkerFluids extends TinkerModule {
  public TinkerFluids() {
    ForgeMod.enableMilkFluid();
  }

  // basic
  public static final FlowingFluidObject<ForgeFlowingFluid> venom = FLUIDS.register("venom").type(slime("venom").temperature(310)).bucket().block(Material.WATER).flowing();
  public static final ItemObject<Item> venomBottle = ITEMS.register("venom_bottle", () -> new FluidContainerFoodItem(new Item.Properties().food(TinkerFood.VENOM_BOTTLE).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE), () -> new FluidStack(venom.get(), FluidValues.BOTTLE)));
  public static final FluidObject<UnplaceableFluid> powderedSnow = FLUIDS.register("powdered_snow").bucket(() -> Items.POWDER_SNOW_BUCKET).type(powder("powdered_snow").temperature(270)).unplacable();

  // slime -  note second name parameter is forge tag name
  public static final FlowingFluidObject<SlimeFluid> earthSlime = FLUIDS.register("earth_slime").type(slime("earth_slime").temperature(350)).bucket().block(Material.WATER).tagName("slime").flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> skySlime   = FLUIDS.register("sky_slime"  ).type(slime("sky_slime"  ).temperature(310)).bucket().block(Material.WATER).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> enderSlime = FLUIDS.register("ender_slime").type(slime("ender_slime").temperature(370)).bucket().block(Material.WATER).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final FlowingFluidObject<SlimeFluid> magma      = FLUIDS.register("magma"      ).type(slime("magma"      ).temperature(600).lightLevel(3)).bucket().block(Material.WATER, 3).flowing(SlimeFluid.Source::new, SlimeFluid.Flowing::new);
  public static final EnumObject<SlimeType, SlimeFluid> slime = new EnumObject.Builder<SlimeType, SlimeFluid>(SlimeType.class).put(SlimeType.EARTH, earthSlime).put(SlimeType.SKY, skySlime).put(SlimeType.ENDER, enderSlime).build();
  // bottles of slime
  public static final EnumObject<SlimeType, Item> slimeBottle = ITEMS.registerEnum(SlimeType.values(), "slime_bottle", type -> new FluidContainerFoodItem(
    new Item.Properties().food(TinkerFood.getBottle(type)).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE), () -> new FluidStack(slime.get(type), FluidValues.BOTTLE)));
  public static final ItemObject<Item> magmaBottle = ITEMS.register("magma_bottle", () -> new FluidContainerFoodItem(
    new Item.Properties().food(TinkerFood.MAGMA_BOTTLE).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
    () -> new FluidStack(magma.get(), FluidValues.BOTTLE)));

  // foods
  public static FlowingFluidObject<ForgeFlowingFluid> honey        = FLUIDS.register("honey").type(slime("honey").temperature(301)).bucket().block(Material.WATER).flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> beetrootSoup = FLUIDS.register("beetroot_soup").type(cool("beetroot_soup").temperature(400)).bucket().block(Material.WATER).flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> mushroomStew = FLUIDS.register("mushroom_stew").type(cool("mushroom_stew").temperature(400)).bucket().block(Material.WATER).flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> rabbitStew   = FLUIDS.register("rabbit_stew").type(cool("rabbit_stew").temperature(400)).bucket().block(Material.WATER).flowing();
  public static FlowingFluidObject<ForgeFlowingFluid> meatSoup     = FLUIDS.register("meat_soup").type(cool("meat_soup").temperature(400)).bucket().block(Material.WATER).flowing();
  public static final ItemObject<Item> meatSoupBowl = ITEMS.register("meat_soup", () -> new ContainerFoodItem(new Item.Properties().food(TinkerFood.MEAT_SOUP).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.BOWL)));

  // potion
  public static final FluidObject<UnplaceableFluid> potion = FLUIDS.register("potion").type(() -> new PotionFluidType(cool().descriptionId("item.minecraft.potion.effect.empty").density(1100).viscosity(1100).temperature(315))).bucket(fluid -> new PotionBucketItem(fluid, ItemProperties.BUCKET_PROPS)).unplacable();
  public static final ItemObject<Item> splashBottle = ITEMS.register("splash_bottle", () -> new BottleItem(Items.SPLASH_POTION, GENERAL_PROPS));
  public static final ItemObject<Item> lingeringBottle = ITEMS.register("lingering_bottle", () -> new BottleItem(Items.LINGERING_POTION, GENERAL_PROPS));

  // base molten fluids
  public static final FlowingFluidObject<ForgeFlowingFluid> searedStone   = FLUIDS.register("seared_stone"  ).type(hot("seared_stone"  ).temperature( 900).lightLevel(6)).block(Material.LAVA, 6).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> scorchedStone = FLUIDS.register("scorched_stone").type(hot("scorched_stone").temperature( 800).lightLevel(4)).block(Material.LAVA, 4).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenClay    = FLUIDS.register("molten_clay"   ).type(hot("molten_clay"   ).temperature( 750).lightLevel(3)).block(Material.LAVA, 3).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenGlass   = FLUIDS.register("molten_glass"  ).type(hot("molten_glass"  ).temperature(1050).lightLevel(1)).block(Material.LAVA, 1).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> liquidSoul    = FLUIDS.register("liquid_soul"   ).type(hot("liquid_soul"   ).temperature( 700).lightLevel(2)).block(Material.LAVA, 2).bucket().flowing();
  // ceramics compat
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPorcelain = FLUIDS.register("molten_porcelain").type(hot("molten_porcelain").temperature(1000).lightLevel(2)).block(Material.LAVA, 2).bucket().flowing();
  // fancy molten fluids
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenObsidian = FLUIDS.register("molten_obsidian").type(hot("molten_obsidian").temperature(1300).lightLevel(3)).block(Material.LAVA, 3).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEnder    = FLUIDS.register("molten_ender"   ).type(hot("molten_ender"   ).temperature( 777).lightLevel(5)).block(Material.LAVA, 5).bucket().tagName("ender").flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> blazingBlood   = FLUIDS.register("blazing_blood"  ).type(hot("blazing_blood"  ).temperature(1800).lightLevel(15).density(3500)).block(Material.LAVA, 15).bucket().flowing();

  // ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEmerald  = FLUIDS.register("molten_emerald" ).type(hot("molten_emerald" ).temperature(1234).lightLevel( 9)).block(Material.LAVA,  9).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenQuartz   = FLUIDS.register("molten_quartz"  ).type(hot("molten_quartz"  ).temperature( 937).lightLevel( 6)).block(Material.LAVA,  6).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAmethyst = FLUIDS.register("molten_amethyst").type(hot("molten_amethyst").temperature(1250).lightLevel(11)).block(Material.LAVA, 11).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenDiamond  = FLUIDS.register("molten_diamond" ).type(hot("molten_diamond" ).temperature(1750).lightLevel(13)).block(Material.LAVA, 13).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenDebris   = FLUIDS.register("molten_debris"  ).type(hot("molten_debris"  ).temperature(1475).lightLevel(14)).block(Material.LAVA, 14).bucket().flowing();
  // metal ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenIron   = FLUIDS.register("molten_iron"  ).type(hot("molten_iron"  ).temperature(1100).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenGold   = FLUIDS.register("molten_gold"  ).type(hot("molten_gold"  ).temperature(1000).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenCopper = FLUIDS.register("molten_copper").type(hot("molten_copper").temperature( 800).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenCobalt = FLUIDS.register("molten_cobalt").type(hot("molten_cobalt").temperature(1250).lightLevel( 8)).block(Material.LAVA,  8).bucket().flowing();
  // alloys
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSlimesteel     = FLUIDS.register("molten_slimesteel"     ).type(hot("molten_slimesteel"     ).temperature(1200).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAmethystBronze = FLUIDS.register("molten_amethyst_bronze").type(hot("molten_amethyst_bronze").temperature(1120).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRoseGold       = FLUIDS.register("molten_rose_gold"      ).type(hot("molten_rose_gold"      ).temperature( 850).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPigIron        = FLUIDS.register("molten_pig_iron"       ).type(hot("molten_pig_iron"       ).temperature(1111).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();

  public static final FlowingFluidObject<ForgeFlowingFluid> moltenManyullyn   = FLUIDS.register("molten_manyullyn"   ).type(hot("molten_manyullyn"   ).temperature(1500).lightLevel(11)).block(Material.LAVA, 11).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenHepatizon   = FLUIDS.register("molten_hepatizon"   ).type(hot("molten_hepatizon"   ).temperature(1700).lightLevel( 8)).block(Material.LAVA,  8).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenQueensSlime = FLUIDS.register("molten_queens_slime").type(hot("molten_queens_slime").temperature(1450).lightLevel( 9)).block(Material.LAVA,  9).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSoulsteel   = FLUIDS.register("molten_soulsteel"   ).type(hot("molten_soulsteel"   ).temperature(1500).lightLevel( 6)).block(Material.LAVA,  6).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenNetherite   = FLUIDS.register("molten_netherite"   ).type(hot("molten_netherite"   ).temperature(1550).lightLevel(14)).block(Material.LAVA, 14).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenKnightslime = FLUIDS.register("molten_knightslime" ).type(hot("molten_knightslime" ).temperature(1425).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();

  // compat ores
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenTin      = FLUIDS.register("molten_tin"     ).type(hot("molten_tin"     ).temperature( 525).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenAluminum = FLUIDS.register("molten_aluminum").type(hot("molten_aluminum").temperature( 725).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenLead     = FLUIDS.register("molten_lead"    ).type(hot("molten_lead"    ).temperature( 630).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSilver   = FLUIDS.register("molten_silver"  ).type(hot("molten_silver"  ).temperature(1090).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenNickel   = FLUIDS.register("molten_nickel"  ).type(hot("molten_nickel"  ).temperature(1250).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenZinc     = FLUIDS.register("molten_zinc"    ).type(hot("molten_zinc"    ).temperature( 720).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPlatinum = FLUIDS.register("molten_platinum").type(hot("molten_platinum").temperature(1270).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenTungsten = FLUIDS.register("molten_tungsten").type(hot("molten_tungsten").temperature(1250).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenOsmium   = FLUIDS.register("molten_osmium"  ).type(hot("molten_osmium"  ).temperature(1275).lightLevel( 4)).block(Material.LAVA,  4).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenUranium  = FLUIDS.register("molten_uranium" ).type(hot("molten_uranium" ).temperature(1130).lightLevel(15)).block(Material.LAVA, 15).bucket().flowing();

  // compat alloys
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenBronze     = FLUIDS.register("molten_bronze"    ).type(hot("molten_bronze"    ).temperature(1000).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenBrass      = FLUIDS.register("molten_brass"     ).type(hot("molten_brass"     ).temperature( 905).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenElectrum   = FLUIDS.register("molten_electrum"  ).type(hot("molten_electrum"  ).temperature(1060).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenInvar      = FLUIDS.register("molten_invar"     ).type(hot("molten_invar"     ).temperature(1200).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenConstantan = FLUIDS.register("molten_constantan").type(hot("molten_constantan").temperature(1220).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenPewter     = FLUIDS.register("molten_pewter"    ).type(hot("molten_pewter"    ).temperature( 700).lightLevel(10)).block(Material.LAVA, 10).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSteel      = FLUIDS.register("molten_steel"     ).type(hot("molten_steel"     ).temperature(1250).lightLevel(13)).block(Material.LAVA, 13).bucket().flowing();

  // mod-specific compat alloys
  // thermal
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenEnderium = FLUIDS.register("molten_enderium").type(hot("molten_enderium").temperature(1650).lightLevel(12)).block(Material.LAVA, 12).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenLumium   = FLUIDS.register("molten_lumium"  ).type(hot("molten_lumium"  ).temperature(1350).lightLevel(15)).block(Material.LAVA, 15).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenSignalum = FLUIDS.register("molten_signalum").type(hot("molten_signalum").temperature(1425).lightLevel(13)).block(Material.LAVA, 13).bucket().flowing();
  // mekanism
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRefinedGlowstone = FLUIDS.register("molten_refined_glowstone").type(hot("molten_refined_glowstone").temperature(1125).lightLevel(15)).block(Material.LAVA, 15).bucket().flowing();
  public static final FlowingFluidObject<ForgeFlowingFluid> moltenRefinedObsidian  = FLUIDS.register("molten_refined_obsidian" ).type(hot("molten_refined_obsidian" ).temperature(1775).lightLevel( 7)).block(Material.LAVA,  7).bucket().flowing();

  // fluid data serializer
  public static final FluidDataSerializer FLUID_DATA_SERIALIZER = new FluidDataSerializer();
  public static final RegistryObject<EntityDataSerializer<?>> FLUID_DATA_SERIALIZER_REGISTRY = DATA_SERIALIZERS.register("fluid", () -> FLUID_DATA_SERIALIZER);

  /** Creates a builder for a cool fluid with sounds */
  private static FluidType.Properties cool() {
    return FluidType.Properties.create()
                               .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                               .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
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
                               .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    boolean client = event.includeClient();
    datagenerator.addProvider(client, new FluidTooltipProvider(datagenerator));
    datagenerator.addProvider(client, new FluidTextureProvider(datagenerator));
    datagenerator.addProvider(client, new FluidBucketModelProvider(datagenerator, TConstruct.MOD_ID));
    datagenerator.addProvider(client, new FluidBlockstateModelProvider(datagenerator, TConstruct.MOD_ID));
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
        if (container.emptyContents(null, level, blockpos, null)) {
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

      // brew congealed slime into bottles to get slime bottles, easy melting
      for (SlimeType slime : SlimeType.values()) {
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(TinkerWorld.congealedSlime.get(slime)), new ItemStack(TinkerFluids.slimeBottle.get(slime))));
      }
      BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.MAGMA_BLOCK), new ItemStack(TinkerFluids.magmaBottle)));
    });
  }
}
