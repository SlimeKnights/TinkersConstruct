package slimeknights.tconstruct.library.modifiers.aspects;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifiedToolStatsBuilder;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

public abstract class ModifierAspect {

  protected final IModifier parent;

  protected ModifierAspect() {
    this.parent = null;
  }

  public ModifierAspect(IModifier parent) {
    this.parent = parent;
  }

  /**
   *
   * @param stack a copy of the stack
   * @param original the original stack
   *
   * @return
   */
  public abstract boolean canApply(ItemStack stack, ItemStack original);

  /**
   * Override this function in your modifier aspect if you choose to edit the stats of the tool.
   * The edited stats will be passed on to any other aspect on the modifier ALONG with the actual modifier.
   *
   * @param statsBuilder the stat builder
   */
  public void editStats(ModifiedToolStatsBuilder statsBuilder) {

  }

  /**
   * Override this function in your modifier aspect if you want to modify the NBT of the modifier at all.
   * The only information saved on the modifierNBT is identifier and level with both being able to be edited.
   *
   * @param modifierNBT the modifier nbt for the given modifier
   * @return the edited modifier nbt
   */
  public ModifierNBT editNBT(ModifierNBT modifierNBT) {
    return modifierNBT;
  }
}
