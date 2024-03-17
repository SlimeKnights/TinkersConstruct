package slimeknights.tconstruct.world.client;

import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.world.block.FoliageType;

import java.io.IOException;

/**
 * Color reload listener for all slime foliage types
 */
public class SlimeColorReloadListener extends SimplePreparableReloadListener<int[]> {
  private final FoliageType color;
  private final ResourceLocation path;
  public SlimeColorReloadListener(FoliageType color) {
    this.color = color;
    this.path = TConstruct.getResource("textures/colormap/" + color.getSerializedName() + "_grass_color.png");
  }

  /**
   * Performs any reloading that can be done off-thread, such as file IO
   */
  @Override
  protected int[] prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
    if (!ModLoader.isLoadingStateValid()) {
      return new int[0];
    }
    try {
      return LegacyStuffWrapper.getPixels(resourceManager, path);
    } catch (IOException ioexception) {
      TConstruct.LOG.error("Failed to load slime colors", ioexception);
      return new int[0];
    }
  }

  @Override
  protected void apply(int[] buffer, ResourceManager resourceManager, ProfilerFiller profiler) {
    if (buffer.length != 0) {
      SlimeColorizer.setGrassColor(color, buffer);
    }
  }
}
