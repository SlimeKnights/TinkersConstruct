package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to add knockback to a melee attack
 * @param entity     Filter on entities to receive knockback
 * @param formula    Formula to compute the knockback amount
 */
public record KnockbackModule(IJsonPredicate<LivingEntity> entity, ModifierFormula formula, ModifierModuleCondition condition) implements MeleeHitModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_HIT);
  /** Variables for the modifier formula */
  private static final String[] VARIABLES = { "level", "knockback" };
  /** Fallback for the modifier formula */
  private static final FallbackFormula FALLBACK_FORMULA = FallbackFormula.ADD;

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (this.condition.matches(tool, modifier)) {
      // might want to consider an entity predicate here, this special casing is a bit odd
      LivingEntity target = context.getLivingTarget();
      if (target == null ? entity == LivingEntityPredicate.ANY : entity.matches(target)) {
        return formula.apply(formula.computeLevel(tool, modifier), knockback);
      }
    }
    return knockback;
  }


  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<KnockbackModule> LOADER = new IGenericLoader<>() {
    @Override
    public KnockbackModule deserialize(JsonObject json) {
      return new KnockbackModule(
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity"),
        ModifierFormula.deserialize(json, VARIABLES, FALLBACK_FORMULA),
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(KnockbackModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.entity));
      object.formula.serialize(json, VARIABLES);
    }

    @Override
    public KnockbackModule fromNetwork(FriendlyByteBuf buffer) {
      return new KnockbackModule(
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        ModifierFormula.fromNetwork(buffer, VARIABLES.length, FALLBACK_FORMULA),
        ModifierModuleCondition.fromNetwork(buffer)
      );
    }

    @Override
    public void toNetwork(KnockbackModule object, FriendlyByteBuf buffer) {
      LivingEntityPredicate.LOADER.toNetwork(object.entity, buffer);
      object.formula.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  public static class Builder extends ModifierFormula.Builder<Builder,KnockbackModule> {
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;

    private Builder() {
      super(VARIABLES);
    }

    @Override
    protected KnockbackModule build(ModifierFormula formula) {
      return new KnockbackModule(entity, formula, condition);
    }
  }
}
