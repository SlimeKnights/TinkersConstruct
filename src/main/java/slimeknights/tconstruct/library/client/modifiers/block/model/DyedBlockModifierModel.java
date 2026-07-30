package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.QuadTransformers;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Modifier model that applies a dye color (from persistent data) to a nested
 * model.
 */
public class DyedBlockModifierModel implements BlockModifierModel {
  public static final RecordLoadable<DyedBlockModifierModel> LOADER = RecordLoadable.create(
      BlockModifierModel.PARSER.requiredField("model", m -> m.modelRef),
      DyedBlockModifierModel::new);

  private final Either<BlockModifierModel, ResourceLocation> modelRef;
  @Nullable
  private BlockModifierModel resolvedModel;

  public DyedBlockModifierModel(Either<BlockModifierModel, ResourceLocation> modelRef) {
    this.modelRef = modelRef;
  }

  /** Gets the resolved inner model. */
  private BlockModifierModel getModel() {
    if (resolvedModel == null) {
      resolvedModel = modelRef.map(
          model -> model,
          loc -> BlockModifierModelManager.INSTANCE.getModel(loc));
    }
    return resolvedModel;
  }

  /** Cache key combining the wrapped model's key and the dye color. */
  private record CacheKey(Object modelKey, int color) {
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    IModDataView data = tool.getPersistentData();
    if (!data.contains(entry.getId(), Tag.TAG_INT))
      return null;
    int color = 0xFF000000 | data.getInt(entry.getId());
    Object modelKey = getModel().getCacheKey(tool, entry);
    return new CacheKey(modelKey, color);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    getModel().validate(spriteGetter);
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter,
      Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer,
      @Nullable ItemLayerPixels pixels) {
    IModDataView data = tool.getPersistentData();
    if (!data.contains(modifier.getId(), Tag.TAG_INT))
      return;
    int color = 0xFF000000 | data.getInt(modifier.getId());
    getModel().addQuads(tool, modifier, spriteGetter, transforms, isLarge, startTintIndex, quads -> {
      quadConsumer.accept(ColoredBlockModel.applyColorQuadTransformer(color).process(new ArrayList<>(quads)));
    }, pixels);
  }
}
