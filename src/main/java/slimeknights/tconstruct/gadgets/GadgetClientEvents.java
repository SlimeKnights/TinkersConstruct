package slimeknights.tconstruct.gadgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.gadgets.item.SlimeBootsItem;
import slimeknights.tconstruct.gadgets.item.SlimeSlingItem;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.shared.block.SlimeBlock;

import javax.annotation.Nonnull;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class GadgetClientEvents {
  @SubscribeEvent
  public static void registerItemColors(ColorHandlerEvent.Item event) {
    final ItemColors colors = event.getItemColors();
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      colors.register((stack,index)->slime.getBallColor(), GadgetItems.slime_sling.get(slime), GadgetItems.slime_boots.get(slime));
    }
  }

  @SubscribeEvent
  public static void registerModels(ModelRegistryEvent event) {
    // TODO: reinstate when Forge fixes itself
    //StateContainer<Block, BlockState> dummyContainer = new StateContainer.Builder<Block, BlockState>(Blocks.AIR).add(BooleanProperty.create("map")).create(BlockState::new);
    //for (FrameType frameType : FrameType.values()) {
    //  ResourceLocation fancyFrame = new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame");
    //  for (BlockState state : dummyContainer.getValidStates()) {
    //    ModelLoader.addSpecialModel(BlockModelShapes.getModelLocation(fancyFrame, state));
    //  }
    //}

    for (FrameType frameType : FrameType.values()) {
      ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_empty"), "inventory"));
      ModelLoader.addSpecialModel(new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_map"), "inventory"));
    }
  }

  @SubscribeEvent
  public static void clientSetup(FMLClientSetupEvent event) {
    Minecraft mc = Minecraft.getInstance();

    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.fancy_item_frame, (manager) -> new FancyItemFrameRenderer(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.throwable_glow_ball, (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(TinkerGadgets.throwable_efln_ball, (manager) -> new SpriteRenderer<>(manager, mc.getItemRenderer()));
  }
}
