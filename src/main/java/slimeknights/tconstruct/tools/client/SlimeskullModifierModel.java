package slimeknights.tconstruct.tools.client;

import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.modifiers.model.MaterialModifierModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

/**
 * Modifier model that tints the skull material. Will tint it using the dye color, or the material color if not dyed.
 */
public record SlimeskullModifierModel(Material small, int skullIndex, int slimeIndex) implements MaterialModifierModel {
  public static final RecordLoadable<SlimeskullModifierModel> LOADER = RecordLoadable.create(
    TEXTURE_FIELD,
    IntLoadable.FROM_ZERO.requiredField("skull_index", SlimeskullModifierModel::skullIndex),
    IntLoadable.FROM_ZERO.requiredField("slime_index", SlimeskullModifierModel::slimeIndex),
    SlimeskullModifierModel::new);

  @Override
  public RecordLoadable<SlimeskullModifierModel> getLoader() {
    return LOADER;
  }

  @Nullable
  @Override
  public Material large() {
    return null;
  }

  @Override
  public MaterialVariantId getMaterial(IToolStackView tool, ModifierEntry entry) {
    return tool.getMaterial(skullIndex).getVariant();
  }

  @Override
  public int getColor(IToolStackView tool, ModifierEntry entry, TintedSprite sprite) {
    IModDataView data = tool.getPersistentData();
    ResourceLocation dyed = TinkerModifiers.dyed.getId();
    if (data.contains(dyed, Tag.TAG_INT)) {
      return 0xFF000000 | data.getInt(dyed);
    } else {
      return SlimeskullArmorModel.MATERIAL_COLOR_CACHE.apply(tool.getMaterial(slimeIndex).getVariant());
    }
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    IModDataView data = tool.getPersistentData();
    // only need to cache colors from dyed, if its from the slime we will get it from the material in cache
    ResourceLocation dyed = TinkerModifiers.dyed.getId();
    if (data.contains(dyed, Tag.TAG_INT)) {
      return data.getInt(dyed);
    }
    return null;
  }
}
