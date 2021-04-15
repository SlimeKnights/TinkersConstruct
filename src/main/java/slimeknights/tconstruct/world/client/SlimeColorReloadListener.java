package slimeknights.tconstruct.world.client;

import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import java.io.IOException;

/**
 * Color reload listener for all slime foliage types
 */
public class SlimeColorReloadListener extends SinglePreparationResourceReloadListener<int[]> {
  private final FoliageType color;
  private final Identifier path;
  public SlimeColorReloadListener(FoliageType color) {
    this.color = color;
    this.path = Util.getResource("textures/colormap/" + color.asString() + "_grass_color.png");
  }

  /**
   * Performs any reloading that can be done off-thread, such as file IO
   */
  @Override
  protected int[] prepare(ResourceManager resourceManager, Profiler profiler) {
    try {
      return RawTextureDataLoader.loadRawTextureData(resourceManager, path);
    } catch (IOException ioexception) {
      TConstruct.log.error("Failed to load slime colors", ioexception);
      return new int[0];
    }
  }

  @Override
  protected void apply(int[] buffer, ResourceManager resourceManager, Profiler profiler) {
    if (buffer.length != 0) {
//      SlimeColorizer.setGrassColor(color, buffer);
    }
  }
}
