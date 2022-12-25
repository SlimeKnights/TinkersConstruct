package slimeknights.tconstruct.library.tools.nbt;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;

/**
 * Provides mostly read only access to {@link ToolStack}.
 * Used since modifiers should not be modifying the tool materials or modifiers in their behaviors.
 * If you receive an instance of this interface a parameter, do NOT use an instanceof check and cast it to a ToolStack. Don't make me use a private wrapper class.
 */
public interface IToolStackView extends IToolContext {
  /** Commonly used operation, getting a stat multiplier */
  default float getMultiplier(INumericToolStat<?> stat) {
    return getDefinition().getData().getMultiplier(stat);
  }


  /* Damage state */

  /** Gets the current damage of the tool */
  int getDamage();

  /** Gets the current durability remaining for this tool */
  int getCurrentDurability();

  /** Checks whether the tool is broken */
  boolean isBroken();

  /** If true, tool is marked unbreakable by vanilla */
  boolean isUnbreakable();

  /**
   * Sets the tools current damage.
   * Note in general you should use {@link ToolDamageUtil#damage(IToolStackView, int, LivingEntity, ItemStack)} or {@link ToolDamageUtil#repair(IToolStackView, int)} as they handle modifiers
   * @param damage  New damage
   */
  void setDamage(int damage);

  /** Cached tool stats calculated from materials and modifiers */
  StatsNBT getStats();

  /**
   * Gets persistent modifier data from the tool.
   * This data may be edited by modifiers and will persist when stats rebuild
   * TODO 1.19: change return type to NamespaceNBT as modifiers should not be changing slots, will make bow hooks easier
   */
  @Override
  ModDataNBT getPersistentData();


  /* Helpers */

  /**
   * Gets the free upgrade slots remaining on the tool
   * @return  Free upgrade slots
   */
  default int getFreeSlots(SlotType type) {
    return getPersistentData().getSlots(type) + getVolatileData().getSlots(type);
  }
}
