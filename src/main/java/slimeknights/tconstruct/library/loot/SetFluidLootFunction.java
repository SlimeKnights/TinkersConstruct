package slimeknights.tconstruct.library.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;

/** @deprecated use {@link slimeknights.mantle.loot.function.SetFluidLootFunction} */
@Deprecated
public class SetFluidLootFunction extends slimeknights.mantle.loot.function.SetFluidLootFunction {
  public static final ResourceLocation ID = TConstruct.getResource("set_fluid");
  public static final Serializer SERIALIZER = new Serializer();
  private final FluidStack fluid;
  protected SetFluidLootFunction(ILootCondition[] conditionsIn, FluidStack fluid) {
    super(conditionsIn, fluid);
    this.fluid = fluid;
    TConstruct.LOG.warn("Using deprecated loot function '{}', use 'mantle:set_fluid' instead", ID);
  }

  @Override
  public LootFunctionType getFunctionType() {
    return TinkerCommons.lootSetFluid;
  }

  /** Serializer logic for the function */
  private static class Serializer extends LootFunction.Serializer<SetFluidLootFunction> {
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
