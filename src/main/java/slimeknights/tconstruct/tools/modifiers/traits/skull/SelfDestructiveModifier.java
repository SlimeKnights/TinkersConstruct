package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.effect.NoMilkEffect;

import javax.annotation.Nullable;

public class SelfDestructiveModifier extends NoLevelsModifier implements IArmorInteractModifier {
  /** Self damage source */
  private static final DamageSource SELF_DESTRUCT = (new DamageSource(TConstruct.prefix("self_destruct"))).bypassArmor().setExplosion();

  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    if (player.isShiftKeyDown()) {
      TinkerModifiers.selfDestructiveEffect.get().apply(player, 30, 2, true);
      player.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
      return true;
    }
    return false;
  }

  @Override
  public void stopArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    player.removeEffect(TinkerModifiers.selfDestructiveEffect.get());
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    context.getEntity().removeEffect(TinkerModifiers.selfDestructiveEffect.get());
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorInteractModifier.class, this);
  }

  @Override
  protected boolean isSelfHook(ModifierHook<?> hook) {
    return hook == TinkerHooks.ARMOR_INTERACT || super.isSelfHook(hook);
  }

  /** Internal potion effect handling the explosion */
  public static class SelfDestructiveEffect extends NoMilkEffect {
    public SelfDestructiveEffect() {
      super(MobEffectCategory.HARMFUL, 0x59D24A, true);
      // make the player slow
      addAttributeModifier(Attributes.MOVEMENT_SPEED, "68ee3026-1d50-4eb4-914e-a8b05fbfdb71", -0.9f, Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
      return duration == 1;
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
      // effect level is the explosion radius
      if (!living.level.isClientSide) {
        living.level.explode(living, living.getX(), living.getY(), living.getZ(), amplifier + 1, Explosion.BlockInteraction.DESTROY);
        living.hurt(SELF_DESTRUCT, 99999);
      }
    }
  }
}
