package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

/** Fluid transfer info that empties a fluid from an item */
@RequiredArgsConstructor
public class EmptyFluidContainerTransfer implements IFluidContainerTransfer {
  private final Ingredient input;
  private final ItemOutput filled;
  private final FluidStack fluid;

  @Override
  public boolean matches(ItemStack stack, FluidStack fluid) {
    return input.test(stack);
  }

  @Override
  public TransferResult transfer(ItemStack stack, FluidStack fluid, IFluidHandler handler) {
    int simulated = handler.fill(this.fluid.copy(), FluidAction.SIMULATE);
    if (simulated == this.fluid.getAmount()) {
      int actual = handler.fill(this.fluid.copy(), FluidAction.EXECUTE);
      if (actual > 0) {
        if (actual != this.fluid.getAmount()) {
          TConstruct.LOG.error("Wrong amount filled from {}, expected {}, filled {}", stack.getItem().getRegistryName(), this.fluid.getAmount(), actual);
        }
        return new TransferResult(filled.get().copy(), this.fluid, false);
      }
    }
    return null;
  }

  @Override
  public IGenericLoader<? extends IFluidContainerTransfer> getLoader() {
    return LOADER;
  }

  /** Unique loader instance */
  public static final IGenericLoader<EmptyFluidContainerTransfer> LOADER = new IGenericLoader<>() {
    @Override
    public EmptyFluidContainerTransfer deserialize(JsonObject json) {
      Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
      ItemOutput filled = ItemOutput.fromJson(JsonHelper.getElement(json, "filled"));
      FluidStack fluid = RecipeHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "fluid"));
      return new EmptyFluidContainerTransfer(input, filled, fluid);
    }

    @Override
    public void serialize(EmptyFluidContainerTransfer object, JsonObject json) {
      json.add("input", object.input.toJson());
      json.add("filled", object.filled.serialize());
      json.add("fluid", RecipeHelper.serializeFluidStack(object.fluid));
    }

    @Override
    public EmptyFluidContainerTransfer fromNetwork(FriendlyByteBuf buffer) {
      Ingredient input = Ingredient.fromNetwork(buffer);
      ItemOutput filled = ItemOutput.read(buffer);
      FluidStack fluid = buffer.readFluidStack();
      return new EmptyFluidContainerTransfer(input, filled, fluid);
    }

    @Override
    public void toNetwork(EmptyFluidContainerTransfer object, FriendlyByteBuf buffer) {
      object.input.toNetwork(buffer);
      object.filled.write(buffer);
      buffer.writeFluidStack(object.fluid);
    }
  };
}
