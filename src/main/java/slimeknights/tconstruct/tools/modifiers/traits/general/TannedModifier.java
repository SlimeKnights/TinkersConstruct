package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class TannedModifier extends SingleUseModifier {
  public TannedModifier() {
    super(0xC65C35);
  }

  @Override
  public int getPriority() {
    // higher than stoneshield, overslime, and reinforced
    return 200;
  }

  @Override
  public int onDamageTool(IModifierToolStack toolStack, int level, int amount) {
    return amount >= 1 ? 1 : 0;
  }
}
