package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.TransformationMatrix;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Default modifier model loader, loads a single texture from the standard path
 */
public class NormalModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = new Unbaked(-1, 0);

  /** Textures to show */
  private final RenderMaterial[] textures;
  /** Color to apply to the texture */
  private final int color;
  /** Luminosity to apply to the texture */
  private final int luminosity;
  /** Cache of quads */
  @SuppressWarnings("unchecked")
  private final ImmutableList<BakedQuad>[] quads = new ImmutableList[2];

  public NormalModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture, int color, int luminosity) {
    this.color = color;
    this.luminosity = luminosity;
    this.textures = new RenderMaterial[]{ smallTexture, largeTexture };
  }

  public NormalModifierModel(@Nullable RenderMaterial smallTexture, @Nullable RenderMaterial largeTexture) {
    this(smallTexture, largeTexture, -1, 0);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry entry, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    int index = isLarge ? 1 : 0;
    if (quads[index] == null) {
      if (textures[index] == null) {
        quads[index] = ImmutableList.of();
      } else {
        quads[index] = MantleItemLayerModel.getQuadsForSprite(color, -1, spriteGetter.apply(textures[index]), transforms, luminosity);
      }
    }
    return quads[index];
  }

  @RequiredArgsConstructor
  private static class Unbaked implements IUnbakedModifierModel {
    private final int color;
    private final int luminosity;

    @Nullable
    @Override
    public IBakedModifierModel forTool(Function<String,RenderMaterial> smallGetter, Function<String,RenderMaterial> largeGetter) {
      RenderMaterial smallTexture = smallGetter.apply("");
      RenderMaterial largeTexture = largeGetter.apply("");
      if (smallTexture != null || largeTexture != null) {
        return new NormalModifierModel(smallTexture, largeTexture, color, luminosity);
      }
      return null;
    }

    @Override
    public IUnbakedModifierModel configure(JsonObject data) {
      // parse the two keys, if we ended up with something new create an instance
      int color = JsonHelper.parseColor(JSONUtils.getString(data, "color", ""));
      int luminosity = JSONUtils.getInt(data, "luminosity");
      if (color != this.color || luminosity != this.luminosity) {
        return new Unbaked(color, luminosity);
      }
      return this;
    }
  }
}
