package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeSerializer;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.smeltery.block.CastingBasinBlock;
import slimeknights.tconstruct.smeltery.block.CastingTableBlock;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.SearedGlassBlock;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.item.TankItem;
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import java.util.Set;
import java.util.function.Function;

/**
 * Contains logic for the multiblocks in the mod
 */
public final class TinkerSmeltery extends TinkerModule {
  /** Tab for all blocks related to the smeltery */
  public static final ItemGroup TAB_SMELTERY = new SupplierItemGroup(TConstruct.modID, "smeltery", () -> new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK)));
  public static final Logger log = Util.getLogger("tinker_smeltery");

  /* Bricks */
  /* Crafting related items */

  /*
   * Block base properties
   */
  private static final Item.Properties SMELTERY_PROPS = new Item.Properties().group(TAB_SMELTERY);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, SMELTERY_PROPS);

  /*
   * Blocks
   */
  private static final Block.Properties SMELTERY_GLASS = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).notSolid();
  public static final BlockItemObject<Block> grout = BLOCKS.register("grout", () -> new Block(GENERIC_SAND_BLOCK), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<SearedGlassBlock> searedGlass = BLOCKS.register("seared_glass", () -> new SearedGlassBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<ClearGlassPaneBlock> searedGlassPane = BLOCKS.register("seared_glass_pane", () -> new ClearGlassPaneBlock(SMELTERY_GLASS), TOOLTIP_BLOCK_ITEM);

  // seared
  // TODO: registerBuilding does not handle custom blocks
  private static final Block.Properties SMELTERY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F);
  private static final Block.Properties FAUCET = builder(Material.ROCK, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).notSolid();
  public static final BuildingBlockObject searedStone = BLOCKS.registerBuilding("seared_stone", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedCobble = BLOCKS.registerBuilding("seared_cobble", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedPaver = BLOCKS.registerBuilding("seared_paver", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedBricks = BLOCKS.registerBuilding("seared_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedCrackedBricks = BLOCKS.registerBuilding("seared_cracked_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedFancyBricks = BLOCKS.registerBuilding("seared_fancy_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedSquareBricks = BLOCKS.registerBuilding("seared_square_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedSmallBricks = BLOCKS.registerBuilding("seared_small_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedTriangleBricks = BLOCKS.registerBuilding("seared_triangle_bricks", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedCreeper = BLOCKS.registerBuilding("seared_creeper", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedRoad = BLOCKS.registerBuilding("seared_road", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final BuildingBlockObject searedTile = BLOCKS.registerBuilding("seared_tile", SMELTERY, TOOLTIP_BLOCK_ITEM);
  public static final EnumObject<SearedTankBlock.TankType,SearedTankBlock> searedTank = BLOCKS.registerEnum("seared", SearedTankBlock.TankType.values(), (type) -> new SearedTankBlock(SMELTERY_GLASS), (b) -> new TankItem(b, SMELTERY_PROPS));
  public static final BlockItemObject<FaucetBlock> searedFaucet = BLOCKS.register("faucet", () -> new FaucetBlock(FAUCET), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<CastingBasinBlock> castingBasin = BLOCKS.register("casting_basin", () -> new CastingBasinBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<CastingTableBlock> castingTable = BLOCKS.register("casting_table", () -> new CastingTableBlock(SMELTERY), TOOLTIP_BLOCK_ITEM);

  /*
   * Tile entities
   */
  public static final RegistryObject<TileEntityType<SmelteryComponentTileEntity>> smelteryComponent = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, (set) -> {
    set.addAll(searedStone.values());
    set.addAll(searedCobble.values());
    set.addAll(searedBricks.values());
    set.addAll(searedCrackedBricks.values());
    set.addAll(searedFancyBricks.values());
    set.addAll(searedSquareBricks.values());
    set.addAll(searedSmallBricks.values());
    set.addAll(searedTriangleBricks.values());
    set.addAll(searedCreeper.values());
    set.addAll(searedPaver.values());
    set.addAll(searedRoad.values());
    set.addAll(searedTile.values());
  });
  public static final RegistryObject<TileEntityType<TankTileEntity>> tank = TILE_ENTITIES.register("tank", TankTileEntity::new, (set) -> set.addAll(searedTank.values()));
  public static final RegistryObject<TileEntityType<FaucetTileEntity>> faucet = TILE_ENTITIES.register("faucet", FaucetTileEntity::new, searedFaucet);
  public static final RegistryObject<TileEntityType<AbstractCastingTileEntity>> basin = TILE_ENTITIES.register("basin", AbstractCastingTileEntity.Basin::new, castingBasin);
  public static final RegistryObject<TileEntityType<AbstractCastingTileEntity>> table = TILE_ENTITIES.register("table", AbstractCastingTileEntity.Table::new, castingTable);

  /*
   * Items
   */
  public static final ItemObject<Item> searedBrick = ITEMS.register("seared_brick", SMELTERY_PROPS);
  public static final ItemObject<Item> blankCast = ITEMS.register("blank_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> ingotCast = ITEMS.register("ingot_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> nuggetCast = ITEMS.register("nugget_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> gemCast = ITEMS.register("gem_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> pickaxeHeadCast = ITEMS.register("pickaxe_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> smallBindingCast = ITEMS.register("small_binding_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> toolRodCast = ITEMS.register("tool_rod_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> toughToolRodCast = ITEMS.register("tough_tool_rod_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> largePlateCast = ITEMS.register("large_plate_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> swordBladeCast = ITEMS.register("sword_blade_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> hammerHeadCast = ITEMS.register("hammer_head_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> wideGuardCast = ITEMS.register("wide_guard_cast", SMELTERY_PROPS);
  public static final ItemObject<Item> shovelHeadCast = ITEMS.register("shovel_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> axeHeadCast = ITEMS.register("axe_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> scytheHeadCast = ITEMS.register("scythe_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> sharpeningKitCast = ITEMS.register("sharpening_kit_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> largeSwordBladeCast = ITEMS.register("large_sword_blade_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> knifeBladeCast = ITEMS.register("knife_blade_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> bowLimbCast = ITEMS.register("bow_limb_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> excavatorHeadCast = ITEMS.register("excavator_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> handGuardCast = ITEMS.register("hand_guard_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> crossGuardCast = ITEMS.register("cross_guard_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> gearCast = ITEMS.register("gear_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> broadAxeHeadCast = ITEMS.register("broad_axe_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> arrowHeadCast = ITEMS.register("arrow_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> kamaHeadCast = ITEMS.register("kama_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> panHeadCast = ITEMS.register("pan_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> signHeadCast = ITEMS.register("sign_head_cast", SMELTERY_PROPS);
//  public static final ItemObject<Item> toughBindingCast = ITEMS.register("tough_binding_cast", SMELTERY_PROPS);


  /*
   * Recipe
   */
  public static final RegistryObject<ItemCastingRecipeSerializer<ItemCastingRecipe.Basin>> basinRecipeSerializer = RECIPE_SERIALIZERS.register("casting_basin", () -> new ItemCastingRecipeSerializer<>(ItemCastingRecipe.Basin::new));
  public static final RegistryObject<ItemCastingRecipeSerializer<ItemCastingRecipe.Table>> tableRecipeSerializer = RECIPE_SERIALIZERS.register("casting_table", () -> new ItemCastingRecipeSerializer<>(ItemCastingRecipe.Table::new));

  /*
   * Smeltery block lists
   */
  public static Set<Block> validSmelteryBlocks;
  public static Set<Block> searedStairsSlabs;
  public static Set<Block> validTinkerTankBlocks;
  public static Set<Block> validTinkerTankFloorBlocks;
  @SubscribeEvent
  static void registerBlockLists(final FMLCommonSetupEvent event) {
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(TinkerSmeltery.searedStone.get());
    builder.add(TinkerSmeltery.searedCobble.get());
    builder.add(TinkerSmeltery.searedBricks.get());
    builder.add(TinkerSmeltery.searedCrackedBricks.get());
    builder.add(TinkerSmeltery.searedFancyBricks.get());
    builder.add(TinkerSmeltery.searedSquareBricks.get());
    builder.add(TinkerSmeltery.searedSmallBricks.get());
    builder.add(TinkerSmeltery.searedTriangleBricks.get());
    builder.add(TinkerSmeltery.searedCreeper.get());
    builder.add(TinkerSmeltery.searedPaver.get());
    builder.add(TinkerSmeltery.searedRoad.get());
    builder.add(TinkerSmeltery.searedTile.get());
    ImmutableSet<Block> searedBlocks = builder.build();

    // smeltery adds in tank, glass and drains
    builder = ImmutableSet.builder();
    builder.addAll(searedBlocks);
    builder.addAll(TinkerSmeltery.searedTank.values());
    //builder.add(smelteryIO);
    builder.add(TinkerSmeltery.searedGlass.get());

    // same blocks right now for smeltery and tinker tank
    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build();
    // tinker tank floor disallows tanks
    builder = ImmutableSet.builder();
    builder.addAll(searedBlocks);
    builder.add(TinkerSmeltery.searedGlass.get());
    //builder.add(smelteryIO);
    validTinkerTankFloorBlocks = builder.build();

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.addAll(TinkerSmeltery.searedStone.values());
    builder.addAll(TinkerSmeltery.searedCobble.values());
    builder.addAll(TinkerSmeltery.searedBricks.values());
    builder.addAll(TinkerSmeltery.searedCrackedBricks.values());
    builder.addAll(TinkerSmeltery.searedFancyBricks.values());
    builder.addAll(TinkerSmeltery.searedSquareBricks.values());
    builder.addAll(TinkerSmeltery.searedSmallBricks.values());
    builder.addAll(TinkerSmeltery.searedTriangleBricks.values());
    builder.addAll(TinkerSmeltery.searedCreeper.values());
    builder.addAll(TinkerSmeltery.searedPaver.values());
    builder.addAll(TinkerSmeltery.searedRoad.values());
    builder.addAll(TinkerSmeltery.searedTile.values());
    searedStairsSlabs = builder.build();
  }
}
