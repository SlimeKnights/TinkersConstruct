package slimeknights.tconstruct.fluids.item;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.fluid.transfer.EmptyFluidContainerTransfer;
import slimeknights.tconstruct.library.fluid.transfer.EmptyFluidWithNBTTransfer;

/** Fluid transfer info that empties a fluid from an item, copying the fluid's NBT to the stack */
public class EmptyPotionTransfer extends EmptyFluidWithNBTTransfer {
  public static final ResourceLocation ID = TConstruct.getResource("empty_potion");
  public EmptyPotionTransfer(Ingredient input, ItemOutput filled, FluidStack fluid) {
    super(input, filled, fluid);
  }

  @Override
  protected FluidStack getFluid(ItemStack stack) {
    if (PotionUtils.getPotion(stack) == Potions.WATER) {
      return new FluidStack(Fluids.WATER, fluid.getAmount());
    }
    return new FluidStack(fluid.getFluid(), fluid.getAmount(), stack.getTag());
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = super.serialize(context);
    json.addProperty("type", ID.toString());
    return json;
  }

  /** Unique loader instance */
  public static final JsonDeserializer<EmptyFluidContainerTransfer> DESERIALIZER = new Deserializer<>(EmptyPotionTransfer::new);
}
