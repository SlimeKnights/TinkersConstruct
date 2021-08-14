package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class StickyModifier extends Modifier {
  public StickyModifier() {
    super(0xffffff);
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getTrueSource();
    if (isDirectDamage && attacker instanceof LivingEntity) {
      // TODO: should we use getRandomEquipped like vanilla?
      // 15% chance of working per level
      if (RANDOM.nextFloat() < (level * 0.15f)) {
        int duration = 20 + RANDOM.nextInt(10 * level);
        ((LivingEntity)attacker).addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, level));
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }
}
