package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ExperiencedModifier extends Modifier {
  private static final TinkerDataKey<Integer> EXPERIENCED = TConstruct.createKey("experienced");
  public ExperiencedModifier() {
    MinecraftForge.EVENT_BUS.addListener(this::onExperienceDrop);
    MinecraftForge.EVENT_BUS.addListener(this::onEntityKilled);
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

  /** Mark entities killed by our arrows */
  private void onEntityKilled(LivingDeathEvent event) {
    DamageSource source = event.getSource();
    if (source != null && source.getDirectEntity() instanceof Projectile projectile) {
      ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);
      // it is very unlikely that we fire an arrow on a bow with no modifiers, if that ever happens though we will not be able to identify its our arrow
      if (!modifiers.isEmpty()) {
        event.getEntityLiving().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(EXPERIENCED, modifiers.getLevel(this.getId())));
      }
    }
  }

  /**
   * Event handled locally as its pretty specialized
   * @param event  Event
   */
  private void onExperienceDrop(LivingExperienceDropEvent event) {
    // if the entity was killed by one of our arrows, boost the experience from that
    int experienced = event.getEntityLiving().getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(EXPERIENCED)).orElse(-1);
    if (experienced > 0) {
      event.setDroppedExperience(boost(event.getDroppedExperience(), experienced));
      // experienced being zero means it was our arrow but it was not modified, do not check the held item in that case
    } else if (experienced != 0) {
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
}
