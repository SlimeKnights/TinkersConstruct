package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.Objects;

/** Spilling effect to remove a specific effect */
@RequiredArgsConstructor
public class RemoveEffectSpillingEffect implements ISpillingEffect {
  private final MobEffect effect;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity living = context.getLivingTarget();
    if (living != null) {
      living.removeEffect(effect);
    }
  }

  @Override
  public IGenericLoader<? extends ISpillingEffect> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<RemoveEffectSpillingEffect> LOADER = new IGenericLoader<>() {
    @Override
    public RemoveEffectSpillingEffect deserialize(JsonObject json) {
      return new RemoveEffectSpillingEffect(JsonUtils.getAsEntry(ForgeRegistries.MOB_EFFECTS, json, "effect"));
    }

    @Override
    public void serialize(RemoveEffectSpillingEffect object, JsonObject json) {
      json.addProperty("effect", Objects.requireNonNull(object.effect.getRegistryName()).toString());
    }

    @Override
    public RemoveEffectSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      return new RemoveEffectSpillingEffect(buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS));
    }

    @Override
    public void toNetwork(RemoveEffectSpillingEffect object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, object.effect);
    }
  };
}
