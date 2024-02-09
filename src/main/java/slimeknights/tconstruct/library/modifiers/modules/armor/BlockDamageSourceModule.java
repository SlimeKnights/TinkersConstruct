package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to block damage of the passed sources
 * @param source  Predicate of sources to block
 */
public record BlockDamageSourceModule(IJsonPredicate<DamageSource> source, ModifierModuleCondition condition) implements DamageBlockModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.DAMAGE_BLOCK);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return condition.matches(tool, modifier) && this.source.matches(source);
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<BlockDamageSourceModule> LOADER = new IGenericLoader<>() {
    @Override
    public BlockDamageSourceModule deserialize(JsonObject json) {
      return new BlockDamageSourceModule(DamageSourcePredicate.LOADER.getAndDeserialize(json, "damage_source"), ModifierModuleCondition.deserializeFrom(json));
    }

    @Override
    public void serialize(BlockDamageSourceModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("damage_source", DamageSourcePredicate.LOADER.serialize(object.source));
    }

    @Override
    public BlockDamageSourceModule fromNetwork(FriendlyByteBuf buffer) {
      return new BlockDamageSourceModule(DamageSourcePredicate.LOADER.fromNetwork(buffer), ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(BlockDamageSourceModule object, FriendlyByteBuf buffer) {
      DamageSourcePredicate.LOADER.toNetwork(object.source, buffer);
      object.condition.toNetwork(buffer);
    }
  };

  /* Builder */

  public static Builder source(IJsonPredicate<DamageSource> source) {
    return new Builder(source);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final IJsonPredicate<DamageSource> source;

    public BlockDamageSourceModule build() {
      return new BlockDamageSourceModule(source, condition);
    }
  }
}
