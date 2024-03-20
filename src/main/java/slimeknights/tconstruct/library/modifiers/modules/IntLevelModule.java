package slimeknights.tconstruct.library.modifiers.modules;

import net.minecraft.util.Mth;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Helper to handle effective levels in a usecase that requires int levels */
public interface IntLevelModule {
  /** Level of the leveling thing */
  int level();

  /** Gets the level to use for the module */
  default int getLevel(IToolStackView tool, ModifierEntry modifier) {
    return Mth.floor(modifier.getEffectiveLevel(tool)) * this.level();
  }
}
