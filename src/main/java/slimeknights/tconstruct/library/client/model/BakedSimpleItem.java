package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Based on {@link ItemLayerModel.BakedItemModel}
 * Implements culling for items in the GUI
 */
public class BakedSimpleItem implements IBakedModel {
  private final ImmutableList<BakedQuad> quads;
  private final TextureAtlasSprite particle;
  private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
  private final IBakedModel otherModel;
  private final boolean isCulled;
  private final ItemOverrideList overrides;

  public BakedSimpleItem(ImmutableList<BakedQuad> quads, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, IBakedModel original) {
      this(quads, original.getParticleTexture(), transforms, original.getOverrides(), null);
  }

  private BakedSimpleItem(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, ItemOverrideList overrides, @Nullable IBakedModel unculledModel) {
    this.quads = quads;
    this.particle = particle;
    this.transforms = transforms;
    this.overrides = overrides;
    if(unculledModel != null) {
      this.otherModel = unculledModel;
      this.isCulled = true;
    } else {
      ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
      for(BakedQuad quad : quads) {
        if(quad.getFace() == EnumFacing.SOUTH) {
          builder.add(quad);
        }
      }
      this.otherModel = new BakedSimpleItem(builder.build(), particle, transforms, overrides, this);
      isCulled = false;
    }
  }

  public boolean isAmbientOcclusion() { return true; }
  public boolean isGui3d() { return false; }
  public boolean isBuiltInRenderer() { return false; }
  public TextureAtlasSprite getParticleTexture() { return particle; }
  public ItemOverrideList getOverrides() { return overrides; }
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
    if(side == null) {
      return quads;
    }
    return ImmutableList.of();
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
    Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, transforms, type);
    if(type == ItemCameraTransforms.TransformType.GUI && !isCulled && pair.getRight() == null) {
      return Pair.of(otherModel, null);
    } else if(type != ItemCameraTransforms.TransformType.GUI && isCulled) {
      return Pair.of(otherModel, pair.getRight());
    }
    return pair;
  }
}
