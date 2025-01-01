package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;

import java.util.List;

/**
 * Part builder recipes that can show in JEI
 */
public interface IDisplayPartBuilderRecipe extends IPartBuilderRecipe {
  /** Gets the material variant required to craft this recipe */
  MaterialVariant getMaterial();

  /**
   * Gets a list of pattern items to display in the pattern slot
   * @return  Pattern items
   */
  default List<ItemStack> getPatternItems() {
    return RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.DEFAULT_PATTERNS).map(ItemStack::new).toList();
  }
}
