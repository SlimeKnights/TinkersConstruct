package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

import java.util.List;

/**
 * Provides partial access to tool data, essentially a bridge between {@link IToolStackView} and {@link slimeknights.tconstruct.library.tools.context.ToolRebuildContext}
 */
public interface IToolContext {
  /** Gets the item contained in this tool */
  Item getItem();

  /** Determines if the tool has the given tag */
  default boolean is(Tag<Item> tag) {
    return tag.contains(getItem());
  }

  /** Gets the tool definition */
  ToolDefinition getDefinition();

  /** Checks if the tool has the given tag */
  default boolean hasTag(Tag<Item> tag) {
    return tag.contains(getItem());
  }


  /* Materials */

  /** Gets the list of current materials making this tool */
  MaterialNBT getMaterials();

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  default MaterialVariant getMaterial(int index) {
    return getMaterials().get(index);
  }


  /* Modifiers */

  /** Gets a list of modifiers that are specifically added to this tool. Unlike {@link #getModifiers()}, does not include modifiers from the tool or materials */
  ModifierNBT getUpgrades();

  /** Gets a full list of effective modifiers on this tool, from both upgrades/abilities and material traits */
  ModifierNBT getModifiers();

  /**
   * Helper to get a list of all modifiers on the tool. Note this list is already sorted by priority
   * @return  List of all modifiers
   */
  default List<ModifierEntry> getModifierList() {
    return getModifiers().getModifiers();
  }

  /**
   * Gets the level of a modifier on this tool. Will consider both raw modifiers and material traits
   * @param modifier  Modifier
   * @return  Level of modifier, 0 if the modifier is not on the tool
   */
  default int getModifierLevel(Modifier modifier) {
    return getModifiers().getLevel(modifier);
  }


  /* Tool data */

  /**
   * Gets persistent modifier data from the tool.
   * This data may be edited by modifiers and will persist when stats rebuild
   */
  IModDataView getPersistentData();

  /**
   * Gets volatile modifier data from the tool.
   * This data will be reset whenever modifiers reload and should not be edited.
   */
  IModDataView getVolatileData();
}
