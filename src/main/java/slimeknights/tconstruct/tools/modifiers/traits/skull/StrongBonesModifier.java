package slimeknights.tconstruct.tools.modifiers.traits.skull;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class StrongBonesModifier extends NoLevelsModifier {
  /** Key for modifiers that are boosted by drinking milk */
  public static final TinkerDataKey<Integer> CALCIFIABLE = TConstruct.createKey("calcifable");
  public StrongBonesModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingEntityUseItemEvent.Finish.class, StrongBonesModifier::onItemFinishUse);
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    super.onUnequip(tool, level, context);
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }

  private static void drinkMilk(LivingEntity living, int duration) {
    // strong bones has to be the helmet as we use it for curing
    // TODO 1.20: can use the new cure effects to make this work in any slot
    ItemStack helmet = living.getItemBySlot(EquipmentSlot.HEAD);
    if (ModifierUtil.getModifierLevel(helmet, TinkerModifiers.strongBones.getId()) > 0) {
      MobEffectInstance effect = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration);
      effect.getCurativeItems().clear();
      effect.getCurativeItems().add(new ItemStack(helmet.getItem()));
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


  /* Spilling effect */

  /** ID for the spilling effect */
  public static final ResourceLocation SPILLING_EFFECT_ID = TConstruct.getResource("calcified");

  /** GSON does not support anonymous classes */
  private static class SpillingEffect implements ISpillingEffect {
    @Override
    public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        drinkMilk(target, (int)(400 * scale));
      }
    }

    @Override
    public JsonObject serialize(JsonSerializationContext context) {
      return JsonUtils.withType(SPILLING_EFFECT_ID);
    }
  }
  /** Singleton instance the spilling effect */
  public static final ISpillingEffect SPILLING_EFFECT = new SpillingEffect();

  /** Loader for the spilling effect */
  public static final JsonDeserializer<ISpillingEffect> SPILLING_EFFECT_LOADER = (json, type, context) -> SPILLING_EFFECT;
}
