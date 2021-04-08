package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
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
  public BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, ModelOverrideList overrides, Identifier modelLocation) {
    ImmutableMap.Builder<String, BakedModel> bakedParts = ImmutableMap.builder();
    for (Map.Entry<String, Submodel> part : parts.entrySet()) {
      Submodel submodel = part.getValue();
      if (!owner.getPartVisibility(submodel)) continue;
      bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
    }

    // place names in an array so we can maintain order
    String[] partNames = this.parts.keySet().toArray(new String[0]);
    ModelBakeSettings transforms = owner.getCombinedTransform();
    return new ToolModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), bakedParts.build(), transforms, partNames);
  }

  @Override
  public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<SpriteIdentifier> textures = new HashSet<>();
    for (Submodel part : parts.values()) {
      textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
    }
    return textures;
  }
}
