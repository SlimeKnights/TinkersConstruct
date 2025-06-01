package slimeknights.tconstruct.tools.modifiers.ability.armor;

import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.ZoomModule;

/** @deprecated use {@link ZoomModule} */
@Deprecated(forRemoval = true)
public class ZoomModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ZoomModule.SPYGLASS);
  }

  @Override
  public int getPriority() {
    return 90; // after slurping and slings, before blocking
  }
}
