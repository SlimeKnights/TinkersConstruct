package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpringyModifier extends Modifier {
  private static final TinkerDataKey<SlotInCharge> SLOT_IN_CHARGE = TConstruct.createKey("springy");

  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    LivingEntity user = context.getEntity();
    Entity attacker = source.getEntity();
    if (isDirectDamage && !user.level.isClientSide && attacker instanceof LivingEntity livingAttacker) {
      context.getTinkerData().ifPresent(data -> {
        // ensure this slot is in charge before continuing
        if (Optional.ofNullable(data.get(SLOT_IN_CHARGE)).filter(slot -> slot.inCharge == slotType).isPresent()) {
          // each slot attempts to apply, we keep the largest one, consistent with other counter attack modifiers
          float bestBonus = 0;
          for (EquipmentSlot bouncingSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
            IToolStackView bouncingTool = context.getToolInSlot(bouncingSlot);
            if (bouncingTool != null && !bouncingTool.isBroken()) {
              // 15% chance per level of it applying
              if (RANDOM.nextFloat() < (level * 0.25f)) {
                // does 0.5 base, plus up to 0.5f per level -- for comparison, 0.4 is normal knockback, 0.9 is with knockback 1
                float newBonus = 0.5f * RANDOM.nextFloat() * level;
                if (newBonus > bestBonus) {
                  bestBonus = newBonus;
                }
              }
            }
          }
          // did we end up with any bonus?
          if (bestBonus > 0) {
            float angle = attacker.getYRot() * (float)Math.PI / 180F;
            livingAttacker.knockback(bestBonus, -Mth.sin(angle), Mth.cos(angle));
          }
        }
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    // remove slot in charge if that is us
    EquipmentSlot slot = context.getChangedSlot();
    if (!tool.isBroken() && slot.getType() == Type.ARMOR && !context.getEntity().level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        SlotInCharge slotInCharge = data.get(SLOT_IN_CHARGE);
        if (slotInCharge != null) {
          slotInCharge.removeSlot(slot);
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    EquipmentSlot slot = context.getChangedSlot();
    if (!tool.isBroken() && slot.getType() == Type.ARMOR && !context.getEntity().level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        SlotInCharge slotInCharge = data.get(SLOT_IN_CHARGE);
        if (slotInCharge == null) {
          slotInCharge = new SlotInCharge();
          data.put(SLOT_IN_CHARGE, slotInCharge);
        }
        slotInCharge.addSlot(slot);
      });
    }
  }
  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // unarmed bonus
    return knockback + level * 0.5f;
  }

  /** Tracker to determine which slot should be in charge */
  private static class SlotInCharge {
    private final boolean[] active = new boolean[4];
    @Nullable
    EquipmentSlot inCharge = null;

    /** Adds the given slot to the tracker */
    void addSlot(EquipmentSlot slotType) {
      active[slotType.getIndex()] = true;
      if (inCharge == null) {
        inCharge = slotType;
      }
    }

    /** Removes the given slot from the tracker */
    void removeSlot(EquipmentSlot slotType) {
      active[slotType.getIndex()] = false;
      for (EquipmentSlot armorSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
        if (active[slotType.getIndex()]) {
          inCharge = armorSlot;
          return;
        }
      }
      inCharge = null;
    }
  }
}
