package slimeknights.tconstruct.library.client.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.GsonHelper;
import com.mojang.math.Transformation;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Modifier model for a modifier that changes its texture when broken
 */
public class BreakableModifierModel implements IBakedModifierModel {
  /** Constant unbaked model instance, as they are all the same */
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = new Unbaked(-1, 0);

  /** Textures for this model */
  private final Material[] sprites;
  /** Color to apply to the texture */
  private final int color;
  /** Luminosity to apply to the texture */
  private final int luminosity;

  public BreakableModifierModel(@Nullable Material normalSmall, @Nullable Material brokenSmall, @Nullable Material normalLarge, @Nullable Material brokenLarge, int color, int luminosity) {
    this.color = color;
    this.luminosity = luminosity;
    this.sprites = new Material[] {normalSmall, brokenSmall, normalLarge, brokenLarge};
  }

  public BreakableModifierModel(@Nullable Material normalSmall, @Nullable Material brokenSmall, @Nullable Material normalLarge, @Nullable Material brokenLarge) {
    this(normalSmall, brokenSmall, normalLarge, brokenLarge, -1, 0);
  }

  @Override
  public ImmutableList<BakedQuad> getQuads(IToolStackView tool, ModifierEntry entry, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, @Nullable ItemLayerPixels pixels) {
    // first get the cache index
    int index = (isLarge ? 2 : 0) | (tool.isBroken() ? 1 : 0);
    // then return the quads
    return MantleItemLayerModel.getQuadsForSprite(color, -1, spriteGetter.apply(sprites[index]), transforms, luminosity, pixels);
  }

  @RequiredArgsConstructor
  private static class Unbaked implements IUnbakedModifierModel {
    private final int color;
    private final int luminosity;

    @Nullable
    @Override
    public IBakedModifierModel forTool(Function<String,Material> smallGetter, Function<String,Material> largeGetter) {
      Material normalSmall = smallGetter.apply("");
      Material brokenSmall = smallGetter.apply("_broken");
      Material normalLarge = smallGetter.apply("");
      Material brokenLarge = smallGetter.apply("_broken");
      // we need both to exist for this to work
      if (normalSmall != null || brokenSmall != null || normalLarge != null || brokenLarge != null) {
        return new BreakableModifierModel(normalSmall, brokenSmall, normalLarge, brokenLarge, color, luminosity);
      }
      return null;
    }

    @Override
    public IUnbakedModifierModel configure(JsonObject data) {
      // parse the two keys, if we ended up with something new create an instance
      int color = JsonHelper.parseColor(GsonHelper.getAsString(data, "color", ""));
      int luminosity = GsonHelper.getAsInt(data, "luminosity");
      if (color != this.color || luminosity != this.luminosity) {
        return new Unbaked(color, luminosity);
      }
      return this;
    }
  }
}
