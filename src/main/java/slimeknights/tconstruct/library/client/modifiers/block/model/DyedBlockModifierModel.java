package slimeknights.tconstruct.library.client.modifiers.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.nbt.Tag;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

import java.util.function.Function;

/**
 * Modifier model that applies a dye color (from persistent data) to a nested
 * model.
 */
public record DyedBlockModifierModel(BlockModifierModel model) implements BlockModifierModel {
  public static final RecordLoadable<DyedBlockModifierModel> LOADER = RecordLoadable.create(
      BlockModifierModelLoadable.DEFAULT.requiredField("model", m -> m.model),
      DyedBlockModifierModel::new);


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
    Object modelKey = model.getCacheKey(tool, entry);
    if(modelKey == null)
      return null;
    return new CacheKey(modelKey, color);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
    model.validate();
  }

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, Builder builder) {
    IModDataView data = tool.getPersistentData();
    if (data.contains(modifier.getId(), Tag.TAG_INT)) {
      int color = 0xFF000000 | data.getInt(modifier.getId());
      quadTransformer = quadTransformer.andThen(QuadTransformers.applyingColor(color));
    }
    model.addParts(tool, modifier, context, spriteGetter, quadTransformer, builder);
  }

}
