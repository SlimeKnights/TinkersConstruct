package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Module to increase protection against the given source
 * @param source    Source to protect against
 * @param entity    Conditions on the entity wearing the armor
 * @param amount    Amount of damage to block
 * @param subtract  Enchantment also part of this modifier, subtracted from the protection amount to prevent redundancies
 * @param condition Modifier module conditions
 */
public record ProtectionModule(IJsonPredicate<DamageSource> source, IJsonPredicate<LivingEntity> entity, LevelingValue amount, @Nullable Enchantment subtract, ModifierModuleCondition condition) implements ProtectionModifierHook, TooltipModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.PROTECTION, TinkerHooks.TOOLTIP);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (condition.matches(tool, modifier) && this.source.matches(source) && this.entity.matches(context.getEntity())) {
      // if this modifier also has an enchantment, subtract out that enchantment value
      // used for fire protection to subtract out the 2 protection from vanilla
      if (subtract != null && LogicHelper.isInList(subtract.slots, slotType)) {
        float scaledLevel = modifier.getEffectiveLevel(tool);
        modifierValue += amount.compute(scaledLevel) - subtract.getDamageProtection(Mth.floor(scaledLevel), source);
      } else {
        modifierValue += amount.compute(tool, modifier);
      }
    }
    return modifierValue;
  }

  /** Adds the tooltip for the module */
  public static void addResistanceTooltip(IToolStackView tool, Modifier modifier, float amount, @Nullable Player player, List<Component> tooltip) {
    float cap;
    if (player != null) {
      cap = ProtectionModifierHook.getProtectionCap(player.getCapability(TinkerDataCapability.CAPABILITY));
    } else {
      cap = Math.min(20f + tool.getModifierLevel(TinkerModifiers.boundless.getId()) * 2.5f, 20 * 0.95f);
    }
    tooltip.add(modifier.applyStyle(
      new TextComponent(Util.PERCENT_BOOST_FORMAT.format(Math.min(amount, cap) / 25f))
        .append(" ").append(new TranslatableComponent(modifier.getTranslationKey() + ".resistance"))));
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier)) {
      addResistanceTooltip(tool, modifier.getModifier(), amount.compute(tool, modifier), player, tooltip);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ProtectionModule> LOADER = new IGenericLoader<>() {
    @Override
    public ProtectionModule deserialize(JsonObject json) {
      Enchantment enchantment = null;
      if (json.has("subtract_enchantment")) {
        enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, json, "subtract_enchantment");
      }
      return new ProtectionModule(
        DamageSourcePredicate.LOADER.getAndDeserialize(json, "damage_source"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "wearing_entity"),
        LevelingValue.deserialize(json), enchantment, ModifierModuleCondition.deserializeFrom(json));
    }

    @Override
    public void serialize(ProtectionModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("damage_source", DamageSourcePredicate.LOADER.serialize(object.source));
      json.add("wearing_entity", LivingEntityPredicate.LOADER.serialize(object.entity));
      object.amount.serialize(json);
      if (object.subtract != null) {
        json.addProperty("subtract_enchantment", Objects.requireNonNull(object.subtract.getRegistryName()).toString());
      }
    }

    @Override
    public ProtectionModule fromNetwork(FriendlyByteBuf buffer) {
      Enchantment enchantment = null;
      if (buffer.readBoolean()) {
        enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      }
      return new ProtectionModule(
        DamageSourcePredicate.LOADER.fromNetwork(buffer), LivingEntityPredicate.LOADER.fromNetwork(buffer),
        LevelingValue.fromNetwork(buffer), enchantment, ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(ProtectionModule object, FriendlyByteBuf buffer) {
      if (object.subtract != null) {
        buffer.writeBoolean(true);
        buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.subtract);
      } else {
        buffer.writeBoolean(false);
      }
      DamageSourcePredicate.LOADER.toNetwork(object.source, buffer);
      LivingEntityPredicate.LOADER.toNetwork(object.entity, buffer);
      object.amount.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /* Creates a new builder instance */
  public static Builder source(IJsonPredicate<DamageSource> source) {
    return new Builder(source);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> implements LevelingValue.Builder<ProtectionModule> {
    private final IJsonPredicate<DamageSource> source;
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;
    private Enchantment subtract;

    @Override
    public ProtectionModule amount(float flat, float eachLevel) {
      return new ProtectionModule(source, entity, new LevelingValue(flat, eachLevel), subtract, condition);
    }
  }
}
