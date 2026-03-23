package slimeknights.tconstruct.library.modifiers.modules.util;

import net.minecraft.util.Mth;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

/** @deprecated use {@link LevelingIntModule} */
@Deprecated(forRemoval = true)
public interface IntLevelModule {
  LoadableField<Integer,IntLevelModule> FIELD = IntLoadable.ANY_SHORT.defaultField("level", 1, true, IntLevelModule::level);

  /** Level of the leveling thing */
  int level();

  /** Gets the level to use for the module */
  default int getLevel(ModifierEntry modifier) {
    return Mth.floor(modifier.getEffectiveLevel() * level());
  }
}
