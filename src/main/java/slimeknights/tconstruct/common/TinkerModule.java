package slimeknights.tconstruct.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.TileEntityTypeDeferredRegister;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.swing.plaf.PanelUI;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains base helpers for all Tinker modules
 */
public abstract class TinkerModule implements ModInitializer {
  // deferred register instances
  protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(TConstruct.modID);
  protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(TConstruct.modID);
//  protected static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TConstruct.modID);
  protected static final TileEntityTypeDeferredRegister TILE_ENTITIES = new TileEntityTypeDeferredRegister(TConstruct.modID);
  protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(TConstruct.modID);

  // base block properties

  public static AbstractBlock.Settings getGenericSandBlock() {
    return builder(Material.AGGREGATE, FabricToolTags.SHOVELS, BlockSoundGroup.SAND).strength(3.0f).slipperiness(0.8F);
  }

  public static AbstractBlock.Settings getGenericMetalBlock() {
    return builder(Material.METAL, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(5.0f);
  }

  public static AbstractBlock.Settings getGenericGemBlock() {
    return getGenericMetalBlock();
  }

  public static AbstractBlock.Settings getGenericGlassBlock() {
    return builder(Material.GLASS, FabricToolTags.PICKAXES, BlockSoundGroup.GLASS)
    .requiresTool().strength(0.3F).nonOpaque().allowsSpawning(Blocks::never)
    .solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never);
  }

  /** Creative tab for items that do not fit in another tab */
  @SuppressWarnings("WeakerAccess")
  public static final ItemGroup TAB_GENERAL = FabricItemGroupBuilder.build(new Identifier(TConstruct.modID, "general"), () -> new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)));

  // base item properties
  protected static final Item.Settings GENERAL_PROPS = new Item.Settings().group(TAB_GENERAL);
  protected static final Function<Block,BlockItem> HIDDEN_BLOCK_ITEM = (b) -> new BlockItem(b, getHiddenProps());
  protected static final Function<Block,BlockItem> GENERAL_TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GENERAL_PROPS);
  protected static final Supplier<Item> TOOLTIP_ITEM = () -> new TooltipItem(GENERAL_PROPS);

  public static Item.Settings getHiddenProps() {
    return new Item.Settings().group(TAB_GENERAL);
  }

  public static Function<Block, BlockItem> getGeneralBlockItem() {
    return (b) -> new BlockItem(b, GENERAL_PROPS);
  }

  /** Called during construction to initialize the registers for this mod */
  public static void initRegisters() {
//    CapabilityTinkerPiggyback.register();
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
    final FabricBlockSettings settings = FabricBlockSettings.of(material).sounds(soundType);
    if(toolType != null) {
      settings.breakByTool(toolType);
    }
    return settings;
  }

  public static Identifier id(String path) {
    return new Identifier(TConstruct.modID, path);
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
