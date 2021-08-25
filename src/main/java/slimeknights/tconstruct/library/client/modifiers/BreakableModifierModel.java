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
 * Modifier model for a modifier that changes its texture when broken
 */
public class BreakableModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = new Unbaked(-1, 0);

  /** Textures for this model */
  private final RenderMaterial[] sprites;
  /** Color to apply to the texture */
  private final int color;
  /** Luminosity to apply to the texture */
  private final int luminosity;
  /* Caches of the small quad list */
  @SuppressWarnings("unchecked")
  private final ImmutableList<BakedQuad>[] quadCache = new ImmutableList[4];

  public BreakableModifierModel(@Nullable RenderMaterial normalSmall, @Nullable RenderMaterial brokenSmall, @Nullable RenderMaterial normalLarge, @Nullable RenderMaterial brokenLarge, int color, int luminosity) {
    this.color = color;
    this.luminosity = luminosity;
    this.sprites = new RenderMaterial[] {normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  public BreakableModifierModel(@Nullable RenderMaterial normalSmall, @Nullable RenderMaterial brokenSmall, @Nullable RenderMaterial normalLarge, @Nullable RenderMaterial brokenLarge) {
    this(normalSmall, brokenSmall, normalLarge, brokenLarge, -1, 0);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IModifierToolStack tool, ModifierEntry entry, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, TransformationMatrix transforms, boolean isLarge) {
    // first get the cache index
    int index = (isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0);
    // if not cached, build
    if (quadCache[index] == null) {
      if (sprites[index] == null) {
        quadCache[index] = ImmutableList.of();
      } else {
        quadCache[index] = MantleItemLayerModel.getQuadsForSprite(color, -1, spriteGetter.apply(sprites[index]), transforms, luminosity);
      }
    }
    return quadCache[index];
  }

  @RequiredArgsConstructor
  private static class Unbaked implements IUnbakedModifierModel {
    private final int color;
    private final int luminosity;

    @Nullable
    @Override
    public IBakedModifierModel forTool(Function<String,RenderMaterial> smallGetter, Function<String,RenderMaterial> largeGetter) {
      RenderMaterial normalSmall = smallGetter.apply("");
      RenderMaterial brokenSmall = smallGetter.apply("_broken");
      RenderMaterial normalLarge = smallGetter.apply("");
      RenderMaterial brokenLarge = smallGetter.apply("_broken");
      // we need both to exist for this to work
      if (normalSmall != null || brokenSmall != null || normalLarge != null || brokenLarge != null) {
        return new BreakableModifierModel(normalSmall, brokenSmall, normalLarge, brokenLarge, color, luminosity);
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
