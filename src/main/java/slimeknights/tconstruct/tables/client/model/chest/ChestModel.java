package slimeknights.tconstruct.tables.client.model.chest;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ChestModel implements IModelGeometry<ChestModel> {

  private final List<BlockPart> elements;

  public ChestModel(List<BlockPart> list) {
    this.elements = list;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

    IModelBuilder<?> builder = IModelBuilder.of(owner, overrides, particle);

    for (BlockPart blockpart : elements) {
      for (Direction direction : blockpart.mapFaces.keySet()) {
        BlockPartFace blockpartface = blockpart.mapFaces.get(direction);
        TextureAtlasSprite textureAtlasSprite = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));

        if (blockpartface.cullFace == null) {
          builder.addGeneralQuad(BlockModel.makeBakedQuad(blockpart, blockpartface, textureAtlasSprite, direction, modelTransform, modelLocation));
        } else {
          builder.addFaceQuad(modelTransform.getRotation().rotateTransform(blockpartface.cullFace), BlockModel.makeBakedQuad(blockpart, blockpartface, textureAtlasSprite, direction, modelTransform, modelLocation));
        }
      }
    }

    return new ChestBakedModel(builder.build());
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    Set<RenderMaterial> textures = Sets.newHashSet();

    for (BlockPart part : elements) {
      for (BlockPartFace face : part.mapFaces.values()) {
        RenderMaterial texture = owner.resolveTexture(face.texture);
        if (Objects.equals(texture.getTextureLocation(), MissingTextureSprite.getLocation())) {
          missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
        }

        textures.add(texture);
      }
    }

    return textures;
  }
}
