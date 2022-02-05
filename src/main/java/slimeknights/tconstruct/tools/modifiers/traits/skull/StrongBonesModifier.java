package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class StrongBonesModifier extends TotalArmorLevelModifier {
  public static final SpillingEffect SPILLING_EFFECT = new SpillingEffect();
  public static final IGenericLoader<SpillingEffect> SPILLING_EFFECT_LOADER = new SingletonLoader<>(SPILLING_EFFECT);
  private static final TinkerDataKey<Integer> STRONG_BONES = TConstruct.createKey("strong_bones");
  /** Key for modifiers that are boosted by drinking milk */
  public static final TinkerDataKey<Integer> CALCIFIABLE = TConstruct.createKey("calcifable");
  public StrongBonesModifier() {
    super(STRONG_BONES, true);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingEntityUseItemEvent.Finish.class, StrongBonesModifier::onItemFinishUse);
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

  private static void drinkMilk(LivingEntity living, int duration) {
    if (ModifierUtil.getTotalModifierLevel(living, STRONG_BONES) > 0) {
      MobEffectInstance effect = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration);
      effect.getCurativeItems().clear();
      effect.getCurativeItems().add(new ItemStack(living.getItemBySlot(EquipmentSlot.HEAD).getItem()));
      living.addEffect(effect);
    }
    if (ModifierUtil.getTotalModifierLevel(living, CALCIFIABLE) > 0) {
      TinkerModifiers.calcifiedEffect.get().apply(living, duration, 0, true);
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
    public IGenericLoader<?> getLoader() {
      return SPILLING_EFFECT_LOADER;
    }
  }
}
