package slimeknights.tconstruct.common;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.mantle.registration.deferred.ContainerTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.TileEntityTypeDeferredRegister;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains base helpers for all Tinker modules
 */
public abstract class TinkerModule {
  // deferred register instances
  protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(TConstruct.modID);
  protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(TConstruct.modID);
//  protected static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TConstruct.modID);
  protected static final TileEntityTypeDeferredRegister TILE_ENTITIES = new TileEntityTypeDeferredRegister(TConstruct.modID);
  protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(TConstruct.modID);
  protected static final ContainerTypeDeferredRegister CONTAINERS = new ContainerTypeDeferredRegister(TConstruct.modID);
//  protected static final DeferredRegister<StatusEffect> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, TConstruct.modID);
//  protected static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TConstruct.modID);
//  protected static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, TConstruct.modID);
//  protected static final DeferredRegister<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, TConstruct.modID);
//  protected static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TConstruct.modID);
//  protected static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TConstruct.modID);
//  protected static final DeferredRegister<Modifier> MODIFIERS = DeferredRegister.create(Modifier.class, TConstruct.modID);
//  protected static final DeferredRegister<GlobalLootModifierSerializer<?>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, TConstruct.modID);

  // base block properties
  protected static final AbstractBlock.Settings GENERIC_SAND_BLOCK = builder(Material.AGGREGATE, FabricToolTags.SHOVELS, BlockSoundGroup.SAND).strength(3.0f).slipperiness(0.8F);
  protected static final AbstractBlock.Settings GENERIC_METAL_BLOCK = builder(Material.METAL, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(5.0f);
  protected static final AbstractBlock.Settings GENERIC_GEM_BLOCK = GENERIC_METAL_BLOCK;
  protected static final AbstractBlock.Settings GENERIC_GLASS_BLOCK = builder(Material.GLASS, FabricToolTags.PICKAXES, BlockSoundGroup.GLASS).requiresTool().strength(0.3F).nonOpaque(); //TODO: .setAllowsSpawn(Blocks::neverAllowSpawn).setOpaque(Blocks::isntSolid).setSuffocates(Blocks::isntSolid).setBlocksVision(Blocks::isntSolid);

  /** Creative tab for items that do not fit in another tab */
  @SuppressWarnings("WeakerAccess")
  public static final ItemGroup TAB_GENERAL = FabricItemGroupBuilder.build(new Identifier(TConstruct.modID, "general"), () -> new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)));

  // base item properties
  protected static final Item.Settings HIDDEN_PROPS = new Item.Settings();
  protected static final Item.Settings GENERAL_PROPS = new Item.Settings().group(TAB_GENERAL);
  protected static final Function<Block,? extends BlockItem> HIDDEN_BLOCK_ITEM = (b) -> new BlockItem(b, HIDDEN_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_BLOCK_ITEM = (b) -> new BlockItem(b, GENERAL_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GENERAL_PROPS);
  protected static final Supplier<Item> TOOLTIP_ITEM = () -> new TooltipItem(GENERAL_PROPS);

  /** Called during construction to initialize the registers for this mod */
  public static void initRegisters() {
    CapabilityTinkerPiggyback.register();
/*//    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(TConstruct.modID);
    ITEMS.register();
//    FLUIDS.register(bus);
    TILE_ENTITIES.register();
    ENTITIES.register();
    CONTAINERS.register();
//    POTIONS.register(bus);
//    FEATURES.register(bus);
//    STRUCTURE_FEATURES.register(bus);
//    BLOCK_STATE_PROVIDER_TYPES.register(bus);
//    RECIPE_SERIALIZERS.register(bus);
//    PARTICLE_TYPES.register(bus);
//    MODIFIERS.register(bus);
//    GLOBAL_LOOT_MODIFIERS.register(bus);*/
  }

  /**
   * This is a function that returns null, despite being nonnull. It is used on object holder fields to remove IDE warnings about constant null as it will be nonnull
   * @param <T>  Field type
   * @return  Null
   */
  @NotNull
  @SuppressWarnings("ConstantConditions")
  public static <T> T injected() {
    return null;
  }

  /** Constant to use for blocks with no tool for more readable code */
  protected static final Tag<Item> NO_TOOL = null;

  /**
   * We use this builder to ensure that our blocks all have the most important properties set.
   * This way it'll stick out if a block doesn't have a tooltype or sound set.
   * It may be a bit less clear at first, since the actual builder methods tell you what each value means,
   * but as long as we don't statically import the enums it should be just as readable.
   */
  protected static FabricBlockSettings builder(Material material, @Nullable Tag<Item> toolType, BlockSoundGroup soundType) {
    return FabricBlockSettings.of(material).breakByTool(toolType).sounds(soundType);
  }

  /**
   * Creates a Tinkers Construct resource location
   * @param id  Resource path
   * @return  Tinkers Construct resource location
   */
  protected static Identifier location(String id) {
    return new Identifier(TConstruct.modID, id);
  }

  /**
   * Creates a Tinkers Construct resource location string
   * @param id  Resource path
   * @return  Tinkers Construct resource location string
   */
  protected static String locationString(String id) {
    return TConstruct.modID + ":" + id;
  }
}
