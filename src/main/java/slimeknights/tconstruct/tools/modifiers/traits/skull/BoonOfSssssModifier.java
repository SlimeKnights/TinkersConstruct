package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BoonOfSssssModifier extends NoLevelsModifier implements EquipmentChangeModifierHook {
  public BoonOfSssssModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, MobEffectEvent.Added.class, this::onPotionStart);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }

  /** Called when the potion effects start to apply this effect */
  private void onPotionStart(MobEffectEvent.Added event) {
    MobEffectInstance newEffect = event.getEffectInstance();
    if (newEffect.getEffect().isBeneficial() && !newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntity();
      // strong bones has to be the helmet as we use it for curing
      // TODO 1.20: can use the new cure effects to make this work in any slot
      ItemStack helmet = living.getItemBySlot(EquipmentSlot.HEAD);
      if (ModifierUtil.getModifierLevel(helmet, this.getId()) > 0) {
        newEffect.duration *= 1.25f;
        newEffect.getCurativeItems().add(new ItemStack(helmet.getItem()));
      }
    }
  }
}
