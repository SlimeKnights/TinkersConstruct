package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
public class ToolModelGeometry implements IMultipartModelGeometry<ToolModelGeometry> {
  // TODO: switch from composite model to list of data instead. Composite is overcomplicated for what we really need
  private final ImmutableMap<String,Submodel> parts;

  @Override
  public Collection<? extends IModelGeometryPart> getParts() {
    return parts.values();
  }

  @Override
  public Optional<? extends IModelGeometryPart> getPart(String name) {
    return Optional.ofNullable(parts.get(name));
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
    for (Map.Entry<String, Submodel> part : parts.entrySet()) {
      Submodel submodel = part.getValue();
      if (!owner.getPartVisibility(submodel)) continue;
      bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
    }

    // place names in an array so we can maintain order
    String[] partNames = this.parts.keySet().toArray(new String[0]);
    IModelTransform transforms = owner.getCombinedTransform();
    return new ToolModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), bakedParts.build(), transforms, partNames);
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<RenderMaterial> textures = new HashSet<>();
    for (Submodel part : parts.values()) {
      textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
    }
    return textures;
  }
}
