package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class RevengeModifier extends SingleUseModifier {
  public RevengeModifier() {
    super(0x698E45);
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // must be attacked by entity
    Entity trueSource = source.getTrueSource();
    LivingEntity living = context.getEntity();
    if (trueSource != null && trueSource != living) { // no making yourself mad with slurping or self-destruct or alike
      EffectInstance effect = new EffectInstance(Effects.STRENGTH, 300);
      effect.getCurativeItems().clear();
      effect.getCurativeItems().add(new ItemStack(living.getItemStackFromSlot(slotType).getItem()));
      living.addPotionEffect(effect);
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    super.onUnequip(tool, level, context);
    if (context.getChangedSlot() == EquipmentSlotType.HEAD) {
      IModifierToolStack replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }
}
