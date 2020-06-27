package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontRenderer;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.tools.ToolClientEvents;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class CommonsClientEvents extends ClientEventBase {

  public static Minecraft minecraft = Minecraft.getInstance();

  public static CustomFontRenderer fontRenderer;

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    RenderTypeLookup.setRenderLayer(TinkerCommons.glow.get(), RenderType.getTranslucent());

    // glass
    RenderTypeLookup.setRenderLayer(TinkerCommons.clearGlass.get(), RenderType.getCutout());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      RenderTypeLookup.setRenderLayer(TinkerCommons.clearStainedGlass.get(color), RenderType.getTranslucent());
    }
  }

  @SubscribeEvent
  static void registerColorHandlers(ColorHandlerEvent.Item event) {
    BlockColors blockColors = event.getBlockColors();
    ItemColors itemColors = event.getItemColors();
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      blockColors.register((state, reader, pos, index) -> color.getColor(), block);
      registerBlockItemColorAlias(blockColors, itemColors, block);
    }
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    CommonsClientEvents.fontRenderer = new CustomFontRenderer(Minecraft.getInstance().fontRenderer);
    CommonsClientEvents.fontRenderer.setBidiFlag(Minecraft.getInstance().getLanguageManager().isCurrentLanguageBidirectional());

    Minecraft minecraft = Minecraft.getInstance();

    if (minecraft != null) {
      IResourceManager manager = Minecraft.getInstance().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
        ((IReloadableResourceManager) manager).addReloadListener(CommonsClientEvents.fontRenderer);
      }
    }
  }
}
