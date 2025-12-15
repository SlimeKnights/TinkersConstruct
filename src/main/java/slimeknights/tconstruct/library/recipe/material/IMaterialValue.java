package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.recipe.container.ISingleStackContainer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;

/**
 * Represents a material with an amount and a cost
 */
public interface IMaterialValue {
  /** Field for a material value in JSON */
  LoadableField<Integer,IMaterialValue> VALUE_FIELD = IntLoadable.FROM_ONE.defaultField("value", 1, true, IMaterialValue::getValue);
  /** Field for a needed amount in JSON */
  LoadableField<Integer,IMaterialValue> NEEDED_FIELD = IntLoadable.FROM_ONE.defaultField("needed", 1, true, IMaterialValue::getNeeded);

  /** Gets the material represented in this cost */
  MaterialVariant getMaterial();

  /** Gets the number of items needed for a single craft */
  default int getNeeded() {
    return 1;
  }

  /** Gets the value of a single item of this material */
  int getValue();

  /** Checks if a leftover is present without creating item stack copies. */
  default boolean hasLeftover() {
    return false;
  }

  /**
   * Gets a copy of the leftover stack for this recipe
   * @return  Leftover stack
   */
  default ItemStack getLeftover() {
    return ItemStack.EMPTY;
  }


  /* Helpers */

  /**
   * Gets the amount of material present in the inventory as a float for display
   * @param inv  Inventory reference
   * @return  Number of material present as a float
   */
  default float getMaterialValue(ISingleStackContainer inv) {
    return inv.getStack().getCount() * this.getValue() / (float)this.getNeeded();
  }

  /**
   * Gets the number of items in order to craft a material with the given cost
   * @param itemCost  Cost of the item being crafted
   * @return  Number of the input to consume
   */
  default int getItemsUsed(int itemCost) {
    int needed = itemCost * getNeeded();
    int value = getValue();
    int cost = needed / value;
    if (needed % value != 0) {
      cost++;
    }
    return cost;
  }

  /**
   * Gets the number of leftover material from crafting a part with this material
   * @param itemCost  Cost of the item being crafted
   * @return  Number of input to consume
   */
  default int getRemainder(int itemCost) {
    return itemCost * this.getNeeded() % this.getValue();
  }
}
