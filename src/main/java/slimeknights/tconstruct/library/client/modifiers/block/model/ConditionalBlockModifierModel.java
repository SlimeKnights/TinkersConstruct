package slimeknights.tconstruct.library.client.modifiers.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

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
  public void validate() {
    nested.validate();
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
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, SimpleBakedModel.Builder builder) {
    if (condition.matches(tool, modifier)) {
      nested.addParts(tool, modifier, context, spriteGetter, quadTransformer, builder);
    }
  }
}
