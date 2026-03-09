package slimeknights.tconstruct.common.data.model;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel.Armor;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.Arrays;
import java.util.HashMap;
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
  private static final String PALETTE_FOLDER = "trims/color_palettes/";
  private static final String TRIM_FOLDER = "trims/models/armor/";

  public TinkerSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, TConstruct.MOD_ID);
  }

  @SuppressWarnings("removal")
  @Override
  protected void addSources() {
    ResourceLocation trimPalette = new ResourceLocation(PALETTE_FOLDER + "trim_palette");
    // map of material suffix to material paeltte for trims
    Map<String,ResourceLocation> tinkerMaterials = Arrays.stream(MaterialIds.TRIM_MATERIALS).collect(Collectors.toMap(id -> id.getNamespace() + "_" + id.getPath(), id -> id.withPrefix(PALETTE_FOLDER)));
    Map<String,ResourceLocation> vanillaMaterials = new HashMap<>();
    addVanilla(vanillaMaterials, TrimMaterials.QUARTZ);
    addVanilla(vanillaMaterials, TrimMaterials.IRON);
    addVanilla(vanillaMaterials, TrimMaterials.NETHERITE);
    addVanilla(vanillaMaterials, TrimMaterials.REDSTONE);
    addVanilla(vanillaMaterials, TrimMaterials.COPPER);
    addVanilla(vanillaMaterials, TrimMaterials.GOLD);
    addVanilla(vanillaMaterials, TrimMaterials.EMERALD);
    addVanilla(vanillaMaterials, TrimMaterials.DIAMOND);
    addVanilla(vanillaMaterials, TrimMaterials.LAPIS);
    addVanilla(vanillaMaterials, TrimMaterials.AMETHYST);
    // custom armor "modifier" textures that use the trim materials
    List<ResourceLocation> customItemTrims = Stream.of("item/tool/armor/travelers/goggles/trim", "item/tool/armor/slime/wings_trim").map(TConstruct::getResource).toList();

    SourceList blocks = atlas(BLOCKS_ATLAS)
      // We load our fluid textures from here
      .addSource(directory("fluid"))
      // patterns load from this directory
      .addSource(directory("gui/modifiers"))
      // we typically use this directory for modifier icons that are not items nor blocks
      .addSource(directory("gui/tinker_pattern"))
      // trim armor icons
      .addSource(new PalettedPermutations(
        Stream.concat(Arrays.stream(Armor.values()).map(Armor::getRoot), customItemTrims.stream()).toList(),
        trimPalette, tinkerMaterials))
      // trim shield icons
      .addSource(new PalettedPermutations(customItemTrims, trimPalette, vanillaMaterials));
    // add untinted trim textures, we use them as fallbacks
    for (Armor armor : TrimModifierModel.Armor.values()) {
      blocks.addSource(new SingleFile(armor.getRoot(), Optional.empty()));
    }
    // add armor trims in our materials
    atlas(new ResourceLocation("armor_trims"))
      .addSource(new PalettedPermutations(
        Arrays.stream(TRIMS).flatMap(name -> Stream.of(new ResourceLocation(TRIM_FOLDER + name), new ResourceLocation(TRIM_FOLDER + name + "_leggings"))).toList(),
        trimPalette, tinkerMaterials));
  }

  /** Creates a directory lister where the source matches the prefix. */
  private static DirectoryLister directory(String path) {
    return new DirectoryLister(path, path + '/');
  }

  /** Adds a vanilla material to the map */
  private static void addVanilla(Map<String,ResourceLocation> map, ResourceKey<TrimMaterial> material) {
    ResourceLocation path = material.location();
    map.put(path.getPath(), path.withPrefix(PALETTE_FOLDER));
  }
}
