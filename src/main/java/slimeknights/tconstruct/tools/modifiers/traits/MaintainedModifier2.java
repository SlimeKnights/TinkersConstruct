package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Well maintained for classic Bronze */
public class MaintainedModifier2 extends MaintainedModifier {
  public MaintainedModifier2() {
    super(0xcea179);
  }

  @Override
  protected float getTotalBoost(IModifierToolStack tool, int level) {
    int durability = tool.getCurrentDurability();
    int fullMax = tool.getStats().getDurability();
    // from 25% to 100%: 25% boost per level
    return boost(durability, 0.25f, fullMax / 4, fullMax) * level;
  }
}
