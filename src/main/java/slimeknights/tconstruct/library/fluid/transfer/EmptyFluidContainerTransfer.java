package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.function.Consumer;

/** Fluid transfer info that empties a fluid from an item */
@RequiredArgsConstructor
public class EmptyFluidContainerTransfer implements IFluidContainerTransfer {
  public static final ResourceLocation ID = TConstruct.getResource("empty_item");

  private final Ingredient input;
  private final ItemOutput filled;
  private final FluidStack fluid;

  @Override
  public void addRepresentativeItems(Consumer<Item> consumer) {
    for (ItemStack stack : input.getItems()) {
      consumer.accept(stack.getItem());
    }
  }

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
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("input", input.toJson());
    json.add("filled", filled.serialize());
    json.add("fluid", RecipeHelper.serializeFluidStack(fluid));
    return json;
  }

  /** Unique loader instance */
  public static final JsonDeserializer<EmptyFluidContainerTransfer> DESERIALIZER = (element, typeOfT, context) -> {
    JsonObject json = element.getAsJsonObject();
    Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
    ItemOutput filled = ItemOutput.fromJson(JsonHelper.getElement(json, "filled"));
    FluidStack fluid = RecipeHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "fluid"));
    return new EmptyFluidContainerTransfer(input, filled, fluid);
  };
}
