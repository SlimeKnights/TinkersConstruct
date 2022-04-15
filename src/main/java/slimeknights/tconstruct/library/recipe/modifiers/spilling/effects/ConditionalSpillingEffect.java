package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

/** Spilling effect that conditions on the entity targeted */
@RequiredArgsConstructor
public class ConditionalSpillingEffect implements ISpillingEffect {
  private final IJsonPredicate<LivingEntity> predicate;
  private final ISpillingEffect effect;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null && predicate.matches(target)) {
      effect.applyEffects(fluid, scale, context);
    }
  }

  @Override
  public IGenericLoader<? extends ISpillingEffect> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<ConditionalSpillingEffect> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalSpillingEffect deserialize(JsonObject json) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity");
      ISpillingEffect effect = ISpillingEffect.LOADER.getAndDeserialize(json, "effect");
      return new ConditionalSpillingEffect(predicate, effect);
    }

    @Override
    public ConditionalSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.fromNetwork(buffer);
      ISpillingEffect effect = ISpillingEffect.LOADER.fromNetwork(buffer);
      return new ConditionalSpillingEffect(predicate, effect);
    }

    @Override
    public void serialize(ConditionalSpillingEffect object, JsonObject json) {
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.predicate));
      json.add("effect", ISpillingEffect.LOADER.serialize(object.effect));
    }

    @Override
    public void toNetwork(ConditionalSpillingEffect object, FriendlyByteBuf buffer) {
      LivingEntityPredicate.LOADER.toNetwork(object.predicate, buffer);
      ISpillingEffect.LOADER.toNetwork(object.effect, buffer);
    }
  };
}
