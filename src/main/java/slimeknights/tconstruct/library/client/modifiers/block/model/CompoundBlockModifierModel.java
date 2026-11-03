package slimeknights.tconstruct.library.client.modifiers.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Modifier model that composes multiple modifier models together. */
public record CompoundBlockModifierModel(List<BlockModifierModel> models) implements BlockModifierModel {
  public static final Loadable<List<BlockModifierModel>> LIST_LOADABLE = BlockModifierModelLoadable.DEFAULT.list(1);
  public static final RecordLoadable<CompoundBlockModifierModel> LOADER = RecordLoadable.create(LIST_LOADABLE.requiredField("models", CompoundBlockModifierModel::models), CompoundBlockModifierModel::new);

  /** Creates a model for the given list of models with no color or transform. */
  public static CompoundBlockModifierModel create(List<BlockModifierModel> models) {
    return new CompoundBlockModifierModel(models);
  }

  @Override
  public RecordLoadable<CompoundBlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
    for (BlockModifierModel model : models()) {
      model.validate();
    }
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    List<Object> cacheKey = new ArrayList<>();
    boolean nonNull = false;
    for (int i = 0; i < models.size(); i++) {
      Object key = models.get(i).getCacheKey(tool, modifier);
      if (key != null) {
        nonNull = true;
        cacheKey.add(key);
      }
    }
    return nonNull ? cacheKey : null;
  }

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, SimpleBakedModel.Builder builder) {   
    for (BlockModifierModel model : models()) {
      model.addParts(tool, modifier, context, spriteGetter, quadTransformer, builder);
    }
  }
}
