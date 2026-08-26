package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model fetching a modifier entry from the tool and using it to show a nested model. */
public interface NestedModifierModel extends ModifierModel {
  /** Gets the nested model instance */
  ModifierModel nested();

  /** Gets the relevant modifier entry to use from the tool */
  ModifierEntry getEntry(IToolStackView tool);

  @Override
  default void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    nested().validate(spriteGetter);
  }

  @Nullable
  @Override
  default Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    ModifierEntry entry = getEntry(tool);
    if (entry.getLevel() > 0) {
      return nested().getCacheKey(tool, entry);
    }
    return null;
  }

  @Override
  default void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    ModifierEntry trait = getEntry(tool);
    if (trait.getLevel() > 0) {
      nested().addQuads(tool, trait, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
    }
  }

  @Override
  default int getTintIndexes() {
    return nested().getTintIndexes();
  }

  @Override
  default int getTint(IToolStackView tool, ModifierEntry entry, int index) {
    // assuming if we made it this far, the condition matches
    return nested().getTint(tool, getEntry(tool), index);
  }

  /**
   * Modifier model to condition on a crafted modifier, intended to force a modifier to show first on a tool via constants without it always showing.
   * @param modifier  Modifier condition
   * @param nested    Model to show for that modifier
   */
  record Crafted(ModifierId modifier, ModifierModel nested) implements NestedModifierModel {
    public static final RecordLoadable<Crafted> LOADER = RecordLoadable.create(
      ModifierId.PARSER.requiredField("modifier", Crafted::modifier),
      ModifierModel.LOADER.requiredField("model", Crafted::nested),
      Crafted::new);

    @Override
    public RecordLoadable<Crafted> getLoader() {
      return LOADER;
    }

    @Override
    public ModifierEntry getEntry(IToolStackView tool) {
      return tool.getUpgrades().getEntry(modifier);
    }
  }

  /**
   * Modifier model that shows even on traits, named as that is the primary usecase.
   * On the chance the modifier is both in traits and craftable, this will show it even if crafted. Intended use is for dynamic elements such as fluids that show regardless.
   * @param modifier  Modifier condition
   * @param nested    Model to show for that modifier
   */
  record Trait(ModifierId modifier, ModifierModel nested) implements NestedModifierModel {
    public static final RecordLoadable<Trait> LOADER = RecordLoadable.create(
      ModifierId.PARSER.requiredField("modifier", Trait::modifier),
      ModifierModel.LOADER.requiredField("model", Trait::nested),
      Trait::new);

    @Override
    public RecordLoadable<Trait> getLoader() {
      return LOADER;
    }

    @Override
    public ModifierEntry getEntry(IToolStackView tool) {
      return tool.getModifier(modifier);
    }
  }
}
