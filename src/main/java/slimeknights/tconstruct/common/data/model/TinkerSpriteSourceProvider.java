package slimeknights.tconstruct.common.data.model;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import slimeknights.tconstruct.TConstruct;

/**
 * Provider to stitch textures from additional folders
 */
public class TinkerSpriteSourceProvider extends SpriteSourceProvider {
  public TinkerSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, TConstruct.MOD_ID);
  }

  @Override
  protected void addSources() {
    atlas(BLOCKS_ATLAS)
      // We load our fluid textures from here
      .addSource(directory("fluid"))
      // patterns load from this directory
      .addSource(directory("gui/modifiers"))
      // we typically use this directory for modifier icons that are not items nor blocks
      .addSource(directory("gui/tinker_pattern"));
  }

  /** Creates a directory lister where the source matches the prefix. */
  private static DirectoryLister directory(String path) {
    return new DirectoryLister(path, path + '/');
  }
}
