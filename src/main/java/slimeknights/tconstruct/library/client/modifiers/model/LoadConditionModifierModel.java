package slimeknights.tconstruct.library.client.modifiers.model;

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
 * Datagen helper for making conditional {@link ModifierModel}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
public record LoadConditionModifierModel(ModifierModel ifTrue, ModifierModel ifFalse, ICondition... conditions) implements ModifierModel, ConditionalObject<ModifierModel> {
  public LoadConditionModifierModel(ModifierModel ifTrue, ICondition... conditions) {
    this(ifTrue, ModifierModel.EMPTY, conditions);
  }

  @Override
  public RecordLoadable<? extends ModifierModel> getLoader() {
    return ModifierModel.LOADER.getConditionalLoader();
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {}

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {}
}
