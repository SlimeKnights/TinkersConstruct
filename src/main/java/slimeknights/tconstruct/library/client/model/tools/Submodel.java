package slimeknights.tconstruct.library.client.model.tools;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.client.model.SimpleModelBakeSettings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.client.render.model.ModelLoader.GENERATION_MARKER;

public class Submodel implements UnbakedModel, BakedModel {

  private final UnbakedModel model;
  private final ModelBakeSettings modelTransform;

  public Submodel(UnbakedModel model, ModelBakeSettings modelTransform) {
    this.model = model;
    this.modelTransform = modelTransform;
  }
//
//  @Override
//  public boolean isVanillaAdapter() {
//    return false;
//  }
//
//  @Override
//  public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
//    throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
//  }
//
//  @Override
//  public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
//    throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
//  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
    return Collections.emptyList();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean hasDepth() {
    return false;
  }

  @Override
  public boolean isSideLit() {
    return false;
  }

  @Override
  public boolean isBuiltin() {
    return false;
  }

  @Override
  public Sprite getSprite() {
    return null;
  }

  @Override
  public ModelTransformation getTransformation() {
    return null;
  }

  @Override
  public ModelOverrideList getOverrides() {
    return null;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return Collections.emptySet();
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    return model.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
  }

  @Nullable
  @Override
  public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    UnbakedModel unbakedModel = model;
    boolean uvLock = this.modelTransform.isShaded() || modelTransform.isShaded();
    ModelBakeSettings transform = new SimpleModelBakeSettings(this.modelTransform, modelTransform, uvLock);
    if (model instanceof JsonUnbakedModel) {
      JsonUnbakedModel blockmodel = (JsonUnbakedModel)model;
      if (blockmodel.getRootModel() == GENERATION_MARKER) {
        unbakedModel = new ItemModelGenerator().create(textureGetter, blockmodel);
      }
    }
    return unbakedModel.bake(loader, textureGetter, transform, modelId);
  }
}
