package slimeknights.tconstruct.library.client.modifiers;

import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.model.SimpleModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default modifier model loader, loads a single texture from the standard path.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.modifiers.modules}
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class NormalModifierModel implements SimpleModifierModel {
  protected static final LoadableField<Integer, NormalModifierModel> COLOR_FIELD = ColorLoadable.ALPHA.defaultField("color", false, m -> m.color);
  protected static final LoadableField<Integer, NormalModifierModel> LUMINOSITY_FIELD = IntLoadable.range(0, 15).defaultField("luminosity", 0, false, m -> m.luminosity);
  public static final RecordLoadable<NormalModifierModel> LOADER = RecordLoadable.create(TEXTURE_FIELD, LARGE_TEXTURE_FIELD, COLOR_FIELD, LUMINOSITY_FIELD, NormalModifierModel::new);
  /** @deprecated legacy system, use {@link #LOADER */
  @Deprecated
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = new Unbaked(-1, 0);

  /** Textures to show */
  @Nullable
  private final Material small;
  @Nullable
  private final Material large;
  /** Color to apply to the texture */
  private final int color;
  /** Luminosity to apply to the texture */
  private final int luminosity;

  public NormalModifierModel(@Nullable Material smallTexture, @Nullable Material largeTexture) {
    this(smallTexture, largeTexture, -1, 0);
  }

  @Override
  public RecordLoadable<? extends NormalModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    if (small != null) {
      spriteGetter.apply(small);
    }
    if (large != null) {
      spriteGetter.apply(large);
    }
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry entry, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Material spriteName = isLarge ? large : small;
    if (spriteName != null) {
      quadConsumer.accept(MantleItemLayerModel.getQuadsForSprite(color, -1, spriteGetter.apply(spriteName), transforms, luminosity, pixels));
    }
  }

  private record Unbaked(int color, int luminosity) implements IUnbakedModifierModel {
    @Nullable
    @Override
    public IBakedModifierModel forTool(Function<String,Material> smallGetter, Function<String,Material> largeGetter) {
      Material smallTexture = smallGetter.apply("");
      Material largeTexture = largeGetter.apply("");
      if (smallTexture != null || largeTexture != null) {
        return new NormalModifierModel(smallTexture, largeTexture, color, luminosity);
      }
      return null;
    }

    @Override
    public IUnbakedModifierModel configure(JsonObject data) {
      // parse the two keys, if we ended up with something new create an instance
      int color = COLOR_FIELD.get(data);
      int luminosity = LUMINOSITY_FIELD.get(data);
      if (color != this.color || luminosity != this.luminosity) {
        return new Unbaked(color, luminosity);
      }
      return this;
    }
  }
}
