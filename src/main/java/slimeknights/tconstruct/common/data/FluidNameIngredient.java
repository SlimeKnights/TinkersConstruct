package slimeknights.tconstruct.common.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;

import java.util.List;

/** Datagen fluid ingredient to create an ingredient matching a fluid from another mod, should not be used outside datagen */
@RequiredArgsConstructor(staticName = "of")
public class FluidNameIngredient extends FluidIngredient {
  private final ResourceLocation fluidName;
  private final int amount;

  @Override
  public boolean test(Fluid fluid) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getAmount(Fluid fluid) {
    return amount;
  }

  @Override
  protected List<FluidStack> getAllFluids() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonElement serialize() {
    JsonObject object = new JsonObject();
    object.addProperty("name", this.fluidName.toString());
    object.addProperty("amount", this.amount);
    return object;
  }
}
