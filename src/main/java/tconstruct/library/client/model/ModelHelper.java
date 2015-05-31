package tconstruct.library.client.model;

import com.google.common.base.Charsets;
import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ModelHelper {

  // copy of the one in the ModelBakery
  public static final ModelBlock DEFAULT_PARENT;

  private static final ItemModelGenerator generator = new ItemModelGenerator();
  private static final FaceBakery faceBakery = new FaceBakery();

  /**
   * Loads a model from the given location
   *
   * @param location Usually something like "modid:models/mySuperAwesomeModel". Note that it contains the path but not
   *                 the file extension.
   * @return The modelblock deserialized from the data.
   */
  public static ModelBlock loadModelBlock(ResourceLocation location) throws IOException {
    IResource
        iresource =
        Minecraft.getMinecraft().getResourceManager()
            .getResource(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json"));
    Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);

    return ModelBlock.deserialize(reader);
  }

  public static ResourceLocation getModelLocation(ResourceLocation location) {
    return new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath() + ".json");
  }

  public static IFlexibleBakedModel bakeModelFromModelBlock(ModelBlock model,
                                                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    ModelBlock mb = generator.makeItemModel(Minecraft.getMinecraft().getTextureMapBlocks(), model);
    SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(mb));
    TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(mb.resolveTextureName("layer0")));
    builder.setTexture(sprite);

    for (Object o : mb.getElements()) {
      BlockPart blockpart = (BlockPart) o;
      for (Object o2 : blockpart.mapFaces.keySet()) {
        EnumFacing enumfacing = (EnumFacing) o2;
        BlockPartFace blockpartface = (BlockPartFace) blockpart.mapFaces.get(enumfacing);
        builder.addGeneralQuad(makeBakedQuad(blockpart, blockpartface, sprite, enumfacing, ModelRotation.X0_Y0, false));
      }
    }

    return new IFlexibleBakedModel.Wrapper(builder.makeBakedModel(), Attributes.DEFAULT_BAKED_FORMAT);
  }

  public static IFlexibleBakedModel bakeModelWithTexture(IFlexibleBakedModel model, TextureAtlasSprite newSprite) {
    SimpleBakedModel.Builder builder = (new SimpleBakedModel.Builder(model, newSprite));

    return new IFlexibleBakedModel.Wrapper(builder.makeBakedModel(), Attributes.DEFAULT_BAKED_FORMAT);
  }

  public static BakedQuad makeBakedQuad(BlockPart p_177589_1_, BlockPartFace p_177589_2_,
                                        TextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_,
                                        net.minecraftforge.client.model.ITransformation p_177589_5_,
                                        boolean p_177589_6_) {
    return faceBakery
        .makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_,
                       p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade);
  }


  static String getPartModelJSON(String texture) {
    return String.format("{"
                         + "    \"parent\": \"builtin/generated\","
                         + "    \"textures\": {"
                         + "        \"layer0\": \"%s\""
                         + "    }"
                         + "}"
        , texture);
  }

  static {
    DEFAULT_PARENT = ModelBlock.deserialize("{"
                                            + "\t\"elements\": [{\n"
                                            + "\t\t\"from\": [0, 0, 0],\n"
                                            + "\t\t\"to\": [16, 16, 16],\n"
                                            + "\t\t\"faces\": {\n"
                                            + "\t\t\t\"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}\n"
                                            + "\t\t}\n"
                                            + "\t}],\n"
                                            + "\t\n"
                                            + "    \"display\": {\n"
                                            + "        \"thirdperson\": {\n"
                                            + "            \"rotation\": [ 0, 90, -35 ],\n"
                                            + "            \"translation\": [ 0, 1.25, -3.5 ],\n"
                                            + "            \"scale\": [ 0.85, 0.85, 0.85 ]\n"
                                            + "        },\n"
                                            + "        \"firstperson\": {\n"
                                            + "            \"rotation\": [ 0, -135, 25 ],\n"
                                            + "            \"translation\": [ 0, 4, 2 ],\n"
                                            + "            \"scale\": [ 1.7, 1.7, 1.7 ]\n"
                                            + "        }\n"
                                            + "    }\n"
                                            + "}");
  }
}
