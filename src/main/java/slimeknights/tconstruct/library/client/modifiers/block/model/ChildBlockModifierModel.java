package slimeknights.tconstruct.library.client.modifiers.block.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.QuadTransformers;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class ChildBlockModifierModel implements TransformableBlockModifierModel, ParentModel {
  public static final Loadable<JsonObject> JSON_LOADABLE = new Loadable<>() {

    @Override
    public JsonObject decode(FriendlyByteBuf buffer, TypedMap context) {
      throw new UnsupportedOperationException("Unimplemented method 'decode'");
    }

    @Override
    public void encode(FriendlyByteBuf buffer, JsonObject value) {
      throw new UnsupportedOperationException("Unimplemented method 'encode'");
    }

    @Override
    public JsonObject convert(JsonElement element, String key, TypedMap context) {
      return element.getAsJsonObject();
    }

    @Override
    public JsonElement serialize(JsonObject object) {
      return object;
    }
  };
  public static final RecordLoadable<ChildBlockModifierModel> LOADER = RecordLoadable.create(
      BlockModifierModel.PARSER.requiredField("parent", ChildBlockModifierModel::parent),
      JSON_LOADABLE.requiredField("override", ChildBlockModifierModel::override),
      COLOR_FIELD, LUMINOSITY_FIELD, TRANSFORM_FIELD,
      ChildBlockModifierModel::create);

  private final Either<BlockModifierModel, ResourceLocation> parent;
  private final JsonObject override;
  private final int color;
  private final int luminosity;
  @Nonnull
  private final Transformation transform;
  @Nullable
  private ParentModel resolvedParent;

  private static ChildBlockModifierModel create(Either<BlockModifierModel, ResourceLocation> parent, JsonObject json,
      int color, int luminosity, Transformation transform) {
    return new ChildBlockModifierModel(parent, json, color, luminosity, transform);
  }

  /** Resolves the parent model lazily */
  private ParentModel getResolvedParent() {
    if (resolvedParent == null) {
      BlockModifierModel model = parent.map(Function.identity(), BlockModifierModelManager.INSTANCE::getModel);
      if (model == null) {
        ResourceLocation id = parent.right().orElse(null);
        throw new IllegalArgumentException("Parent model " + id + " not found");
      }
      if (!(model instanceof ParentModel pm))
        throw new IllegalArgumentException("Parent model " + model + " does not support parenting");
      resolvedParent = pm.mergeChild(override);
    }
    return resolvedParent;
  }

  @Override
  public RecordLoadable<ChildBlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    ParentModel resolved = getResolvedParent();
    if (resolved != null) {
      resolved.validate(spriteGetter);
    }
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge,int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    ParentModel resolved = getResolvedParent();
    if (resolved == null)
      return;
    Transformation combined = transforms.compose(this.transform);
    Consumer<Collection<BakedQuad>> newQuadConsumer = quadConsumer;
    if (color != -1 || luminosity != 0) {
      newQuadConsumer = quads -> {
        quadConsumer.accept(ColoredBlockModel.applyColorQuadTransformer(color).andThen(QuadTransformers.settingEmissivity(luminosity)).process(new ArrayList<>(quads)));
      };
    }
    resolved.addQuads(tool, modifier, spriteGetter, combined, isLarge, startTintIndex, newQuadConsumer, pixels);
  }

  @Override
  public ParentModel mergeChild(JsonObject obj) {
    ParentModel resolved = getResolvedParent();
    if (resolved != null) {
      return new ChildBlockModifierModel(Either.left(resolved.mergeChild(obj)), override, color, luminosity, transform);
    }
    throw new IllegalStateException("Parent model not resolved");
  }
}
