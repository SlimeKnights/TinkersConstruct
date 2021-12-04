package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffectLoader;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class StrongBonesModifier extends TotalArmorLevelModifier {
  public static final SpillingEffect SPILLING_EFFECT = new SpillingEffect();
  public static final ISpillingEffectLoader<SpillingEffect> SPILLING_EFFECT_LOADER = new ISpillingEffectLoader.Singleton<>(SPILLING_EFFECT);
  private static final TinkerDataKey<Integer> STRONG_BONES = TConstruct.createKey("strong_bones");
  public StrongBonesModifier() {
    super(-1, STRONG_BONES, true);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingEntityUseItemEvent.Finish.class, StrongBonesModifier::onItemFinishUse);
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

  private static void drinkMilk(LivingEntity living, int duration) {
    if (ModifierUtil.getTotalModifierLevel(living, STRONG_BONES) > 0) {
      EffectInstance effect = new EffectInstance(Effects.RESISTANCE, duration);
      effect.getCurativeItems().clear();
      effect.getCurativeItems().add(new ItemStack(living.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem()));
      living.addPotionEffect(effect);
    }
  }

  /** Called when you finish drinking milk */
  private static void onItemFinishUse(LivingEntityUseItemEvent.Finish event) {
    LivingEntity living = event.getEntityLiving();
    if (event.getItem().getItem() == Items.MILK_BUCKET) {
      drinkMilk(living, 1200);
    }
  }

  /** Spilling effect hook */
  public static class SpillingEffect implements ISpillingEffect {
    private SpillingEffect() {}

    @Override
    public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        drinkMilk(target, (int)(400 * scale));
      }
    }

    @Override
    public ISpillingEffectLoader<?> getLoader() {
      return SPILLING_EFFECT_LOADER;
    }
  }
}
