package slimeknights.tconstruct.library.client.model.composite;

import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.client.renderer.model.ModelBakery.MODEL_GENERATED;

@AllArgsConstructor
public class Submodel implements IModelGeometryPart {

  private final String name;
  private final IUnbakedModel model;
  private final IModelTransform modelTransform;

  @Override
  public String name() {
    return name;
  }

  @Override
  public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
    throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
  }

  public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
    IUnbakedModel unbakedModel = model;
    boolean uvLock = this.modelTransform.isUvLock() || modelTransform.isUvLock();
    IModelTransform transform = new ModelTransformComposition(this.modelTransform, modelTransform, uvLock);
    if (model instanceof BlockModel) {
      BlockModel blockmodel = (BlockModel)model;
      if (blockmodel.getRootModel() == MODEL_GENERATED) {
        unbakedModel = new ItemModelGenerator().makeItemModel(spriteGetter, blockmodel);
      }
    }
    return unbakedModel.bakeModel(bakery, spriteGetter, transform, modelLocation);
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return model.getTextures(modelGetter, missingTextureErrors);
  }
}
