package slimeknights.tconstruct.shared;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

public class CommonsClientProxy extends ClientProxy {

  public static Minecraft minecraft = Minecraft.getInstance();

  @Override
  public void init() {
    final BlockColors blockColors = minecraft.getBlockColors();

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state != null && state.getBlock() instanceof ClearStainedGlassBlock) {
        ClearStainedGlassBlock stainedGlassBlock = (ClearStainedGlassBlock) state.getBlock();
        return stainedGlassBlock.getGlassColor().getColor();
      }

      MaterialColor materialcolor = state.getMaterialColor(reader, blockPos);
      return materialcolor != null ? materialcolor.colorValue : -1;

    }, TinkerCommons.white_clear_stained_glass, TinkerCommons.orange_clear_stained_glass, TinkerCommons.magenta_clear_stained_glass, TinkerCommons.light_blue_clear_stained_glass, TinkerCommons.yellow_clear_stained_glass, TinkerCommons.lime_clear_stained_glass, TinkerCommons.pink_clear_stained_glass, TinkerCommons.gray_clear_stained_glass, TinkerCommons.light_gray_clear_stained_glass, TinkerCommons.cyan_clear_stained_glass, TinkerCommons.purple_clear_stained_glass, TinkerCommons.blue_clear_stained_glass, TinkerCommons.brown_clear_stained_glass, TinkerCommons.green_clear_stained_glass, TinkerCommons.red_clear_stained_glass, TinkerCommons.black_clear_stained_glass);

    minecraft.getItemColors().register((itemStack, tintIndex) -> {
      BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
      return blockColors.getColor(blockstate, (IEnviromentBlockReader) null, (BlockPos) null, tintIndex);
    }, TinkerCommons.white_clear_stained_glass, TinkerCommons.orange_clear_stained_glass, TinkerCommons.magenta_clear_stained_glass, TinkerCommons.light_blue_clear_stained_glass, TinkerCommons.yellow_clear_stained_glass, TinkerCommons.lime_clear_stained_glass, TinkerCommons.pink_clear_stained_glass, TinkerCommons.gray_clear_stained_glass, TinkerCommons.light_gray_clear_stained_glass, TinkerCommons.cyan_clear_stained_glass, TinkerCommons.purple_clear_stained_glass, TinkerCommons.blue_clear_stained_glass, TinkerCommons.brown_clear_stained_glass, TinkerCommons.green_clear_stained_glass, TinkerCommons.red_clear_stained_glass, TinkerCommons.black_clear_stained_glass);

    super.init();
  }
}
