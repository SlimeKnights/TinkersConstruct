package slimeknights.tconstruct.blocks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.block.DropperRailBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.BlockDeferredRegister;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GadgetBlocks {
  private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TConstruct.modID);
  private static final Item.Properties GADGET_PROPS = new Item.Properties().group(TinkerRegistry.tabGadgets);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GADGET_PROPS);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(modEventBus);
  }

  public static final BlockItemObject<LadderBlock> stone_ladder = BLOCKS.register("stone_ladder", () -> new LadderBlock(BlockProperties.STONE_LADDER) {}, DEFAULT_BLOCK_ITEM);
  public static final RegistryObject<WallTorchBlock> wall_stone_torch = BLOCKS.registerNoItem("wall_stone_torch", () -> new WallTorchBlock(BlockProperties.STONE_TORCH) {});
  public static final BlockItemObject<TorchBlock> stone_torch = BLOCKS.register("stone_torch",
    () -> new TorchBlock(BlockProperties.STONE_TORCH) {},
    (block) -> new WallOrFloorItem(block, wall_stone_torch.get(), GADGET_PROPS));
  public static final BlockItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(BlockProperties.PUNJI), TOOLTIP_BLOCK_ITEM);
  public static final BlockItemObject<RailBlock> wooden_rail = BLOCKS.register("wooden_rail", () -> new RailBlock(BlockProperties.WOODEN_RAIL) {}, DEFAULT_BLOCK_ITEM);
  public static final BlockItemObject<DropperRailBlock> wooden_dropper_rail = BLOCKS.register("wooden_dropper_rail", () -> new DropperRailBlock(BlockProperties.WOODEN_RAIL), TOOLTIP_BLOCK_ITEM);

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(stone_ladder.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(stone_torch.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(wall_stone_torch.get(), (layer) -> layer == RenderType.getCutout());

    RenderTypeLookup.setRenderLayer(wooden_rail.get(), (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(wooden_dropper_rail.get(), (layer) -> layer == RenderType.getCutout());
  }
}
