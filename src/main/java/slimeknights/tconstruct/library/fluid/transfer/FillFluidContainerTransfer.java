package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

/** Fluid transfer info that fills a fluid into an item */
@RequiredArgsConstructor
public class FillFluidContainerTransfer implements IFluidContainerTransfer {
  private final Ingredient input;
  private final ItemOutput filled;
  private final FluidIngredient fluid;

  @Override
  public boolean matches(ItemStack stack, FluidStack fluid) {
    return input.test(stack) && this.fluid.test(fluid);
  }

  @Nullable
  @Override
  public TransferResult transfer(ItemStack stack, FluidStack fluid, IFluidHandler handler) {
    int amount = this.fluid.getAmount(fluid.getFluid());
    FluidStack toDrain = new FluidStack(fluid, amount);
    FluidStack simulated = handler.drain(toDrain.copy(), FluidAction.SIMULATE);
    if (simulated.getAmount() == amount) {
      FluidStack actual = handler.drain(toDrain.copy(), FluidAction.EXECUTE);
      if (actual.getAmount() != amount) {
        TConstruct.LOG.error("Wrong amount drained from {}, expected {}, filled {}", stack.getItem().getRegistryName(), fluid.getAmount(), actual.getAmount());
      }
      return new TransferResult(this.filled.get().copy(), toDrain, true);
    }
    return null;
  }

  @Override
  public IGenericLoader<? extends IFluidContainerTransfer> getLoader() {
    return LOADER;
  }

  /** Unique loader instance */
  public static final IGenericLoader<FillFluidContainerTransfer> LOADER = new IGenericLoader<>() {
    @Override
    public FillFluidContainerTransfer deserialize(JsonObject json) {
      Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
      ItemOutput filled = ItemOutput.fromJson(JsonHelper.getElement(json, "filled"));
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      return new FillFluidContainerTransfer(input, filled, fluid);
    }

    @Override
    public void serialize(FillFluidContainerTransfer object, JsonObject json) {
      json.add("input", object.input.toJson());
      json.add("filled", object.filled.serialize());
      json.add("fluid", object.fluid.serialize());
    }

    @Override
    public FillFluidContainerTransfer fromNetwork(FriendlyByteBuf buffer) {
      Ingredient input = Ingredient.fromNetwork(buffer);
      ItemOutput filled = ItemOutput.read(buffer);
      FluidIngredient fluid = FluidIngredient.read(buffer);
      return new FillFluidContainerTransfer(input, filled, fluid);
    }

    @Override
    public void toNetwork(FillFluidContainerTransfer object, FriendlyByteBuf buffer) {
      object.input.toNetwork(buffer);
      object.filled.write(buffer);
      object.fluid.write(buffer);
    }
  };
}
