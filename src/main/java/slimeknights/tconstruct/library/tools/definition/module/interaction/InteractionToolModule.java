package slimeknights.tconstruct.library.tools.definition.module.interaction;

import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Logic for configuring how a tool's interaction modifiers behave
 */
public interface InteractionToolModule {
  /**
   * Checks if the given modifier on the given tool is allowed to interact
   * @param tool      Tool instance
   * @param modifier  Modifier instance
   * @param source    Source of the interaction
   * @return  True if it can interact
   */
  boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source);
}
