package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.registry.BlockItemRegistryAdapter;
import slimeknights.tconstruct.common.registry.BlockRegistryAdapter;
import slimeknights.tconstruct.gadgets.block.DropperRailBlock;
import slimeknights.tconstruct.gadgets.block.PunjiBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GadgetBlocks {

  public static final LadderBlock stone_ladder = TinkerPulse.injected();
  public static final TorchBlock stone_torch = TinkerPulse.injected();
  public static final WallTorchBlock wall_stone_torch = TinkerPulse.injected();
  public static final PunjiBlock punji = TinkerPulse.injected();
  public static final RailBlock wooden_rail = TinkerPulse.injected();
  public static final DropperRailBlock wooden_dropper_rail = TinkerPulse.injected();

  @SubscribeEvent
  static void registerBlocks(final RegistryEvent.Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    registry.register(new LadderBlock(BlockProperties.STONE_LADDER) {}, "stone_ladder");

    registry.register(new TorchBlock(BlockProperties.STONE_TORCH) {}, "stone_torch");
    registry.register(new WallTorchBlock(BlockProperties.STONE_TORCH) {}, "wall_stone_torch");

    registry.register(new RailBlock(BlockProperties.WOODEN_RAIL) {}, "wooden_rail");
    registry.register(new DropperRailBlock(BlockProperties.WOODEN_RAIL), "wooden_dropper_rail");

    registry.register(new PunjiBlock(BlockProperties.PUNJI), "punji");
  }

  @SubscribeEvent
  static void registerBlockItems(final RegistryEvent.Register<Item> event) {
    BlockItemRegistryAdapter registry = new BlockItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGadgets);

    registry.registerBlockItem(GadgetBlocks.stone_ladder);

    registry.registerBlockItem(new WallOrFloorItem(GadgetBlocks.stone_torch, GadgetBlocks.wall_stone_torch, (new Item.Properties()).group(TinkerRegistry.tabGadgets)));

    registry.registerBlockItem(GadgetBlocks.punji);

    registry.registerBlockItem(GadgetBlocks.wooden_rail);
    registry.registerBlockItem(GadgetBlocks.wooden_dropper_rail);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(stone_ladder, (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(stone_torch, (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(wall_stone_torch, (layer) -> layer == RenderType.getCutout());

    RenderTypeLookup.setRenderLayer(wooden_rail, (layer) -> layer == RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(wooden_dropper_rail, (layer) -> layer == RenderType.getCutout());
  }

  private GadgetBlocks() {}
}
