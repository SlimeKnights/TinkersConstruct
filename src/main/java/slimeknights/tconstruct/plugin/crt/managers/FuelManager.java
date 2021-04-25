package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Fuel")
public class FuelManager implements IRecipeManager {
  
  @ZenCodeType.Method
  public void addFuel(String name, CTFluidIngredient input, int duration, int temperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    FluidIngredient fluidIngredient = CRTHelper.mapFluidIngredient(input);
    MeltingFuel recipe = new MeltingFuel(id, "", fluidIngredient, duration, temperature);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }
  
  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Fuel Recipes by an IItemStack output as it doesn't output anything! Use `removeRecipe(Fluid input)` instead!");
  }
  
  @ZenCodeType.Method
  public void removeRecipe(Fluid input) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if(iRecipe instanceof MeltingFuel) {
        MeltingFuel recipe = (MeltingFuel) iRecipe;
        return recipe.getInputs().stream().anyMatch(fluidStack -> fluidStack.getFluid() == input);
      }
      return false;
    }));
  }
  
  @Override
  public IRecipeType<MeltingFuel> getRecipeType() {
    return RecipeTypes.FUEL;
  }
  
}
