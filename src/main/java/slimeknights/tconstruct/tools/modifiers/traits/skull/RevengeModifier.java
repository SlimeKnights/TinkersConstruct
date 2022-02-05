package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RevengeModifier extends SingleUseModifier {
  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // must be attacked by entity
    Entity trueSource = source.getEntity();
    LivingEntity living = context.getEntity();
    if (trueSource != null && trueSource != living) { // no making yourself mad with slurping or self-destruct or alike
      MobEffectInstance effect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300);
      effect.getCurativeItems().clear();
      effect.getCurativeItems().add(new ItemStack(living.getItemBySlot(slotType).getItem()));
      living.addEffect(effect);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    super.onUnequip(tool, level, context);
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }
}
