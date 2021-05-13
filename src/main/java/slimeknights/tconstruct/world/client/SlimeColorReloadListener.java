package slimeknights.tconstruct.world.client;

import net.minecraft.client.resources.ColorMapLoader;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.io.IOException;

/**
 * Color reload listener for all slime foliage types
 */
public class SlimeColorReloadListener extends ReloadListener<int[]> {
  private final SlimeType color;
  private final ResourceLocation path;
  public SlimeColorReloadListener(SlimeType color) {
    this.color = color;
    this.path = Util.getResource("textures/colormap/" + color.getString() + "_grass_color.png");
  }

  /**
   * Performs any reloading that can be done off-thread, such as file IO
   */
  @Override
  protected int[] prepare(IResourceManager resourceManager, IProfiler profiler) {
    if (!ModLoader.isLoadingStateValid()) {
      return new int[0];
    }
    try {
      return ColorMapLoader.loadColors(resourceManager, path);
    } catch (IOException ioexception) {
      TConstruct.log.error("Failed to load slime colors", ioexception);
      return new int[0];
    }
  }

  @Override
  protected void apply(int[] buffer, IResourceManager resourceManager, IProfiler profiler) {
    if (buffer.length != 0) {
      SlimeColorizer.setGrassColor(color, buffer);
    }
  }
}
