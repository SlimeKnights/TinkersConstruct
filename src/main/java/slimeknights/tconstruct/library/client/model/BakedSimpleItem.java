package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.model.BakedSimple;

public class BakedSimpleItem extends BakedSimple {

  public BakedSimpleItem(ImmutableList<net.minecraft.client.renderer.block.model.BakedQuad> quads, ImmutableMap<net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType, net.minecraftforge.common.model.TRSRTransformation> transforms, net.minecraft.client.renderer.block.model.IBakedModel base) {
    super(quads, transforms, base);
  }

  public BakedSimpleItem(List<net.minecraft.client.renderer.block.model.BakedQuad> quads, ImmutableMap<net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType, net.minecraftforge.common.model.TRSRTransformation> transforms, net.minecraft.client.renderer.block.model.IBakedModel base) {
    super(quads, transforms, base);
  }

  public BakedSimpleItem(List<net.minecraft.client.renderer.block.model.BakedQuad> quads, ImmutableMap<net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType, net.minecraftforge.common.model.TRSRTransformation> transforms, net.minecraft.client.renderer.texture.TextureAtlasSprite particle, boolean ambientOcclusion, boolean isGui3d, net.minecraft.client.renderer.block.model.ItemOverrideList overrides) {
    super(quads, transforms, particle, ambientOcclusion, isGui3d, overrides);
  }

  @Nonnull
  @Override
  public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(net.minecraft.block.state.IBlockState state, net.minecraft.util.EnumFacing side, long rand) {
    if(side == null) {
      return super.getQuads(state, null, rand);
    }
    return ImmutableList.of();
  }
}
