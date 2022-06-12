package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class MeleeProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final UUID SPEED_UUID = UUID.fromString("6f030b1e-e9e1-11ec-8fea-0242ac120002");
  private static final TinkerDataKey<ModifierMaxLevel> KEY = TConstruct.createKey("melee_protection");

  public MeleeProtectionModifier() {
    super(KEY);
  }

  /** Checks if the damage source is blocked by this modifier */
  private static boolean doesApply(DamageSource source) {
    if (source.isBypassMagic() || source.isProjectile() || source.isBypassInvul()) {
      return false;
    }
    // if its caused by an entity, require it to simply not be thorns
    // meets most normal melee attacks, like zombies, but also means a melee fire or melee magic attack will work
    if (source.getEntity() != null) {
      return source instanceof EntityDamageSource entityDamage && !entityDamage.isThorns();
    } else {
      // for non-entity damage, require it to not be any other type
      // blocks dall damage, falling blocks, cactus, but not starving, drowning, freezing
      return !source.isBypassArmor() && !source.isFire() && !source.isMagic() && !source.isExplosion();
    }
  }

  @Override
  protected void set(ModifierMaxLevel data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    float oldMax = data.getMax();
    super.set(data, slot, scaledLevel, context);
    float newMax = data.getMax();
    // 5% bonus attack speed for the largest level
    if (oldMax != newMax) {
      AttributeInstance instance = context.getEntity().getAttribute(Attributes.ATTACK_SPEED);
      if (instance != null) {
        instance.removeModifier(SPEED_UUID);
        if (newMax != 0) {
          instance.addTransientModifier(new AttributeModifier(SPEED_UUID, "tconstruct.melee_protection", 0.03 * newMax, Operation.MULTIPLY_BASE));
        }
      }
    }
  }

  @Override
  protected void reset(ModifierMaxLevel data, EquipmentChangeContext context) {
    super.reset(data, context);
    AttributeInstance instance = context.getEntity().getAttribute(Attributes.ATTACK_SPEED);
    if (instance != null) {
      instance.removeModifier(SPEED_UUID);
    }
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (doesApply(source)) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2.0f, tooltip);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  private static class MeleeAttackSpeed extends ModifierMaxLevel {

    @Override
    public void set(EquipmentSlot slot, float level) {

    }
  }
}
