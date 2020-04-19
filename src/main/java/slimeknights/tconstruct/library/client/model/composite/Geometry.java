package slimeknights.tconstruct.library.client.model.composite;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class Geometry implements IMultipartModelGeometry<Geometry> {

  private final ImmutableMap<String, Submodel> parts;

  public Geometry(ImmutableMap<String, Submodel> parts) {
    this.parts = parts;
  }

  @Override
  public Collection<? extends IModelGeometryPart> getParts() {
    return parts.values();
  }

  @Override
  public Optional<? extends IModelGeometryPart> getPart(String name) {
    return Optional.ofNullable(parts.get(name));
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    Material particleLocation = owner.resolveTexture("particle");
    TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

    ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
    for (Map.Entry<String, Submodel> part : parts.entrySet()) {
      Submodel submodel = part.getValue();
      if (!owner.getPartVisibility(submodel))
        continue;
      bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
    }
    return new CompositeModel(owner.isShadedInGui(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
  }

  @Override
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<Material> textures = new HashSet<>();
    for (Submodel part : parts.values()) {
      textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
    }
    return textures;
  }
}
