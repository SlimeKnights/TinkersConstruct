package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class MagicProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> implements ProtectionModifierHook, TooltipModifierHook {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<ModifierMaxLevel> MAGIC_DATA = TConstruct.createKey("magic_protection");
  public MagicProtectionModifier() {
    super(MAGIC_DATA);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PotionEvent.PotionAddedEvent.class, MagicProtectionModifier::onPotionStart);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.PROTECTION, TinkerHooks.TOOLTIP);
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && source.isMagic()) {
      modifierValue += modifier.getEffectiveLevel(tool) * 2.5f;
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    ProtectionModule.addResistanceTooltip(tool, this, modifier.getEffectiveLevel(tool) * 2.5f, player, tooltip);
  }

  @Override
  protected ModifierMaxLevel createData(EquipmentChangeContext context) {
    return new ModifierMaxLevel();
  }

  private static void onPotionStart(PotionEvent.PotionAddedEvent event) {
    MobEffectInstance newEffect = event.getPotionEffect();
    if (!newEffect.getEffect().isBeneficial() && !newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntityLiving();
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel magicData = data.get(MAGIC_DATA);
        if (magicData != null) {
          float max = magicData.getMax();
          if (max > 0) {
            // decrease duration by 5% per level
            int duration = (int)(newEffect.getDuration() * (1 - (max * 0.05f)));
            if (duration < 0) {
              duration = 0;
            }
            newEffect.duration = duration;
          }
        }
      });
    }
  }
}
