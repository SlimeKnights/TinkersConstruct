package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import java.util.List;

public class DragonbornModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final TinkerDataKey<ModifierMaxLevel> DRAGONBORN = TConstruct.createKey("dragonborn");
  public DragonbornModifier() {
    super(0x232323, DRAGONBORN);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, CriticalHitEvent.class, DragonbornModifier::onCritical);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  private static boolean isAirborne(LivingEntity living) {
    return !living.isOnGround() && !living.isOnLadder() && !living.isInWater() && !living.isPassenger();
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && isAirborne(context.getEntity())) {
      modifierValue += getScaledLevel(tool, level) * 2.5;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2.5f, tooltip);
  }

  /** Boosts critical hit damage */
  private static void onCritical(CriticalHitEvent event) {
    if (event.getResult() != Result.DENY) {
      // force critical if not already critical and in the air
      LivingEntity living = event.getEntityLiving();
      // make it critical if we meet our simpler conditions, note this does not boost attack damage
      boolean isCritical = event.isVanillaCritical() || event.getResult() == Result.ALLOW;
      if (!isCritical && isAirborne(living)) {
        isCritical = true;
        event.setResult(Result.ALLOW);
      }

      // if we either were or became critical, time to boost
      if (isCritical) {
        living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
          ModifierMaxLevel dragonborn = data.get(DRAGONBORN);
          if (dragonborn != null) {
            float max = dragonborn.getMax();
            if (max > 0) {
              // adds +10% critical hit per level
              event.setDamageModifier(event.getDamageModifier() + max * 0.1f);
            }
          }
        });
      }
    }
  }
}
