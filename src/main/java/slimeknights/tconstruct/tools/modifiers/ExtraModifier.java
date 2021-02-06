package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/**
 * Shared logic for all modifiers that just grant a bonus modifier
 */
public class ExtraModifier extends Modifier {
  private final boolean singleUse;
  public ExtraModifier(int color, boolean singleUse) {
    super(color);
    this.singleUse = singleUse;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    // single use modifiers can hide in the tooltip, but show enhanced regardless
    return !singleUse || advanced;
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT data) {
    data.addUpgrades(singleUse ? 1 : level);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (singleUse) {
      return getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public int getPriority() {
    // show lower priority, the trait should be above the rest though
    return singleUse ? 50 : 75;
  }
}
