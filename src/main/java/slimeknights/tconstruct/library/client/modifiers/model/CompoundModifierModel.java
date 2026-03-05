package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.data.loadable.ErrorFactory;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model that composes multiple modifier models together. */
public record CompoundModifierModel(List<ModifierModel> models) implements ModifierModel {
  public static final Loadable<List<ModifierModel>> LIST_LOADABLE = ModifierModel.LOADER.list(2);
  public static final RecordLoadable<CompoundModifierModel> LOADER = RecordLoadable.create(LIST_LOADABLE.requiredField("models", CompoundModifierModel::models), CompoundModifierModel::new)
    .validate((model, error) -> {
      // if parsing from JSON, discard any empty models from the list
      if (error == ErrorFactory.JSON_SYNTAX_ERROR) {
        return new CompoundModifierModel(filter(model.models));
      }
      return model;
    });

  /** Filters the modifier list */
  private static List<ModifierModel> filter(List<ModifierModel> models) {
    return models.stream().filter(model -> model != ModifierModel.EMPTY).toList();
  }

  /** Creates a model for the given list of models */
  public static ModifierModel create(List<ModifierModel> models) {
    models = filter(models);
    if (models.isEmpty()) {
      return ModifierModel.EMPTY;
    }
    if (models.size() == 1) {
      return models.get(0);
    }
    return new CompoundModifierModel(models);
  }

  @Override
  public RecordLoadable<CompoundModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    for (ModifierModel model : this.models) {
      model.validate(spriteGetter);
    }
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    Object[] cacheKey = new Object[this.models.size()];
    boolean nonNull = false;
    for (int i = 0; i < this.models.size(); i++) {
      Object key = this.models.get(i).getCacheKey(tool, modifier);
      if (key != null) {
        nonNull = true;
        cacheKey[i] = key;
      }
    }
    return nonNull ? cacheKey[0] : null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    for (ModifierModel model : this.models) {
      model.addQuads(tool, modifier, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
      startTintIndex += model.getTintIndexes();
    }
  }

  @Override
  public int getTintIndexes() {
    int count = 0;
    for (ModifierModel model : this.models) {
      count += model.getTintIndexes();
    }
    return count;
  }

  @Override
  public int getTint(IToolStackView tool, ModifierEntry entry, int index) {
    for (ModifierModel model : this.models) {
      int count = model.getTintIndexes();
      if (index < count) {
        return model.getTint(tool, entry, index);
      }
      index -= count;
    }
    return -1;
  }
}
