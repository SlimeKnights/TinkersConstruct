package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logic to handle the looting event for all main tinker tools
 */
public class ModifierLootingHandler {
  /** If contained in the set, they should use the offhand for looting */
  private static final Map<UUID,EquipmentSlotType> LOOTING_OFFHAND = new HashMap<>();
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

  /** @deprecated use {@link #setLootingSlot(LivingEntity, EquipmentSlotType)} */
  @Deprecated
  public static void setLootingHand(LivingEntity entity, Hand hand) {
    setLootingSlot(entity, Util.getSlotType(hand));
  }

  /**
   * Sets the hand used for looting, so the tool is fetched from the proper context
   * @param entity    Player to set
   * @param slotType  Slot type
   */
  public static void setLootingSlot(LivingEntity entity, EquipmentSlotType slotType) {
    if (slotType == EquipmentSlotType.MAINHAND) {
      LOOTING_OFFHAND.remove(entity.getUniqueID());
    } else {
      LOOTING_OFFHAND.put(entity.getUniqueID(), slotType);
    }
  }

  /** @deprecated use {@link #getLootingSlot(LivingEntity)} */
  @Deprecated
  public static Hand getLootingHand(@Nullable LivingEntity entity) {
    return entity != null && LOOTING_OFFHAND.get(entity.getUniqueID()) == EquipmentSlotType.OFFHAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
  }

  /** Gets the slot to use for looting */
  public static EquipmentSlotType getLootingSlot(@Nullable LivingEntity entity) {
    return entity != null ? LOOTING_OFFHAND.getOrDefault(entity.getUniqueID(), EquipmentSlotType.MAINHAND) : EquipmentSlotType.MAINHAND;
  }

  /** Applies the looting bonus for modifiers */
  private static void onLooting(LootingLevelEvent event) {
    // must be an attacker with our tool
    DamageSource damageSource = event.getDamageSource();
    if (damageSource == null) {
      return;
    }
    Entity source = damageSource.getTrueSource();
    if (source instanceof LivingEntity) {
      // TODO: consider bow usage, as the attack time is not the same as the death time
      // TODO: extend to armor eventually
      LivingEntity holder = ((LivingEntity)source);
      ItemStack held = holder.getItemStackFromSlot(getLootingSlot(holder));
      if (TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
        ToolStack tool = ToolStack.from(held);
        int newLevel = ModifierUtil.getLootingLevel(tool, holder, event.getEntityLiving(), damageSource);
        event.setLootingLevel(newLevel);
      }
    }
  }

  /** Called when a player leaves the server to clear the face */
  private static void onLeaveServer(PlayerLoggedOutEvent event) {
    LOOTING_OFFHAND.remove(event.getPlayer().getUniqueID());
  }
}
