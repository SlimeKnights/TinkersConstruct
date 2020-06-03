package slimeknights.tconstruct.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.entity.ToolEntities;
import slimeknights.tconstruct.entity.WorldEntities;
import slimeknights.tconstruct.library.client.renderer.BlueSlimeRenderer;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;
import slimeknights.tconstruct.world.block.SlimeLeavesBlock;
import slimeknights.tconstruct.world.block.SlimeTallGrassBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.client.slime.BlueColorReloadListener;
import slimeknights.tconstruct.world.client.slime.OrangeColorReloadListener;
import slimeknights.tconstruct.world.client.slime.PurpleColorReloadListener;

import javax.annotation.Nullable;

@EventBusSubscriber(modid=TConstruct.modID, value=Dist.CLIENT, bus=Bus.MOD)
public class WorldClientEvents {

  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  static {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft != null) {
      IResourceManager iManager = Minecraft.getInstance().getResourceManager();
      if (iManager instanceof IReloadableResourceManager) {
        IReloadableResourceManager reloadable = (IReloadableResourceManager)iManager;
        reloadable.addReloadListener(new BlueColorReloadListener());
        reloadable.addReloadListener(new PurpleColorReloadListener());
        reloadable.addReloadListener(new OrangeColorReloadListener());
      }
    }
  }

  @SubscribeEvent
  public static void clientSetup(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(WorldEntities.blue_slime_entity, BlueSlimeRenderer.BLUE_SLIME_FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(ToolEntities.indestructible_item, manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
  }

  @SubscribeEvent
  public static void registerBlockColors(ColorHandlerEvent.Block event) {
    final BlockColors blockColors = event.getBlockColors();

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state.getBlock() instanceof SlimeGrassBlock) {
        SlimeGrassBlock slimeGrassBlock = (SlimeGrassBlock) state.getBlock();
        return getSlimeColorByPos(blockPos, slimeGrassBlock.getFoliageType(), null);
      }

      MaterialColor materialcolor = state.getMaterialColor(reader, blockPos);
      return materialcolor != null ? materialcolor.colorValue : -1;

    }, WorldBlocks.blue_vanilla_slime_grass, WorldBlocks.purple_vanilla_slime_grass, WorldBlocks.orange_vanilla_slime_grass, WorldBlocks.blue_green_slime_grass, WorldBlocks.purple_green_slime_grass, WorldBlocks.orange_green_slime_grass, WorldBlocks.blue_blue_slime_grass, WorldBlocks.purple_blue_slime_grass, WorldBlocks.orange_blue_slime_grass, WorldBlocks.blue_purple_slime_grass, WorldBlocks.purple_purple_slime_grass, WorldBlocks.orange_purple_slime_grass, WorldBlocks.blue_magma_slime_grass, WorldBlocks.purple_magma_slime_grass, WorldBlocks.orange_magma_slime_grass);

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state.getBlock() instanceof SlimeLeavesBlock) {
        SlimeLeavesBlock slimeLeavesBlock = (SlimeLeavesBlock) state.getBlock();
        return getSlimeColorByPos(blockPos, slimeLeavesBlock.getFoliageType(), SlimeColorizer.LOOP_OFFSET);
      }

      MaterialColor materialColor = state.getMaterialColor(reader, blockPos);
      return materialColor != null ? materialColor.colorValue : -1;
    }, WorldBlocks.blue_slime_leaves, WorldBlocks.purple_slime_leaves, WorldBlocks.orange_slime_leaves);

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state.getBlock() instanceof SlimeTallGrassBlock) {
        SlimeTallGrassBlock slimeTallGrassBlock = (SlimeTallGrassBlock) state.getBlock();
        return getSlimeColorByPos(blockPos, slimeTallGrassBlock.getFoliageType(), null);
      }

      MaterialColor materialColor = state.getMaterialColor(reader, blockPos);
      return materialColor != null ? materialColor.colorValue : -1;
    }, WorldBlocks.blue_slime_fern, WorldBlocks.purple_slime_fern, WorldBlocks.orange_slime_fern, WorldBlocks.blue_slime_tall_grass, WorldBlocks.purple_slime_tall_grass, WorldBlocks.orange_slime_tall_grass);

    blockColors.register((state, reader, blockPos, tintIndex) -> {
      if (state.getBlock() instanceof SlimeVineBlock) {
        SlimeVineBlock slimeVineBlock = (SlimeVineBlock) state.getBlock();
        return getSlimeColorByPos(blockPos, slimeVineBlock.getFoliageType(), SlimeColorizer.LOOP_OFFSET);
      }

      MaterialColor materialColor = state.getMaterialColor(reader, blockPos);
      return materialColor != null ? materialColor.colorValue : -1;
    }, WorldBlocks.purple_slime_vine, WorldBlocks.purple_slime_vine_middle, WorldBlocks.purple_slime_vine_end, WorldBlocks.blue_slime_vine, WorldBlocks.blue_slime_vine_middle, WorldBlocks.blue_slime_vine_end);
  }

  @SubscribeEvent
  public static void registerItemColors(ColorHandlerEvent.Item event) {
    final BlockColors blockColors = event.getBlockColors();
    event.getItemColors().register((itemStack, tintIndex) -> {
                                         BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().getDefaultState();
                                         return blockColors.getColor(blockstate, (ILightReader) null, (BlockPos) null, tintIndex);
                                       }, WorldBlocks.blue_vanilla_slime_grass, WorldBlocks.purple_vanilla_slime_grass, WorldBlocks.orange_vanilla_slime_grass, WorldBlocks.blue_green_slime_grass, WorldBlocks.purple_green_slime_grass, WorldBlocks.orange_green_slime_grass, WorldBlocks.blue_blue_slime_grass, WorldBlocks.purple_blue_slime_grass, WorldBlocks.orange_blue_slime_grass, WorldBlocks.blue_purple_slime_grass, WorldBlocks.purple_purple_slime_grass, WorldBlocks.orange_purple_slime_grass, WorldBlocks.blue_magma_slime_grass, WorldBlocks.purple_magma_slime_grass, WorldBlocks.orange_magma_slime_grass, WorldBlocks.blue_slime_leaves, WorldBlocks.purple_slime_leaves, WorldBlocks.orange_slime_leaves, WorldBlocks.blue_slime_fern, WorldBlocks.purple_slime_fern, WorldBlocks.orange_slime_fern, WorldBlocks.blue_slime_tall_grass, WorldBlocks.purple_slime_tall_grass, WorldBlocks.orange_slime_tall_grass, WorldBlocks.purple_slime_vine, WorldBlocks.purple_slime_vine_middle, WorldBlocks.purple_slime_vine_end,
                                       WorldBlocks.blue_slime_vine, WorldBlocks.blue_slime_vine_middle, WorldBlocks.blue_slime_vine_end);
  }

  private static int getSlimeColorByPos(@Nullable BlockPos pos, FoliageType type, @Nullable BlockPos add) {
    if (pos == null) {
      return SlimeColorizer.getColorStatic(type);
    }
    if (add != null) {
      pos = pos.add(add);
    }

    return SlimeColorizer.getColorForPos(pos, type);
  }
}
