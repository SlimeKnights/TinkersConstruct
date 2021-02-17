package slimeknights.tconstruct.tools.modifiers.shared;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.function.Consumer;

public class ExperiencedModifier extends Modifier {
  public ExperiencedModifier() {
    super(0xe8db49);
    MinecraftForge.EVENT_BUS.addListener(this::onEntityKill);
    MinecraftForge.EVENT_BUS.addListener((Consumer<BreakEvent>)this::beforeBlockBreak);
  }

  /**
   * Boosts the original based on the level
   * @param original  Original amount
   * @param level     Modifier level
   * @return  Boosted XP
   */
  private static int boost(int original, int level) {
    return (int) (original  * (1 + (0.5 * level)));
  }

  /**
   * Used to modify the XP dropped, regular hook is just for canceling
   * @param event  Event
   */
  private void beforeBlockBreak(BreakEvent event) {
    ToolStack tool = getHeldTool(event.getPlayer());
    if (tool != null) {
      int level = tool.getModifierLevel(this);
      if (level > 0) {
        event.setExpToDrop(boost(event.getExpToDrop(), level));
      }
    }
  }

  /**
   * Event handled locally as its pretty specialized
   * @param event  Event
   */
  private void onEntityKill(LivingExperienceDropEvent event) {
    ToolStack tool = getHeldTool(event.getAttackingPlayer());
    if (tool != null) {
      int level = tool.getModifierLevel(this);
      if (level > 0) {
        event.setDroppedExperience(boost(event.getDroppedExperience(), level));
      }
    }
  }
}
