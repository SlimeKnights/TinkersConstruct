package slimeknights.tconstruct.tables;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import slimeknights.tconstruct.compat.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.render.InventoryBlockEntityRenderer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.tables.client.inventory.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.ModifierWorktableScreen;
import slimeknights.tconstruct.tables.client.inventory.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerChestScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

@SuppressWarnings("unused")
@EventBusSubscriber(modid=TConstruct.MOD_ID, value=Dist.CLIENT, bus=Bus.MOD)
public class TableClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    BlockEntityRendererProvider<TableBlockEntity> tableRenderer = InventoryBlockEntityRenderer::new;
    event.registerBlockEntityRenderer(TinkerTables.craftingStationTile.get(), tableRenderer);
    event.registerBlockEntityRenderer(TinkerTables.tinkerStationTile.get(), tableRenderer);
    event.registerBlockEntityRenderer(TinkerTables.modifierWorktableTile.get(), tableRenderer);
    event.registerBlockEntityRenderer(TinkerTables.partBuilderTile.get(), tableRenderer);
  }

  @SubscribeEvent
  static void setupClient(final FMLClientSetupEvent event) {}

  @SubscribeEvent
  static void registerMenuScreens(RegisterMenuScreensEvent event) {
    event.register(TinkerTables.craftingStationContainer.get(), CraftingStationScreen::new);
    event.register(TinkerTables.tinkerStationContainer.get(), TinkerStationScreen::new);
    event.register(TinkerTables.partBuilderContainer.get(), PartBuilderScreen::new);
    event.register(TinkerTables.modifierWorktableContainer.get(), ModifierWorktableScreen::new);
    event.register(TinkerTables.tinkerChestContainer.get(), TinkerChestScreen::new);
  }

  @SubscribeEvent
  static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
    event.register((state, world, pos, index) -> {
      if (world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TinkersChestBlockEntity) {
          return ((TinkersChestBlockEntity)te).getColor();
        }
      }
      return -1;
    }, TinkerTables.tinkersChest.get());
  }

  @SubscribeEvent
  static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
    event.register((stack, index) -> ((DyeableLeatherItem)stack.getItem()).getColor(stack), TinkerTables.tinkersChest.asItem());
  }
}
