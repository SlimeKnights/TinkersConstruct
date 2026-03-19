package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model that only shows if the given condition passes */
public record ConditionalModifierModel(ModifierCondition<IToolStackView> condition, ModifierModel nested) implements ModifierModel, ConditionalModule<IToolStackView> {
  public static final RecordLoadable<ConditionalModifierModel> LOADER = RecordLoadable.create(
    ModifierCondition.TOOL_FIELD,
    ModifierModel.LOADER.requiredField("model", ConditionalModifierModel::nested),
    ConditionalModifierModel::new);

  @Override
  public RecordLoadable<ConditionalModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    nested.validate(spriteGetter);
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    if (condition.matches(tool, modifier)) {
      return nested.getCacheKey(tool, modifier);
    }
    return null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    if (condition.matches(tool, modifier)) {
      nested.addQuads(tool, modifier, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
    }
  }

  @Override
  public int getTintIndexes() {
    return nested.getTintIndexes();
  }

  @Override
  public int getTint(IToolStackView tool, ModifierEntry entry, int index) {
    // if we are even asking, means condition matches
    return nested.getTint(tool, entry, index);
  }
}
