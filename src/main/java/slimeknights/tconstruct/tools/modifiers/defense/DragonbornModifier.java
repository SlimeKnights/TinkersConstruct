package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import javax.annotation.Nullable;
import java.util.List;

public class DragonbornModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final TinkerDataKey<ModifierMaxLevel> DRAGONBORN = TConstruct.createKey("dragonborn");
  public DragonbornModifier() {
    super(DRAGONBORN);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, CriticalHitEvent.class, DragonbornModifier::onCritical);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  private static boolean isAirborne(LivingEntity living) {
    return !living.isOnGround() && !living.onClimbable() && !living.isInWater() && !living.isPassenger();
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && isAirborne(context.getEntity())) {
      modifierValue += getScaledLevel(tool, level) * 2.5;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2.5f, tooltip);
  }

  /** Boosts critical hit damage */
  private static void onCritical(CriticalHitEvent event) {
    if (event.getResult() != Result.DENY) {
      // force critical if not already critical and in the air
      LivingEntity living = event.getEntityLiving();

      // check dragonborn first, faster check
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel dragonborn = data.get(DRAGONBORN);
        if (dragonborn != null) {
          float max = dragonborn.getMax();
          if (max > 0) {
            // make it critical if we meet our simpler conditions, note this does not boost attack damage
            boolean isCritical = event.isVanillaCritical() || event.getResult() == Result.ALLOW;
            if (!isCritical && isAirborne(living)) {
              isCritical = true;
              event.setResult(Result.ALLOW);
            }

            // if we either were or became critical, time to boost
            if (isCritical) {
              // adds +10% critical hit per level
              event.setDamageModifier(event.getDamageModifier() + max * 0.05f);
            }
          }
        }
      });
    }
  }
}
