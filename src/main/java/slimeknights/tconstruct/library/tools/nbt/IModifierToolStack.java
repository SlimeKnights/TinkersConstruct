package slimeknights.tconstruct.library.tools.nbt;


import net.minecraft.item.Item;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.List;

/**
 * Provides mostly read only access to {@link ToolStack}.
 * Used since modifiers should not be modifying the tool materials or modifiers in their behaviors.
 * If you receive a modifier tool stack as a parameter, do NOT use an instanceof check and cast it to a ToolStack. Don't make me use a private wrapper class.
 */
public interface IModifierToolStack {
  /** Gets the item contained in this tool */
  Item getItem();

  /** Gets the tool definition */
  ToolDefinition getDefinition();


  /* Damage state */

  /** Gets the current damage of the tool */
  int getDamage();

  /** Checks whether the tool is broken */
  boolean isBroken();


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
  ModDataNBT getPersistentData();

  /**
   * Gets volatile modifier data from the tool.
   * This data will be reset whenever modifiers reload and should not be edited.
   */
  IModDataReadOnly getVolatileData();


  /* Helpers */

  /**
   * Gets the free upgrade slots remaining on the tool
   * @return  Free upgrade slots
   */
  default int getFreeUpgrades() {
    return getPersistentData().getUpgrades() + getVolatileData().getUpgrades();
  }

  /**
   * Gets the free ability slots remaining on the tool
   * @return  Free abilities
   */
  default int getFreeAbilities() {
    return getPersistentData().getAbilities() + getVolatileData().getAbilities();
  }
}
