package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ExperiencedModifier extends Modifier {
  public ExperiencedModifier() {
    MinecraftForge.EVENT_BUS.addListener(this::onEntityKill);
    MinecraftForge.EVENT_BUS.addListener(this::beforeBlockBreak);
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
    // only support main hand block breaking currently
    int level = 0;
    ToolStack tool = getHeldTool(event.getPlayer(), InteractionHand.MAIN_HAND);
    if (tool != null) {
      level = tool.getModifierLevel(this);
    }
    // bonus from experienced pants
    tool = getHeldTool(event.getPlayer(), EquipmentSlot.LEGS);
    if (tool != null) {
      level += tool.getModifierLevel(this);
    }
    if (level > 0) {
      event.setExpToDrop(boost(event.getExpToDrop(), level));
    }
  }

  /**
   * Event handled locally as its pretty specialized
   * @param event  Event
   */
  private void onEntityKill(LivingExperienceDropEvent event) {
    Player player = event.getAttackingPlayer();
    if (player != null) {
      int level = 0;
      // held tool
      ToolStack tool = getHeldTool(player, ModifierLootingHandler.getLootingSlot(player));
      if (tool != null) level = tool.getModifierLevel(this);
      // bonus from experienced pants
      tool = getHeldTool(player, EquipmentSlot.LEGS);
      if (tool != null) level += tool.getModifierLevel(this);
      if (level > 0) {
        event.setDroppedExperience(boost(event.getDroppedExperience(), level));
      }
    }
  }
}
