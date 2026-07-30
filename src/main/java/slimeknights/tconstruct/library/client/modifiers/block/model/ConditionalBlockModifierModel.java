package slimeknights.tconstruct.library.client.modifiers.block.model;

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
public record ConditionalBlockModifierModel(ModifierCondition<IToolStackView> condition, BlockModifierModel nested) implements BlockModifierModel, ConditionalModule<IToolStackView> {
  public static final RecordLoadable<ConditionalBlockModifierModel> LOADER = RecordLoadable.create(
    ModifierCondition.TOOL_FIELD,
    BlockModifierModel.LOADER.requiredField("model", ConditionalBlockModifierModel::nested),
    ConditionalBlockModifierModel::new);

  @Override
  public RecordLoadable<ConditionalBlockModifierModel> getLoader() {
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
}
