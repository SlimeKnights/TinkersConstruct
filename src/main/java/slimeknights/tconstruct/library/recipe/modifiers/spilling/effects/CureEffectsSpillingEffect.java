package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

import java.util.Objects;

/** Effect to clear all milk based potion effects */
@RequiredArgsConstructor
public class CureEffectsSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  /** Stack used for curing, standard is milk bucket */
  private final ItemStack stack;

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.curePotionEffects(stack);
    }
  }

  @Override
  public IGenericLoader<? extends ISpillingEffect> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<CureEffectsSpillingEffect> {
    @Override
    public CureEffectsSpillingEffect deserialize(JsonObject json) {
      ItemStack curativeItem = CraftingHelper.getItemStack(json, true);
      return new CureEffectsSpillingEffect(curativeItem);
    }

    @Override
    public CureEffectsSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      ItemStack curativeItem = buffer.readItem();
      return new CureEffectsSpillingEffect(curativeItem);
    }

    @Override
    public void serialize(CureEffectsSpillingEffect effect, JsonObject json) {
      json.addProperty("item", Objects.requireNonNull(effect.stack.getItem().getRegistryName()).toString());
      CompoundTag nbt = effect.stack.getTag();
      if (nbt != null) {
        json.addProperty("nbt", nbt.toString());
      }
    }

    @Override
    public void toNetwork(CureEffectsSpillingEffect effect, FriendlyByteBuf buffer) {
      buffer.writeItem(effect.stack);
    }
  }
}
