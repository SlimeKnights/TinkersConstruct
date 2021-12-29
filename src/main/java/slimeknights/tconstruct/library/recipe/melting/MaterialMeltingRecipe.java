package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to melt all castable tool parts of a given material
 */
@RequiredArgsConstructor
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<MeltingRecipe> {
  @Getter
  private final ResourceLocation id;
  private final MaterialId inputId;
  private final int temperature;
  private final FluidStack result;

  private IMaterial input;

  /** Gets the input material for this recipe */
  public IMaterial getInput() {
    if (input == null) {
      input = MaterialRegistry.getMaterial(inputId);
    }
    return input;
  }

  @Override
  public boolean matches(IMeltingInventory inv, Level worldIn) {
    IMaterial input = getInput();
    if (input == IMaterial.UNKNOWN) {
      return false;
    }
    ItemStack stack = inv.getStack();
    if (stack.isEmpty() || MaterialCastingLookup.getItemCost(stack.getItem()) == 0) {
      return false;
    }
    return IMaterialItem.getMaterialFromStack(stack) == input;
  }

  @Override
  public int getTemperature(IMeltingInventory inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingInventory inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return IMeltingRecipe.calcTimeForAmount(temperature, result.getAmount() * cost);
  }

  @Override
  public FluidStack getOutput(IMeltingInventory inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return new FluidStack(result, result.getAmount() * cost);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }


  /* JEI display */
  private List<MeltingRecipe> multiRecipes = null;

  @Override
  public List<MeltingRecipe> getRecipes() {
    if (multiRecipes == null) {
      if (getInput().isHidden()) {
        multiRecipes = Collections.emptyList();
      } else {
        // 1 recipe for each part
        multiRecipes = MaterialCastingLookup
          .getAllItemCosts().stream()
          .filter(entry -> entry.getKey().canUseMaterial(getInput()))
          .map(entry -> {
            FluidStack output = this.result;
            if (entry.getIntValue() != 1) {
              output = new FluidStack(output, output.getAmount() * entry.getIntValue());
            }
            return new MeltingRecipe(id, "", MaterialIngredient.fromItem(entry.getKey(), inputId), output, temperature,
                                     IMeltingRecipe.calcTime(temperature, output.getAmount()), Collections.emptyList());
          }).collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }

  public static class Serializer extends LoggingRecipeSerializer<MaterialMeltingRecipe> {
    @Override
    public MaterialMeltingRecipe fromJson(ResourceLocation id, JsonObject json) {
      MaterialId inputId = new MaterialId(GsonHelper.getAsString(json, "input"));
      int temperature = GsonHelper.getAsInt(json, "temperature");
      FluidStack output = RecipeHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "result"));
      return new MaterialMeltingRecipe(id, inputId, temperature, output);
    }

    @Nullable
    @Override
    protected MaterialMeltingRecipe readSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      MaterialId inputId = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
      int temperature = buffer.readInt();
      FluidStack output = FluidStack.readFromPacket(buffer);
      return new MaterialMeltingRecipe(id, inputId, temperature, output);
    }

    @Override
    protected void writeSafe(FriendlyByteBuf buffer, MaterialMeltingRecipe recipe) {
      buffer.writeUtf(recipe.inputId.toString());
      buffer.writeInt(recipe.temperature);
      recipe.result.writeToPacket(buffer);
    }
  }
}
