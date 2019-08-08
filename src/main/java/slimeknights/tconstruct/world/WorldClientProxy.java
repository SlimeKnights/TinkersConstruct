package slimeknights.tconstruct.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.BlockItem;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.slime.BlueColorReloadListener;
import slimeknights.tconstruct.world.client.slime.OrangeColorReloadListener;
import slimeknights.tconstruct.world.client.slime.PurpleColorReloadListener;

public class WorldClientProxy extends ClientProxy {

  public static SlimeColorizer slimeColorizer = new SlimeColorizer();
  public static Minecraft minecraft = Minecraft.getInstance();

  @Override
  public void construct() {
    if (minecraft != null) {
      ((IReloadableResourceManager) minecraft.getResourceManager()).addReloadListener(new BlueColorReloadListener());
      ((IReloadableResourceManager) minecraft.getResourceManager()).addReloadListener(new PurpleColorReloadListener());
      ((IReloadableResourceManager) minecraft.getResourceManager()).addReloadListener(new OrangeColorReloadListener());
    }
  }

  @Override
  public void preInit() {
    super.preInit();
  }

  @Override
  public void init() {
    final BlockColors blockColors = minecraft.getBlockColors();

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state != null && state.getBlock() instanceof SlimeGrassBlock) {
        SlimeGrassBlock slimeGrassBlock = (SlimeGrassBlock) state.getBlock();
        return this.getSlimeColorByPos(blockPos, slimeGrassBlock.getFoliageType(), null);
      }

      MaterialColor materialcolor = state.getMaterialColor(reader, blockPos);
      return materialcolor != null ? materialcolor.colorValue : -1;

    }, TinkerWorld.blue_vanilla_slime_grass, TinkerWorld.purple_vanilla_slime_grass, TinkerWorld.orange_vanilla_slime_grass, TinkerWorld.blue_green_slime_grass, TinkerWorld.purple_green_slime_grass, TinkerWorld.orange_green_slime_grass, TinkerWorld.blue_blue_slime_grass, TinkerWorld.purple_blue_slime_grass, TinkerWorld.orange_blue_slime_grass, TinkerWorld.blue_purple_slime_grass, TinkerWorld.purple_purple_slime_grass, TinkerWorld.orange_purple_slime_grass, TinkerWorld.blue_magma_slime_grass, TinkerWorld.purple_magma_slime_grass, TinkerWorld.orange_magma_slime_grass);

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state != null && state.getBlock() instanceof SlimeLeavesBlock) {
        SlimeLeavesBlock slimeLeavesBlock = (SlimeLeavesBlock) state.getBlock();
        return this.getSlimeColorByPos(blockPos, slimeLeavesBlock.getFoliageType(), SlimeColorizer.LOOP_OFFSET);
      }

      MaterialColor materialColor = state.getMaterialColor(reader, blockPos);
      return materialColor != null ? materialColor.colorValue : -1;
    }, TinkerWorld.blue_slime_leaves, TinkerWorld.purple_slime_leaves, TinkerWorld.orange_slime_leaves);

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state != null && state.getBlock() instanceof SlimeTallGrassBlock) {
        SlimeTallGrassBlock slimeTallGrassBlock = (SlimeTallGrassBlock) state.getBlock();
        return this.getSlimeColorByPos(blockPos, slimeTallGrassBlock.getFoliageType(), null);
      }

      MaterialColor materialColor = state.getMaterialColor(reader, blockPos);
      return materialColor != null ? materialColor.colorValue : -1;
    }, TinkerWorld.blue_slime_fern, TinkerWorld.purple_slime_fern, TinkerWorld.orange_slime_fern, TinkerWorld.blue_slime_tall_grass, TinkerWorld.purple_slime_tall_grass, TinkerWorld.orange_slime_tall_grass);

    minecraft.getItemColors().register((itemStack, tintIndex) -> {
      BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
      return blockColors.getColor(blockstate, (IEnviromentBlockReader) null, (BlockPos) null, tintIndex);
    }, TinkerWorld.blue_vanilla_slime_grass, TinkerWorld.purple_vanilla_slime_grass, TinkerWorld.orange_vanilla_slime_grass, TinkerWorld.blue_green_slime_grass, TinkerWorld.purple_green_slime_grass, TinkerWorld.orange_green_slime_grass, TinkerWorld.blue_blue_slime_grass, TinkerWorld.purple_blue_slime_grass, TinkerWorld.orange_blue_slime_grass, TinkerWorld.blue_purple_slime_grass, TinkerWorld.purple_purple_slime_grass, TinkerWorld.orange_purple_slime_grass, TinkerWorld.blue_magma_slime_grass, TinkerWorld.purple_magma_slime_grass, TinkerWorld.orange_magma_slime_grass, TinkerWorld.blue_slime_leaves, TinkerWorld.purple_slime_leaves, TinkerWorld.orange_slime_leaves, TinkerWorld.blue_slime_fern, TinkerWorld.purple_slime_fern, TinkerWorld.orange_slime_fern, TinkerWorld.blue_slime_tall_grass, TinkerWorld.purple_slime_tall_grass, TinkerWorld.orange_slime_tall_grass);

    super.init();
  }

  private int getSlimeColorByPos(BlockPos pos, FoliageType type, BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
