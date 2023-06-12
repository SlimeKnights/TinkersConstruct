package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

public record ConditionalDamageModule(IJsonPredicate<LivingEntity> predicate, float damageBonus) implements MeleeDamageModifierHook, TooltipModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_DAMAGE, TinkerHooks.TOOLTIP);

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity target = context.getLivingTarget();
    if (target != null && predicate.matches(target)) {
      damage += modifier.getEffectiveLevel(tool) * damageBonus * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    TooltipModifierHook.addDamageBoost(tool, modifier, damageBonus, tooltip);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ConditionalDamageModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalDamageModule deserialize(JsonObject json) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity");
      float damage = GsonHelper.getAsFloat(json, "damage");
      return new ConditionalDamageModule(predicate, damage);
    }

    @Override
    public void serialize(ConditionalDamageModule object, JsonObject json) {
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.predicate));
      json.addProperty("damage", object.damageBonus);
    }

    @Override
    public ConditionalDamageModule fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.fromNetwork(buffer);
      float damage = buffer.readFloat();
      return new ConditionalDamageModule(predicate, damage);
    }

    @Override
    public void toNetwork(ConditionalDamageModule object, FriendlyByteBuf buffer) {
      LivingEntityPredicate.LOADER.toNetwork(object.predicate, buffer);
      buffer.writeFloat(object.damageBonus);
    }
  };
}
