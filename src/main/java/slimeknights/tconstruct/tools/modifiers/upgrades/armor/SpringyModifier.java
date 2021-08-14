package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.EntityModifierDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class SpringyModifier extends IncrementalModifier {
  private static final ResourceLocation SLOT_IN_CHARGE = TConstruct.getResource("springy_slot");
  public SpringyModifier() {
    super(0xFF950D);
  }

  /** Checks if the given slot is in charge of this modifier */
  private static boolean isInCharge(IModDataReadOnly data, EquipmentSlotType slotType) {
    return data.contains(SLOT_IN_CHARGE, NBT.TAG_ANY_NUMERIC) && data.getInt(SLOT_IN_CHARGE) == slotType.getIndex();
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getTrueSource();
    if (isDirectDamage && attacker instanceof LivingEntity) {
      LivingEntity user = context.getEntity();
      user.getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
        if (isInCharge(data, slotType)) {
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
    context.getEntity().getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      if (isInCharge(data, context.getChangedSlot())) {
        data.remove(SLOT_IN_CHARGE);
      }
    });
  }

  /** Marks this slot as in charge of springy if no slot is in charge */
  private static void attemptTakeCharge(LivingEntity entity, EquipmentSlotType slotType) {
    entity.getCapability(EntityModifierDataCapability.CAPABILITY).ifPresent(data -> {
      if (!data.contains(SLOT_IN_CHARGE, NBT.TAG_ANY_NUMERIC)) {
        data.putInt(SLOT_IN_CHARGE, slotType.getIndex());
      }
    });
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      attemptTakeCharge(context.getEntity(), context.getChangedSlot());
    }
  }

  @Override
  public void onEquipmentChange(IModifierToolStack tool, int level, EquipmentChangeContext context, EquipmentSlotType slotType) {
    if (!tool.isBroken()) {
      attemptTakeCharge(context.getEntity(), context.getChangedSlot());
    }
  }
}
