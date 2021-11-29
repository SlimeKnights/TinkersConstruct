package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class LeapingModifier extends IncrementalModifier {
  private static final TinkerDataKey<Float> LEAPING = TConstruct.createKey("leaping");
  public LeapingModifier() {
    super(0xD8B281);
    MinecraftForge.EVENT_BUS.addListener(LeapingModifier::onLivingFall);
    MinecraftForge.EVENT_BUS.addListener(LeapingModifier::onLivingJump);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, LEAPING, -getScaledLevel(tool, level));
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, LEAPING, getScaledLevel(tool, level));
  }

  /** Reduce fall distance for fall damage */
  private static void onLivingFall(LivingFallEvent event) {
    LivingEntity entity = event.getEntityLiving();
    float boost = ModifierUtil.getTotalModifierFloat(entity, LEAPING);
    if (boost > 0) {
      event.setDistance(Math.max(event.getDistance() - boost, 0));
    }
  }

  /** Called on jumping to boost the jump height of the entity */
  private static void onLivingJump(LivingJumpEvent event) {
    LivingEntity entity = event.getEntityLiving();
    float boost = ModifierUtil.getTotalModifierFloat(entity, LEAPING);
    if (boost > 0) {
      entity.setMotion(entity.getMotion().add(0, boost * 0.1, 0));
    }
  }
}
