package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/** Fluid transfer info that fills a fluid into an item */
@RequiredArgsConstructor
public class FillFluidContainerTransfer implements IFluidContainerTransfer {
  public static final ResourceLocation ID = TConstruct.getResource("fill_item");

  private final Ingredient input;
  private final ItemOutput filled;
  private final FluidIngredient fluid;

  @Override
  public void addRepresentativeItems(Consumer<Item> consumer) {
    for (ItemStack stack : input.getItems()) {
      consumer.accept(stack.getItem());
    }
  }

  @Override
  public boolean matches(ItemStack stack, FluidStack fluid) {
    return input.test(stack) && this.fluid.test(fluid);
  }

  /** Gets the output filled with the given fluid */
  protected ItemStack getFilled(FluidStack drained) {
    return this.filled.get().copy();
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
      return new TransferResult(getFilled(toDrain), toDrain, true);
    }
    return null;
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("input", input.toJson());
    json.add("filled", filled.serialize());
    json.add("fluid", fluid.serialize());
    return json;
  }

  /**
   * Unique loader instance
   */
  public static final JsonDeserializer<FillFluidContainerTransfer> DESERIALIZER = new Deserializer<>(FillFluidContainerTransfer::new);

  public record Deserializer<T extends FillFluidContainerTransfer>(TriFunction<Ingredient, ItemOutput, FluidIngredient, T> factory) implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject json = element.getAsJsonObject();
      Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
      ItemOutput filled = ItemOutput.fromJson(JsonHelper.getElement(json, "filled"));
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      return factory.apply(input, filled, fluid);
    }
  }
}
