package slimeknights.tconstruct.library.client.data.material;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.client.data.GenericTextureGenerator;
import slimeknights.tconstruct.library.client.data.SpriteReader;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider.MaterialSpriteInfo;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider.PartSpriteInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Texture generator to generate textures for materials, supports adding a set of sprites to recolor, alongside a set of materials
 *
 * Note this only supports generating the crossproduct of materials and textures. If your addon adds both materials and tools, the best setup is having two generators:
 * <ul>
 *   <li>A generator adding all TiC and custom materials for your new sprites</li>
 *   <li>A generator adding all custom materials for TiC sprites</li>
 * </ul>
 * In case you need to divide into more than those two, it will be most efficient if each sprite is handled by only a single generator, so always split over sets of materials.
 */
public class MaterialPartTextureGenerator extends GenericTextureGenerator {
  /** Path to textures outputted by this generator */
  public static final String FOLDER = "textures/item/tool";
  private final SpriteReader spriteReader;
  /** Sprite provider */
  private final AbstractPartSpriteProvider partProvider;
  /** Materials to provide */
  private final AbstractMaterialSpriteProvider[] materialProviders;

  public MaterialPartTextureGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper, AbstractPartSpriteProvider spriteProvider, AbstractMaterialSpriteProvider... materialProviders) {
    super(generator, FOLDER);
    this.spriteReader = new SpriteReader(existingFileHelper, FOLDER);
    this.partProvider = spriteProvider;
    this.materialProviders = materialProviders;
  }

  @Override
  public String getName() {
    StringBuilder name = new StringBuilder();
    name.append("Material Part Generator - ");
    name.append(partProvider.getName());
    name.append(" - ");
    name.append(materialProviders[0].getName());
    for (AbstractMaterialSpriteProvider provider : materialProviders) {
      name.append(", ").append(provider.getName());
    }
    return name.toString();
  }

  @Override
  public void act(DirectoryCache cache) throws IOException {
    // ensure we have parts
    List<PartSpriteInfo> parts = partProvider.getSprites();
    if (parts.isEmpty()) {
      throw new IllegalStateException(partProvider.getName() + " has no parts, must have at least one part to generate");
    }

    // for each material list, generate sprites
    for (AbstractMaterialSpriteProvider materialProvider : materialProviders) {
      Collection<MaterialSpriteInfo> materials = materialProvider.getMaterials().values();
      if (materials.isEmpty()) {
        throw new IllegalStateException(materialProvider.getName() + " has no materials, must have at least one material to generate");
      }
      // want cross product of textures
      for (MaterialSpriteInfo material : materials) {
        for (PartSpriteInfo part : parts) {
          if (material.supportStatType(part.getStatType())) {
            generateSprite(cache, material, part);
          }
        }
      }
    }
    spriteReader.closeAll();
    partProvider.cleanCache();
  }

  /** Generates a sprite for the given material */
  private void generateSprite(DirectoryCache cache, MaterialSpriteInfo material, PartSpriteInfo part) {
    // first step: see if this sprite has already been generated, if so nothing to do
    // path format: pNamespace:pPath_mNamespace_mPath
    ResourceLocation partPath = part.getPath();
    ResourceLocation materialTexture = material.getTexture();
    ResourceLocation spritePath = new ResourceLocation(partPath.getNamespace(),
      partPath.getPath() + "_" + materialTexture.getNamespace() + "_" + materialTexture.getPath());

    // image does not exist? first step is to find a base image
    if (!spriteReader.exists(spritePath)) {
      NativeImage base = null;
      for (String fallback : material.getFallbacks()) {
        base = part.getTexture(spriteReader, fallback);
        if (base != null) {
          break;
        }
      }
      // no fallback existed, try the main one
      if (base == null) {
        base = part.getTexture(spriteReader, "");
      }
      if (base == null) {
        throw new IllegalStateException("Missing sprite at " + partPath.getNamespace() + ":item/tool/" + partPath.getPath() + ".png, cannot generate textures");
      }
      // successfully found a texture, now transform and save
      NativeImage transformed = material.getTransformer().transformCopy(base);
      saveImage(cache, spritePath, transformed);
    }
  }
}
