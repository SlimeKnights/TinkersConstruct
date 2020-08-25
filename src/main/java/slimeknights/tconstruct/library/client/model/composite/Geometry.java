package slimeknights.tconstruct.library.client.model.composite;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    RenderMaterial particleLocation = owner.resolveTexture("particle");
    TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

    ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
    for (Map.Entry<String, Submodel> part : parts.entrySet()) {
      Submodel submodel = part.getValue();
      if (!owner.getPartVisibility(submodel))
        continue;
      bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
    }
    IModelTransform transforms = owner.getCombinedTransform();
    // place names in an array so we can maintain order
    String[] partNames = this.parts.keySet().toArray(new String[0]);
    return new CompositeModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle, bakedParts.build(), transforms, new CompositeOverrides(partNames, transforms));
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<RenderMaterial> textures = new HashSet<>();
    for (Submodel part : parts.values()) {
      textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
    }
    return textures;
  }

  /**
   * Handles loading overrides for each of the contained submodels
   */
  private static final class CompositeOverrides extends ItemOverrideList {
    private final String[] partNames;
    private final IModelTransform originalTransform;
    private final Map<QuickHash, IBakedModel> cache;
    private static final ResourceLocation BAKE_LOCATION = new ResourceLocation("tconstruct:material_model");

    private CompositeOverrides(String[] partNames, IModelTransform transforms) {
      this.partNames = partNames;
      this.originalTransform = transforms;
      this.cache = new HashMap<>();
    }

    @Override
    public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      CompositeModel model = (CompositeModel) originalModel;
      ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
      // store all the baked models in an array to use as a hash key
      Object[] hashKey = new Object[partNames.length];
      for (int i = 0; i < partNames.length; i++) {
        String key = partNames[i];
        IBakedModel part = model.getPart(key);
        if (part != null) {
          // apply the overrides on the model
          IBakedModel override = part.getOverrides().getOverrideModel(part, stack, world, entity);
          // fallback to the untextured model if none
          if (override != null) {
            hashKey[i] = override;
            bakedParts.put(key, override);
          } else {
            hashKey[i] = part;
            bakedParts.put(key, part);
          }
        }
      }
      // skip overrides, we already have them
      return cache.computeIfAbsent(new QuickHash(hashKey), (key) -> new CompositeModel(model.isGui3d(), model.isSideLit(), model.isAmbientOcclusion(), model.getParticleTexture(), bakedParts.build(), originalTransform, this));
    }
  }

  /**
   * Hashes a list of objects as an array. This works as model overrides are cached so we can be sure of same instance
   * Shamelessly stolen from Mekenism
   */
  public static class QuickHash {
    private Object[] objs;
    private QuickHash(Object[] objs) {
      this.objs = objs;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(objs);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this)
        return true;
      return obj instanceof QuickHash && Arrays.deepEquals(objs, ((QuickHash) obj).objs);
    }
  }
}
