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
}
