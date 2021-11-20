package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpringyModifier extends IncrementalModifier {
  private static final TinkerDataKey<SlotInCharge> SLOT_IN_CHARGE = TConstruct.createKey("springy");
  public SpringyModifier() {
    super(0xFF950D);
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    LivingEntity user = context.getEntity();
    Entity attacker = source.getTrueSource();
    if (isDirectDamage && !user.getEntityWorld().isRemote && attacker instanceof LivingEntity) {
      user.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        // ensure this slot is in charge before continuing
        if (Optional.ofNullable(data.get(SLOT_IN_CHARGE)).filter(slot -> slot.inCharge == slotType).isPresent()) {
          // choose a random slot to apply knockback, prevents max from getting too high
          EquipmentSlotType bouncingSlot = EquipmentSlotType.fromSlotTypeAndIndex(Group.ARMOR, RANDOM.nextInt(4));
          IModifierToolStack bouncingTool = context.getToolInSlot(bouncingSlot);
          if (bouncingTool != null && !bouncingTool.isBroken()) {
            // 50% change per level of it applying, means happens every time at level 2 or 3
            float bouncingLevel = getScaledLevel(bouncingTool, bouncingTool.getModifierLevel(this));
            if (bouncingLevel > 1 || (RANDOM.nextFloat() < bouncingLevel * 0.5f)) {
              // does 0.5 base, plus up to 0.5f per level -- for comparison, 0.4 is normal knockback, 0.9 is with knockback 1
              ((LivingEntity)attacker).applyKnockback(0.5f * RANDOM.nextFloat() * bouncingLevel, -MathHelper.sin(attacker.rotationYaw * (float)Math.PI / 180F), MathHelper.cos(attacker.rotationYaw * (float)Math.PI / 180F));
            }
          }
        }
      });
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    // remove slot in charge if that is us
    EquipmentSlotType slot = context.getChangedSlot();
    LivingEntity entity = context.getEntity();
    if (!tool.isBroken() && slot.getSlotType() == Group.ARMOR && !entity.getEntityWorld().isRemote) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        SlotInCharge slotInCharge = data.get(SLOT_IN_CHARGE);
        if (slotInCharge != null) {
          slotInCharge.removeSlot(slot);
        }
      });
    }
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    EquipmentSlotType slot = context.getChangedSlot();
    LivingEntity entity = context.getEntity();
    if (!tool.isBroken() && slot.getSlotType() == Group.ARMOR && !entity.getEntityWorld().isRemote) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        SlotInCharge slotInCharge = data.get(SLOT_IN_CHARGE);
        if (slotInCharge == null) {
          slotInCharge = new SlotInCharge();
          data.put(SLOT_IN_CHARGE, slotInCharge);
        }
        slotInCharge.addSlot(slot);
      });
    }
  }

  /** Tracker to determine which slot should be in charge */
  private static class SlotInCharge {
    private final boolean[] active = new boolean[4];
    @Nullable
    EquipmentSlotType inCharge = null;

    /** Adds the given slot to the tracker */
    void addSlot(EquipmentSlotType slotType) {
      active[slotType.getIndex()] = true;
      if (inCharge == null) {
        inCharge = slotType;
      }
    }

    /** Removes the given slot from the tracker */
    void removeSlot(EquipmentSlotType slotType) {
      active[slotType.getIndex()] = false;
      for (EquipmentSlotType armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
        if (active[slotType.getIndex()]) {
          inCharge = armorSlot;
          break;
        }
      }
      inCharge = null;
    }
  }
}
