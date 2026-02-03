package slimeknights.tconstruct.library.client.data.material;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.client.data.GenericTextureGenerator;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.util.DataGenSpriteReader;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/** Generator transforming the trim palette using our material generators */
public class TrimMaterialPaletteGenerator extends GenericTextureGenerator {
  private static final String PALETTE_FOLDER = "trims/color_palettes";
  private static final String PALETTE_TEXTURES = "textures/" + PALETTE_FOLDER;

  private final String name;
  private final MaterialId[] materials;
  private final AbstractMaterialSpriteProvider materialProvider;
  public TrimMaterialPaletteGenerator(PackOutput packOutput, String name, ExistingFileHelper existingFileHelper, AbstractMaterialSpriteProvider materialProvider, MaterialId... materials) {
    super(packOutput, existingFileHelper, "");
    this.name = name;
    this.materialProvider = materialProvider;
    this.materials = materials;
  }

  /** Gets the sprite transformer for the given material */
  protected ISpriteTransformer getTransformer(MaterialId material) {
    return Objects.requireNonNull(materialProvider.getMaterialInfo(material), "Missing material provider " + material).getTransformer();
  }

  @SuppressWarnings("removal")
  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    assert existingFileHelper != null;
    DataGenSpriteReader spriteReader = new DataGenSpriteReader(existingFileHelper, PALETTE_TEXTURES);
    try {
      // create JSON of all materials for compat with trimmed
      JsonObject trimmedJson = new JsonObject();
      JsonObject values = new JsonObject();
      for (MaterialId material : materials) {
        values.addProperty(material.withPrefix(PALETTE_FOLDER + '/').toString(), material.getSuffix());
      }
      trimmedJson.add("pairs", values);

      NativeImage original = spriteReader.read(new ResourceLocation("trim_palette"));
      return allOf(Stream.concat(
        Stream.of(saveJson(cache, new ResourceLocation("trimmed", "maps/unchecked/custom_trim_material_permutations"), trimmedJson)),
        Arrays.stream(materials).map(
        material -> saveImage(cache, material.withPrefix(PALETTE_TEXTURES + '/'), getTransformer(material).transformCopy(original, false)))))
        .thenRunAsync(spriteReader::closeAll);
    } catch (IOException ex) {
      return CompletableFuture.failedFuture(ex);
    }
  }

  @Override
  public String getName() {
    return name + " Material Trim Palette Generator";
  }
}
