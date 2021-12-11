package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.List;

/**
 * Provides partial access to tool data, essentially a bridge between {@link IModifierToolStack} and {@link slimeknights.tconstruct.library.tools.context.ToolRebuildContext}
 */
public interface IToolContext {
  /** Gets the item contained in this tool */
  Item getItem();

  /** Gets the tool definition */
  ToolDefinition getDefinition();

  /** Checks if the tool has the given tag */
  default boolean hasTag(ITag<Item> tag) {
    return tag.contains(getItem());
  }


  /* Materials */

  /** Gets the list of current materials making this tool */
  MaterialNBT getMaterials();
  /**
   * Gets the list of all materials
   * @return List of all materials
   */
  default List<IMaterial> getMaterialsList() {
    return getMaterials().getMaterials();
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  default IMaterial getMaterial(int index) {
    return getMaterials().getMaterial(index);
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

  /** Cached tool stats calculated from materials and modifiers */
  StatsNBT getStats();

  /**
   * Gets persistent modifier data from the tool.
   * This data may be edited by modifiers and will persist when stats rebuild
   */
  IModDataReadOnly getPersistentData();

  /**
   * Gets volatile modifier data from the tool.
   * This data will be reset whenever modifiers reload and should not be edited.
   */
  IModDataReadOnly getVolatileData();
}
