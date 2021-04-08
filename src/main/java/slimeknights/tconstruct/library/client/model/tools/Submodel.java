package slimeknights.tconstruct.library.client.model.tools;

import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.client.render.model.ModelLoader.GENERATION_MARKER;

@AllArgsConstructor
public class Submodel implements IModelGeometryPart {

  private final String name;
  private final UnbakedModel model;
  private final ModelBakeSettings modelTransform;

  @Override
  public String name() {
    return name;
  }

  @Override
  public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, Identifier modelLocation) {
    throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
  }

  public BakedModel bakeModel(ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, Identifier modelLocation) {
    UnbakedModel unbakedModel = model;
    boolean uvLock = this.modelTransform.isShaded() || modelTransform.isShaded();
    ModelBakeSettings transform = new ModelTransformComposition(this.modelTransform, modelTransform, uvLock);
    if (model instanceof JsonUnbakedModel) {
      JsonUnbakedModel blockmodel = (JsonUnbakedModel)model;
      if (blockmodel.getRootModel() == GENERATION_MARKER) {
        unbakedModel = new ItemModelGenerator().create(spriteGetter, blockmodel);
      }
    }
    return unbakedModel.bake(bakery, spriteGetter, transform, modelLocation);
  }

  @Override
  public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return model.getTextureDependencies(modelGetter, missingTextureErrors);
  }
}
