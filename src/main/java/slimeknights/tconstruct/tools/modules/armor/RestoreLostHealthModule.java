package slimeknights.tconstruct.tools.modules.armor;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module which
 * @param percent          Percentage of health to steal.
 * @param effectLevel      Level of regeneration to apply.
 * @param durabilityUsage  Amount of durability to use when stealing health.
 * @param chance           Chance of applying.
 * @param attacker         Condition on the attacker that dealt the damage.
 * @param defender         Condition on the defender who will heal.
 * @param condition        Condition on the tool and modifier level.
 */
public record RestoreLostHealthModule(LevelingValue percent, LevelingInt effectLevel, LevelingInt durabilityUsage, LevelingValue chance, IJsonPredicate<LivingEntity> attacker, IJsonPredicate<LivingEntity> defender, ModifierCondition<IToolStackView> condition) implements ModifierModule, ModifyDamageModifierHook, TooltipModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RestoreLostHealthModule>defaultHooks(ModifierHooks.MODIFY_DAMAGE, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<RestoreLostHealthModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("percentage", RestoreLostHealthModule::percent),
    LevelingInt.LOADABLE.requiredField("effect_level", RestoreLostHealthModule::effectLevel),
    LevelingInt.LOADABLE.requiredField("durability_usage", RestoreLostHealthModule::durabilityUsage),
    LevelingValue.LOADABLE.requiredField("chance", RestoreLostHealthModule::chance),
    LivingEntityPredicate.LOADER.defaultField("attacker", RestoreLostHealthModule::attacker),
    LivingEntityPredicate.LOADER.defaultField("defender", RestoreLostHealthModule::defender),
    ModifierCondition.TOOL_FIELD,
    RestoreLostHealthModule::new);

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Integer getPriority() {
    return 10;
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // works like vanilla, if multiple pieces have it we get the highest effect
    LivingEntity defender = context.getEntity();
    if (condition.matches(tool, modifier) && this.defender.matches(defender) && TinkerPredicate.matches(this.attacker, source.getEntity())) {
      // no shield doubling as shields prevent the damage, better than any healing
      float level = modifier.getEffectiveLevel();
      int effectLevel = this.effectLevel.compute(level);
      if (effectLevel > 0 && defender.getRandom().nextFloat() < this.chance.compute(level)) {
        // heals slowly over time
        int heal = (int)(this.percent.compute(level) * amount);
        if (heal > 0) {
          // regen restores 1 health every 50 ticks at level 1, restores faster at higher levels
          defender.addEffect(new MobEffectInstance(MobEffects.REGENERATION, heal * (50 >> (effectLevel - 1))));
          defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);

          // extra damage for running based on level
          int durabilityUsage = this.durabilityUsage.compute(level);
          if (durabilityUsage > 0) {
            ToolDamageUtil.damageAnimated(tool, durabilityUsage, defender, slotType, modifier.getId());
          }
        }
      }
    }
    return amount;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (TinkerPredicate.matchesInTooltip(this.defender, player, tooltipKey)) {
      float percent = this.percent.compute(entry.getEffectiveLevel());
      if (percent > 0) {
        Modifier modifier = entry.getModifier();
        tooltip.add(modifier.applyStyle(Component.literal(Util.PERCENT_FORMAT.format(percent) + " ").append(Component.translatable(modifier.getTranslationKey() + ".restore"))));
      }
    }
  }


  /* Builder */

  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<RestoreLostHealthModule> {
    private LevelingInt durabilityUsage = LevelingInt.eachLevel(1);
    private LevelingValue chance = LevelingValue.flat(0.15f);
    private LevelingInt effectLevel = LevelingInt.flat(1);
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> defender = LivingEntityPredicate.ANY;

    private Builder() {}

    @Override
    public RestoreLostHealthModule amount(float flat, float eachLevel) {
      return new RestoreLostHealthModule(new LevelingValue(flat, eachLevel), effectLevel, durabilityUsage, chance, attacker, defender, condition);
    }
  }
}
