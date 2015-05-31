package tconstruct.library.client.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.client.CustomTextureCreator;

public class MaterialModel implements IModel {

  private final ModelBlock model;

  public MaterialModel(ModelBlock model) {
    this.model = model;
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    if (model.getParentLocation() == null || model.getParentLocation().getResourcePath().startsWith("builtin/")) {
      return Collections
          .emptyList();
    }
    return Collections.singletonList(model.getParentLocation());
  }

  @Override
  public Collection<ResourceLocation> getTextures() {
    // setting parent here to make textures resolve properly
    model.parent = ModelHelper.DEFAULT_PARENT;

    ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

    // item-model. should be the standard.
    if (model.getRootModel().name.equals("generation marker")) {
      for (String s : (List<String>) ItemModelGenerator.LAYERS) {
        String r = model.resolveTextureName(s);
        ResourceLocation loc = new ResourceLocation(r);
        if (!r.equals(s)) {
          builder.add(loc);
        }
      }
    }
    for (String s : (Iterable<String>) model.textures.values()) {
      if (!s.startsWith("#")) {
        builder.add(new ResourceLocation(s));
      }
    }
    return builder.build();
  }

  @Override
  public IFlexibleBakedModel bake(IModelState state, VertexFormat format,
                                  Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    return bakeIt(state, format, bakedTextureGetter);
  }

  public BakedMaterialModel bakeIt(IModelState state, VertexFormat format,
                                   Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    // obtain the base model with the base texture
    IFlexibleBakedModel base = ModelHelper.bakeModelFromModelBlock(model, bakedTextureGetter);

    // turn it into a baked material-model
    BakedMaterialModel bakedMaterialModel = new BakedMaterialModel(base);

    // and generate the baked model for each material-variant we have for the base texture
    String baseTexture = base.getTexture().getIconName();
    Map<String, TextureAtlasSprite> sprites = CustomTextureCreator.sprites.get(baseTexture);

    for (Map.Entry<String, TextureAtlasSprite> entry : sprites.entrySet()) {
      model.textures.put("layer0", entry.getValue().getIconName()); // this is sadly needed so that the ItemModelGenerator creates the correct model
      IFlexibleBakedModel model2 = ModelHelper.bakeModelFromModelBlock(model, entry.getValue());

      bakedMaterialModel.addMaterialModel(TinkerRegistry.getMaterial(entry.getKey()), model2);
    }

    model.textures.put("layer0", baseTexture);

    return bakedMaterialModel;
  }

  @Override
  public IModelState getDefaultState() {
    return ModelRotation.X0_Y0;
  }
}
