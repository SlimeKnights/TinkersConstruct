package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

public class LeapingModifier extends Modifier {
  // TODO: do I have to hardcode this to slot?
  private final EquipmentSlotType slot;
  public LeapingModifier(EquipmentSlotType slot) {
    super(0x6DBEBD);
    this.slot = slot;
    MinecraftForge.EVENT_BUS.addListener(this::onLivingFall);
    MinecraftForge.EVENT_BUS.addListener(this::onLivingJump);
  }

  /** Reduce fall distance for fall damage */
  private void onLivingFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    ItemStack leggings = entity.getItemStackFromSlot(slot);
    int boost = ModifierUtil.getModifierLevel(leggings, this);
    if (boost > 0) {
      event.setDistance(Math.max(event.getDistance() - boost, 0));
    }
  }

  /** Called on jumping to boost the jump height of the entity */
  private void onLivingJump(LivingJumpEvent event) {
    LivingEntity entity = event.getEntityLiving();
    ItemStack leggings = entity.getItemStackFromSlot(slot);
    int boost = ModifierUtil.getModifierLevel(leggings, this);
    if (boost > 0) {
      entity.setMotion(entity.getMotion().add(0, boost * 0.1, 0));
    }
  }
}
