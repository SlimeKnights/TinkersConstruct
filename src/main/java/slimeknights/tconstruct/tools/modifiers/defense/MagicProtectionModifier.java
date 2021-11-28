package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

import java.util.List;

public class MagicProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<ModifierMaxLevel> MAGIC_DATA = TConstruct.createKey("magic_protection");
  public MagicProtectionModifier() {
    super(0xF4D362, MAGIC_DATA);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PotionEvent.PotionAddedEvent.class, MagicProtectionModifier::onPotionStart);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && source.isMagicDamage()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  private static void onPotionStart(PotionEvent.PotionAddedEvent event) {
    LivingEntity living = event.getEntityLiving();
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      ModifierMaxLevel magicData = data.get(MAGIC_DATA);
      if (magicData != null) {
        float max = magicData.getMax();
        if (max > 0) {
          EffectInstance newEffect = event.getPotionEffect();
          if (!newEffect.getPotion().isBeneficial()) {
            // decrease duration by 5% per level
            int duration = (int)(newEffect.getDuration() * (1 - (max * 0.05f)));
            if (duration < 0) {
              duration = 0;
            }
            newEffect.duration = duration;
          }
        }
      }
    });
  }
}
