package slimeknights.tconstruct.library.recipe.partbuilder;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Collections;
import java.util.List;

/**
 * Part builder recipes that can show in JEI
 */
public interface IDisplayPartBuilderRecipe extends IPartBuilderRecipe {
  /** Gets the material needed to craft this recipe */
  IMaterial getMaterial();

  /**
   * Gets a list of pattern items to display in the pattern slot
   * @return  Pattern items
   */
  default List<ItemStack> getPatternItems() {
    return Collections.singletonList(new ItemStack(TinkerTables.pattern));
  }
}
