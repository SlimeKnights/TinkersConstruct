package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class DwarfishModifier extends Modifier {
  public DwarfishModifier() {
    super(0xed9f07);
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event) {
    int y = event.getPos().getY();
    // formula means at level 1, we get a 33% boost at y=10, and no boost at or above level 75
    // by level 3, its a 100% boost at y=10
    float factor = Math.max(1f, (75 - y) * (level / 195f) + 1);
    if (factor > 1f) {
      event.setNewSpeed(event.getNewSpeed() * factor);
    }
  }
}
