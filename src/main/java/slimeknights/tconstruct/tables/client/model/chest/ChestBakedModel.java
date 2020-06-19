package slimeknights.tconstruct.tables.client.model.chest;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.tconstruct.tables.client.model.ModelProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChestBakedModel implements IDynamicBakedModel {

  private final IBakedModel internal;
  private final Map<Direction, IBakedModel> cache = Maps.newEnumMap(Direction.class);

  public ChestBakedModel(IBakedModel internal) {
    this.internal = internal;
  }

  @Nonnull
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
    Direction facingDirection = null;

    if (extraData.hasProperty(ModelProperties.DIRECTION)) {
      facingDirection = extraData.getData(ModelProperties.DIRECTION);
    }

    IBakedModel out = internal;
    if (facingDirection != null) {
      out = cache.computeIfAbsent(facingDirection, (facing) -> new TRSRBakedModel(internal, facing));
    }

    // the model returned by getActualModel should be a simple model with no special handling
    return internal.getQuads(state, side, rand, extraData);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return internal.isAmbientOcclusion();
  }

  @Override
  public boolean isAmbientOcclusion(BlockState state) {
    return internal.isAmbientOcclusion(state);
  }

  @Override
  public boolean isGui3d() {
    return internal.isGui3d();
  }

  @Override
  public boolean func_230044_c_() {
    return internal.func_230044_c_();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return internal.isBuiltInRenderer();
  }

  @Nonnull
  @Override
  @Deprecated
  public TextureAtlasSprite getParticleTexture() {
    return internal.getParticleTexture();
  }

  @Override
  public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
    return internal.getParticleTexture(data);
  }

  @Override
  public boolean doesHandlePerspectives() {
    return internal.doesHandlePerspectives();
  }

  @Override
  public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
    return internal.handlePerspective(cameraTransformType, mat);
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides() {
    return internal.getOverrides();
  }

  @Nonnull
  @Override
  public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
    return internal.getModelData(world, pos, state, tileData);
  }

  @Nonnull
  @Override
  @Deprecated
  public ItemCameraTransforms getItemCameraTransforms() {
    return internal.getItemCameraTransforms();
  }
}
