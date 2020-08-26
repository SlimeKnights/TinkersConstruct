package slimeknights.tconstruct.library.recipe.alloy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.library.recipe.alloy.inventory.IAlloyInventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AlloyRecipe implements IAlloyRecipe {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final List<FluidIngredient> fluidIngredients;
  @Getter
  private final FluidStack output;
  @Getter
  private final int temperature;

  @Override
  public boolean matches(IAlloyInventory inv, World worldIn) {
    if (inv.getFluidStacks().size() < fluidIngredients.size()) {// || !inv.canAccept(output)) {
      return false;
    }
    List<FluidStack> copy = new ArrayList<>(inv.getFluidStacks());
    ingredients:
    for (FluidIngredient fluidIngredient : fluidIngredients) {
      for (FluidStack candidate : copy) {
        if (fluidIngredient.test(candidate)) {
          // you might want an iterator for this, but the continue should be enough
          copy.remove(candidate);
          continue ingredients;
        }
      }
      System.out.println("No match: " + id.toString());
      return false;
    }
    System.out.println("Match: " + id.toString());
    return true;
  }

  @Override
  public int getTemperature(IAlloyInventory inv) {
    return temperature;
  }

  @Override
  public FluidStack getOutput(IAlloyInventory inv) {
    return output.copy();
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.alloySerializer.get();
  }

  /**
   * Gets a list of valid fluid inputs for this recipe, for display in jei;
   * TODO: fix
   */
  public List<FluidStack> getFluids() {
    List<FluidStack> fluids = new ArrayList<>();
    for (FluidIngredient fluidIngredient : fluidIngredients) {
      fluids.addAll(fluidIngredient.getFluids());
    }
    return fluids;
  }

  /**
   * Serializer for {@link AlloyRecipe}
   */
  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
    implements IRecipeSerializer<AlloyRecipe> {

    @Override
    public AlloyRecipe read(ResourceLocation recipeId, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      List<FluidIngredient> fluidIngredients = readFluidIngredients(JSONUtils.getJsonArray(json, "ingredients"));
      FluidStack output = RecipeHelper.deserializeFluidStack(JSONUtils.getJsonObject(json, "result"));
      int temperature = JSONUtils.getInt(json, "temperature");
      return new AlloyRecipe(recipeId, group, fluidIngredients, output, temperature);
    }

    private static List<FluidIngredient> readFluidIngredients(JsonArray array) {
      NonNullList<FluidIngredient> nonNullList = NonNullList.create();
      List<FluidIngredient> test = JsonHelper.parseList(array, "fluid",  FluidIngredient::deserialize);

      for (int i = 0; i < array.size(); ++i) {
        FluidIngredient fluidIngredient = FluidIngredient.deserialize(array.get(i), "ingredient " + i);
        if (fluidIngredient != FluidIngredient.EMPTY) {
          nonNullList.add(fluidIngredient);
        }
      }

      return test;
    }

    @Nullable
    @Override
    public AlloyRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        int i = buffer.readVarInt();
        NonNullList<FluidIngredient> fluidIngredients = NonNullList.withSize(i, FluidIngredient.EMPTY);
        for (int j = 0; j < i; ++j) {
          fluidIngredients.set(j, FluidIngredient.read(buffer));
        }
        FluidStack output = FluidStack.readFromPacket(buffer);
        int temperature = buffer.readInt();
        return new AlloyRecipe(recipeId, group, fluidIngredients, output, temperature);
      } catch (Exception e) {
        TConstruct.log.error("Error reading alloy recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, AlloyRecipe recipe) {
      try {
        buffer.writeString(recipe.group);
        buffer.writeVarInt(recipe.fluidIngredients.size());

        for (FluidIngredient fluidIngredient : recipe.fluidIngredients) {
          fluidIngredient.write(buffer);
        }

        recipe.output.writeToPacket(buffer);
        buffer.writeInt(recipe.temperature);
      } catch (Exception e) {
        TConstruct.log.error("Error writing alloy recipe to packet.", e);
        throw e;
      }
    }
  }
}
