package slimeknights.tconstruct.library.modifiers.modules.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.AttributeModuleBuilder;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Adds an attribute modifier to the mob before hitting, then removes the modifier after hitting.
 * @param unique     Unique string used to generate the UUID and as the attribute name
 * @param attribute  Attribute to apply
 * @param uuid       UUID generated via {@link UUID#nameUUIDFromBytes(byte[])}
 * @param operation  Attribute operation
 * @param amount     Amount of the attribute to apply
 * @param condition  Standard modifier conditions
 */
public record MeleeAttributeModule(String unique, Attribute attribute, UUID uuid, Operation operation, LevelingValue amount, ModifierModuleCondition condition) implements ModifierModule, MeleeHitModifierHook, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_HIT);
  public static final RecordLoadable<MeleeAttributeModule> LOADER = RecordLoadable.create(
    StringLoadable.DEFAULT.requiredField("unique", MeleeAttributeModule::unique),
    Loadables.ATTRIBUTE.requiredField("attribute", MeleeAttributeModule::attribute),
    TinkerLoadables.OPERATION.requiredField("operation", MeleeAttributeModule::operation),
    LevelingValue.LOADABLE.directField(MeleeAttributeModule::amount),
    ModifierModuleCondition.FIELD,
    MeleeAttributeModule::new);

  public MeleeAttributeModule(String unique, Attribute attribute, Operation operation, LevelingValue amount, ModifierModuleCondition condition) {
    this(unique, attribute, UUID.nameUUIDFromBytes(unique.getBytes()), operation, amount, condition);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (condition.matches(tool, modifier)) {
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        AttributeInstance instance = target.getAttribute(attribute);
        if (instance != null) {
          instance.addTransientModifier(new AttributeModifier(uuid, unique, amount.compute(tool, modifier), operation));
        }
      }
    }
    return knockback;
  }

  private void removeAttribute(@Nullable LivingEntity target) {
    if (target != null) {
      AttributeInstance instance = target.getAttribute(attribute);
      if (instance != null) {
        instance.removeModifier(uuid);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }


  /** Creates a new builder instance */
  public static Builder builder(Attribute attribute, Operation operation) {
    return new Builder(attribute, operation);
  }

  public static class Builder extends AttributeModuleBuilder<Builder,MeleeAttributeModule> {
    private Builder(Attribute attribute, Operation operation) {
      super(attribute, operation);
    }

    @Override
    public MeleeAttributeModule amount(float flat, float eachLevel) {
      if (unique == null) {
        throw new IllegalStateException("Must set unique for attributes");
      }
      return new MeleeAttributeModule(unique, attribute, operation, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
