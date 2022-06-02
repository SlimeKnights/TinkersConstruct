package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.TConstruct;

/** Fluid transfer info that fills a fluid into an item, copying its NBT */
public class FillFluidWithNBTTransfer extends FillFluidContainerTransfer {
  public static final ResourceLocation ID = TConstruct.getResource("fill_nbt");
  public FillFluidWithNBTTransfer(Ingredient input, ItemOutput filled, FluidIngredient fluid) {
    super(input, filled, fluid);
  }

  @Override
  protected ItemStack getFilled(FluidStack drained) {
    ItemStack filled = super.getFilled(drained);
    if (drained.hasTag()) {
      filled.setTag(drained.getTag().copy());
    }
    return filled;
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = super.serialize(context);
    json.addProperty("type", ID.toString());
    return json;
  }

  /**
   * Unique loader instance
   */
  public static final JsonDeserializer<FillFluidWithNBTTransfer> DESERIALIZER = new Deserializer<>(FillFluidWithNBTTransfer::new);
}
