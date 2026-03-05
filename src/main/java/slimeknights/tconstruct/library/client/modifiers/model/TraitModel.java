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

/**
 * Modifier model that shows a trait, conditioned on it not being present as an upgrade. Meant for use in the constant list
 * @param modifier  Modifier condition
 * @param nested    Model to show for that modifier
 */
public record TraitModel(ModifierId modifier, ModifierModel nested) implements ModifierModel {
  public static final RecordLoadable<TraitModel> LOADER = RecordLoadable.create(
    ModifierId.PARSER.requiredField("modifier", TraitModel::modifier),
    ModifierModel.LOADER.requiredField("model", TraitModel::nested),
    TraitModel::new);

  @Override
  public RecordLoadable<TraitModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    nested.validate(spriteGetter);
  }


  /** Gets the relevant entry on the given tool */
  private ModifierEntry getEntry(IToolStackView tool) {
    if (tool.getUpgrades().getLevel(modifier) == 0) {
      return tool.getModifier(modifier);
    }
    return ModifierEntry.EMPTY;
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    ModifierEntry trait = getEntry(tool);
    if (trait.getLevel() > 0) {
      return nested.getCacheKey(tool, trait);
    }
    return null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    ModifierEntry trait = getEntry(tool);
    if (trait.getLevel() > 0) {
      nested.addQuads(tool, trait, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
    }
  }

  @Override
  public int getTintIndexes() {
    return nested.getTintIndexes();
  }

  @Override
  public int getTint(IToolStackView tool, ModifierEntry entry, int index) {
    // assuming if we made it this far, the condition matches
    return nested.getTint(tool, entry, index);
  }
}
