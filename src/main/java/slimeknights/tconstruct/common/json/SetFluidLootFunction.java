package slimeknights.tconstruct.common.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;

/**
 * Loot function to set the fluid on a dropped item
 */
public class SetFluidLootFunction extends LootFunction {
  public static final ResourceLocation ID = Util.getResource("set_fluid");

  /** Fluid to add to the item */
  private final FluidStack fluid;
  protected SetFluidLootFunction(ILootCondition[] conditionsIn, FluidStack fluid) {
    super(conditionsIn);
    this.fluid = fluid;
  }

  @Override
  protected ItemStack doApply(ItemStack stack, LootContext context) {
    return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                .map(handler -> {
                  handler.fill(fluid.copy(), FluidAction.EXECUTE);
                  return handler.getContainer();
                }).orElse(stack);
  }

  @Override
  public LootFunctionType getFunctionType() {
    return TinkerCommons.lootSetFluid;
  }

  /**
   * Creates a new builder with the given fluid
   * @param fluid  Fluid to set
   * @return  Builder instance
   */
  public static LootFunction.Builder<?> builder(FluidStack fluid) {
    return builder(conditions -> new SetFluidLootFunction(conditions, fluid));
  }

  /** Serializer logic for the function */
  public static class Serializer extends LootFunction.Serializer<SetFluidLootFunction> {
    @Override
    public void serialize(JsonObject json, SetFluidLootFunction loot, JsonSerializationContext context) {
      super.serialize(json, loot, context);
      json.add("fluid", RecipeHelper.serializeFluidStack(loot.fluid));
    }

    @Override
    public SetFluidLootFunction deserialize(JsonObject object, JsonDeserializationContext context, ILootCondition[] conditions) {
      FluidStack fluid = RecipeHelper.deserializeFluidStack(JSONUtils.getJsonObject(object, "fluid"));
      return new SetFluidLootFunction(conditions, fluid);
    }
  }
}
