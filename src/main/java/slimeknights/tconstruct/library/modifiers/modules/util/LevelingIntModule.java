package slimeknights.tconstruct.library.modifiers.modules.util;

import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

/** Helper to handle effective levels in a usecase that requires int levels. */
public interface LevelingIntModule {
  LoadableField<LevelingInt, LevelingIntModule> FIELD = LevelingInt.EACH_LEVEL.defaultField("level", LevelingInt.LEVEL, true, LevelingIntModule::level);

  /** Level of the leveling thing */
  LevelingInt level();

  /** Gets the level to use for the module */
  default int getLevel(ModifierEntry modifier) {
    return level().compute(modifier);
  }
}
