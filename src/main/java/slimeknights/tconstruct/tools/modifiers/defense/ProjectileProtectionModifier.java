package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import java.util.List;

public class ProjectileProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<ModifierMaxLevel> PROJECTILE_DATA = TConstruct.createKey("projectile_protection");
  public ProjectileProtectionModifier() {
    super(0xE2A856, PROJECTILE_DATA);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingVisibilityEvent.class, ProjectileProtectionModifier::livingVisibility);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && source.isProjectile()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }

  /** Reduces visibility to mobs */
  private static void livingVisibility(LivingVisibilityEvent event) {
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      ModifierMaxLevel projData = data.get(PROJECTILE_DATA);
      if (projData != null) {
        float max = projData.getMax();
        if (max > 0) {
          // reduces visibility by 5% per level
          event.modifyVisibility(Math.max(0, 1 - (max * 0.05)));
        }
      }
    });
  }
}
