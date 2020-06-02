package slimeknights.tconstruct.shared;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class CommonsClientEvents {

  public static Minecraft minecraft = Minecraft.getInstance();

  @SubscribeEvent
  public static void registerBlockColors(ColorHandlerEvent.Block event) {
    event.getBlockColors().register((state, reader, blockPos, tintIndex) -> {
      if(state.getBlock() instanceof ClearStainedGlassBlock) {
        ClearStainedGlassBlock stainedGlassBlock = (ClearStainedGlassBlock)state.getBlock();
        return stainedGlassBlock.getGlassColor().getColor();
      }

      MaterialColor materialcolor = state.getMaterialColor(reader, blockPos);
      return materialcolor != null ? materialcolor.colorValue : -1;

    }, DecorativeBlocks.white_clear_stained_glass, DecorativeBlocks.orange_clear_stained_glass, DecorativeBlocks.magenta_clear_stained_glass, DecorativeBlocks.light_blue_clear_stained_glass, DecorativeBlocks.yellow_clear_stained_glass, DecorativeBlocks.lime_clear_stained_glass, DecorativeBlocks.pink_clear_stained_glass, DecorativeBlocks.gray_clear_stained_glass, DecorativeBlocks.light_gray_clear_stained_glass, DecorativeBlocks.cyan_clear_stained_glass, DecorativeBlocks.purple_clear_stained_glass, DecorativeBlocks.blue_clear_stained_glass, DecorativeBlocks.brown_clear_stained_glass, DecorativeBlocks.green_clear_stained_glass, DecorativeBlocks.red_clear_stained_glass, DecorativeBlocks.black_clear_stained_glass);
  }

  @SubscribeEvent
  public static void registerItemColors(ColorHandlerEvent.Item event) {
    final BlockColors blockColors = event.getBlockColors();
    event.getItemColors().register((itemStack, tintIndex) -> {
      BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
      return blockColors.getColor(blockstate, null, null, tintIndex);
    }, DecorativeBlocks.white_clear_stained_glass, DecorativeBlocks.orange_clear_stained_glass, DecorativeBlocks.magenta_clear_stained_glass, DecorativeBlocks.light_blue_clear_stained_glass, DecorativeBlocks.yellow_clear_stained_glass, DecorativeBlocks.lime_clear_stained_glass, DecorativeBlocks.pink_clear_stained_glass, DecorativeBlocks.gray_clear_stained_glass, DecorativeBlocks.light_gray_clear_stained_glass, DecorativeBlocks.cyan_clear_stained_glass, DecorativeBlocks.purple_clear_stained_glass, DecorativeBlocks.blue_clear_stained_glass, DecorativeBlocks.brown_clear_stained_glass, DecorativeBlocks.green_clear_stained_glass, DecorativeBlocks.red_clear_stained_glass, DecorativeBlocks.black_clear_stained_glass);
  }
}
