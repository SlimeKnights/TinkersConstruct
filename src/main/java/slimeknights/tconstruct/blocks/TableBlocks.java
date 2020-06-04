package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.tables.block.chest.PartChestBlock;
import slimeknights.tconstruct.tables.block.chest.PatternChestBlock;
import slimeknights.tconstruct.tables.block.table.CraftingStationBlock;
import slimeknights.tconstruct.tables.block.table.PartBuilderBlock;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TableBlocks {
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);
  private static final Item.Properties GENERAL_PROPS = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GENERAL_PROPS);

  public static void init() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(bus);
  }

  public static final BlockItemObject<CraftingStationBlock> crafting_station = BLOCKS.register("crafting_station", () -> new CraftingStationBlock(BlockProperties.TOOL_TABLE), DEFAULT_BLOCK_ITEM);
  public static final BlockItemObject<PartBuilderBlock> part_builder = BLOCKS.register("part_builder", () -> new PartBuilderBlock(BlockProperties.TOOL_TABLE), DEFAULT_BLOCK_ITEM);
  public static final BlockItemObject<PatternChestBlock> pattern_chest = BLOCKS.register("pattern_chest", () -> new PatternChestBlock(BlockProperties.TOOL_TABLE), DEFAULT_BLOCK_ITEM);
  public static final BlockItemObject<PartChestBlock> part_chest = BLOCKS.register("part_chest", () -> new PartChestBlock(BlockProperties.TOOL_TABLE), DEFAULT_BLOCK_ITEM);
}
