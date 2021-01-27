package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.CompositeModel;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Extension of the Forge class to redirect the particle texture to the first submodel
 */
public class ToolModel extends CompositeModel {
  private final IBakedModel particleModel;

  @SuppressWarnings("ConstantConditions")
  private ToolModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, IBakedModel particleModel, ImmutableMap<String,IBakedModel> bakedParts, IModelTransform combinedTransform, CompositeOverrides overrides) {
    super(isGui3d, isSideLit, isAmbientOcclusion, null, bakedParts, combinedTransform, overrides);
    this.particleModel = particleModel;
  }

  public ToolModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, ImmutableMap<String,IBakedModel> bakedParts, IModelTransform combinedTransform, String[] partNames) {
    this(isGui3d, isSideLit, isAmbientOcclusion, bakedParts.get(partNames[0]), bakedParts, combinedTransform, new CompositeOverrides(partNames, combinedTransform));
  }

  @SuppressWarnings("deprecation")
  @Override
  public TextureAtlasSprite getParticleTexture() {
    return particleModel.getParticleTexture();
  }

  /**
   * Handles loading overrides for each of the contained submodels
   */
  private static final class CompositeOverrides extends ItemOverrideList {
    private final String[] partNames;
    private final IModelTransform originalTransform;
    private final Map<QuickHash, IBakedModel> cache;

    private CompositeOverrides(String[] partNames, IModelTransform transforms) {
      this.partNames = partNames;
      this.originalTransform = transforms;
      this.cache = new HashMap<>();
    }

    @Override
    public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      ToolModel model = (ToolModel) originalModel;
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
      // TODO: modifier model
      return cache.computeIfAbsent(new QuickHash(hashKey), (key) -> new ToolModel(model.isGui3d(), model.isSideLit(), model.isAmbientOcclusion(), model.particleModel, bakedParts.build(), originalTransform, this));
    }
  }

  /**
   * Hashes a list of objects as an array. This works as model overrides are cached so we can be sure of same instance
   * Shamelessly stolen from Mekenism
   */
  private static class QuickHash {
    private final Object[] objs;
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
