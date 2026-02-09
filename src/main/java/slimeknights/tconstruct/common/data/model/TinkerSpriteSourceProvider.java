package slimeknights.tconstruct.common.data.model;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.TrimModifierModel;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provider to stitch textures from additional folders
 */
public class TinkerSpriteSourceProvider extends SpriteSourceProvider {
  /** List of trim variants supported, must all exist in vanilla */
  private static final String[] TRIMS = {
    "coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide", "snout",
    "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host"
  };

  public TinkerSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, TConstruct.MOD_ID);
  }

  @SuppressWarnings("removal")
  @Override
  protected void addSources() {
    String paletteFolder = "trims/color_palettes/";
    String trimFolder = "trims/models/armor/";
    ResourceLocation trimPalette = new ResourceLocation(paletteFolder + "trim_palette");
    // map of material suffix to material paeltte for trims
    Map<String,ResourceLocation> materialMap = Arrays.stream(MaterialIds.TRIM_MATERIALS).collect(Collectors.toMap(id -> id.getNamespace() + "_" + id.getPath(), id -> id.withPrefix(paletteFolder)));

    SourceList blocks = atlas(BLOCKS_ATLAS)
      // We load our fluid textures from here
      .addSource(directory("fluid"))
      // patterns load from this directory
      .addSource(directory("gui/modifiers"))
      // we typically use this directory for modifier icons that are not items nor blocks
      .addSource(directory("gui/tinker_pattern"))
      // trim armor icons
      .addSource(new PalettedPermutations(
        List.of(TrimModifierModel.TRIM_TEXTURES),
        trimPalette, materialMap));
    // add untinted trim textures, we use them as fallbacks
    for (ResourceLocation name : TrimModifierModel.TRIM_TEXTURES) {
      blocks.addSource(new SingleFile(name, Optional.empty()));
    }
    // add armor trims in our materials
    atlas(new ResourceLocation("armor_trims"))
      .addSource(new PalettedPermutations(
        Arrays.stream(TRIMS).flatMap(name -> Stream.of(new ResourceLocation(trimFolder + name), new ResourceLocation(trimFolder + name + "_leggings"))).toList(),
        trimPalette, materialMap));
  }

  /** Creates a directory lister where the source matches the prefix. */
  private static DirectoryLister directory(String path) {
    return new DirectoryLister(path, path + '/');
  }
}
