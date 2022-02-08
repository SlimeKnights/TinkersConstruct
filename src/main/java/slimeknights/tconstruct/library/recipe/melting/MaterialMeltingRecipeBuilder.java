package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Builder for a recipe to melt a dynamic part material item
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialMeltingRecipeBuilder extends AbstractRecipeBuilder<MaterialMeltingRecipeBuilder> {
  private final MaterialVariantId inputId;
  private final int temperature;
  private final FluidStack result;

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, int temperature, FluidStack result) {
    if (temperature < 0) {
      throw new IllegalArgumentException("Invalid temperature " + temperature + ", must be 0 or greater");
    }
    return new MaterialMeltingRecipeBuilder(materialId, temperature, result);
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialVariantId materialId, FluidStack result) {
    return material(materialId, result.getFluid().getAttributes().getTemperature(result) - 300, result);
  }

  /** Creates a recipe using the fluids temperature */
  public static MaterialMeltingRecipeBuilder material(MaterialId materialId, Fluid result, int amount) {
    return material(materialId, new FluidStack(result, amount));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, inputId.getId());
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementID = this.buildOptionalAdvancement(id, "melting");
    consumer.accept(new Result(id, advancementID));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("input", inputId.toString());
      json.addProperty("temperature", temperature);
      json.add("result", RecipeHelper.serializeFluidStack(result));
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerSmeltery.materialMeltingSerializer.get();
    }
  }
}
