package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logic to handle the looting event for all main tinker tools
 */
public class ModifierLootingHandler {
  /** If contained in the set, they should use the offhand for looting */
  private static final Map<UUID,EquipmentSlot> LOOTING_OFFHAND = new HashMap<>();
  private static boolean init = false;

  /** Initializies this listener */
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    MinecraftForge.EVENT_BUS.addListener(ModifierLootingHandler::onLooting);
    MinecraftForge.EVENT_BUS.addListener(ModifierLootingHandler::onLeaveServer);
  }

  /**
   * Sets the hand used for looting, so the tool is fetched from the proper context
   * @param entity    Player to set
   * @param slotType  Slot type
   */
  public static void setLootingSlot(LivingEntity entity, EquipmentSlot slotType) {
    if (slotType == EquipmentSlot.MAINHAND) {
      LOOTING_OFFHAND.remove(entity.getUUID());
    } else {
      LOOTING_OFFHAND.put(entity.getUUID(), slotType);
    }
  }

  /** Gets the slot to use for looting */
  public static EquipmentSlot getLootingSlot(@Nullable LivingEntity entity) {
    return entity != null ? LOOTING_OFFHAND.getOrDefault(entity.getUUID(), EquipmentSlot.MAINHAND) : EquipmentSlot.MAINHAND;
  }

  /** Applies the looting bonus for modifiers */
  private static void onLooting(LootingLevelEvent event) {
    // must be an attacker with our tool
    DamageSource damageSource = event.getDamageSource();
    if (damageSource == null) {
      return;
    }
    Entity source = damageSource.getEntity();
    if (source instanceof LivingEntity) {
      // TODO: consider bow usage, as the attack time is not the same as the death time
      // TODO: extend to armor eventually
      LivingEntity holder = ((LivingEntity)source);
      EquipmentSlot slotType = getLootingSlot(holder);
      ItemStack held = holder.getItemBySlot(slotType);
      int level = event.getLootingLevel();
      if (TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
        ToolStack tool = ToolStack.from(held);
        level = ModifierUtil.getLootingLevel(tool, holder, event.getEntityLiving(), damageSource);
        // ignore default looting if we are looting from another slot
      } else if (slotType != EquipmentSlot.MAINHAND) {
        level = 0;
      }
      // boot looting with pants
      level = ModifierUtil.getLeggingsLootingLevel(holder, event.getEntityLiving(), damageSource, level);
      event.setLootingLevel(level);
    }
  }

  /** Called when a player leaves the server to clear the face */
  private static void onLeaveServer(PlayerLoggedOutEvent event) {
    LOOTING_OFFHAND.remove(event.getPlayer().getUUID());
  }
}
