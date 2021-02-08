package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ExperiencedModifier extends Modifier {
  public ExperiencedModifier() {
    super(0xe8db49);
    MinecraftForge.EVENT_BUS.addListener(this::onEntityKill);
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

  @Override
  public void beforeBlockBreak(IModifierToolStack tool, int level, BreakEvent event) {
    event.setExpToDrop(boost(event.getExpToDrop(), level));
  }

  /**
   * Event handled locally as its pretty specialized
   * @param event  Event
   */
  private void onEntityKill(LivingExperienceDropEvent event) {
    PlayerEntity player = event.getAttackingPlayer();
    if (player != null) {
      ToolStack tool = ToolStack.from(player.getHeldItemMainhand());
      if (!tool.isBroken()) {
        int level = tool.getModifierLevel(this);
        if (level > 0) {
          event.setDroppedExperience(boost(event.getDroppedExperience(), level));
        }
      }
    }
  }
}
