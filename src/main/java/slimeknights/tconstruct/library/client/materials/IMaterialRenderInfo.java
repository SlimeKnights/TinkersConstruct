package slimeknights.tconstruct.library.client.materials;

import lombok.Getter;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Determines the type of texture used for rendering a specific material
 */
public interface IMaterialRenderInfo {
  /**
   * Gets the ID of this render info
   * @return  Render info unique ID
   */
  MaterialId getIdentifier();

  /**
   * Gets the color used to tint this model as an item colors handler
   * @return  Color used to tint this model
   */
  int getVertexColor();

  /**
   * Gets a list of textures that may be chosen for this render info
   * @param base  Base texture
   * @return  A list of textures to choose from
   */
  List<Material> getTextureChoices(Material base);

  /**
   * Gets all dependencies for this render info
   * @param textures  Texture consumer
   * @param base      Base texture, will be used to generate texture names
   */
  void getTextureDependencies(Consumer<Material> textures, Material base);

  /** Standard render information for tinting */
  class Default implements IMaterialRenderInfo {
    @Getter
    protected final MaterialId identifier;
    protected final ResourceLocation texture;
    protected final int color;

    public Default(MaterialId id, @Nullable ResourceLocation texture, int color) {
      this.identifier = id;
      this.texture = texture == null ? id : texture;
      this.color = color;
    }

    @Override
    public int getVertexColor() {
      return color;
    }

    @Override
    public List<Material> getTextureChoices(Material base) {
      return Arrays.asList(
        getMaterial(base.getTextureLocation(), texture),
        base);
    }

    @Override
    public void getTextureDependencies(Consumer<Material> textures, Material base) {
      textures.accept(getMaterial(base.getTextureLocation(), texture));
    }
  }

  /** Render information for a material with a falllback texture */
  class Fallback extends Default {
    private final ResourceLocation fallback;

    public Fallback(MaterialId identifier, ResourceLocation texture, ResourceLocation fallback, int color) {
      super(identifier, texture, color);
      this.fallback = fallback;
    }

    @Override
    public List<Material> getTextureChoices(Material base) {
      ResourceLocation location = base.getTextureLocation();
      return Arrays.asList(
        getMaterial(location, texture),
        getMaterial(location, fallback),
        base);
    }

    @Override
    public void getTextureDependencies(Consumer<Material> textures, Material base) {
      super.getTextureDependencies(textures, base);
      textures.accept(getMaterial(base.getTextureLocation(), fallback));
    }
  }

  /**
   * Gets a material for the given resource locations
   * @param texture   Texture path
   * @param material  Material ID
   * @return  Material instance
   */
  static Material getMaterial(ResourceLocation texture, ResourceLocation material) {
    return ModelLoaderRegistry.blockMaterial(new ResourceLocation(material.getNamespace(), texture.getPath() + "_" + material.getPath()));
  }
}
