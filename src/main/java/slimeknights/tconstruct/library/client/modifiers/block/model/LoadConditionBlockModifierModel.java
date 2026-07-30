package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Datagen helper for making conditional {@link BlockModifierModel}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
public record LoadConditionBlockModifierModel(BlockModifierModel ifTrue, BlockModifierModel ifFalse, ICondition... conditions) implements BlockModifierModel, ConditionalObject<BlockModifierModel> {
  public LoadConditionBlockModifierModel(BlockModifierModel ifTrue, ICondition... conditions) {
    this(ifTrue, BlockModifierModel.EMPTY, conditions);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return BlockModifierModel.LOADER.getConditionalLoader();
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {}

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {}
}
