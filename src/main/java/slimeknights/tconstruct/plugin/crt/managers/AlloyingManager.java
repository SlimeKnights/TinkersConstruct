package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.fluid.MCFluidStackMutable;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Allying")
public class AlloyingManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, IFluidStack[] ingredients, IFluidStack output, int temperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<FluidIngredient> fluidIngredients = Arrays.stream(ingredients).map(IFluidStack::getInternal).map(FluidIngredient::of).collect(Collectors.toList());
    FluidStack fluidOutput = output.getInternal();
    AlloyRecipe recipe = new AlloyRecipe(id, fluidIngredients, fluidOutput, temperature);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }


  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Alloy Recipes by an IItemStack output as it outputs Fluids! Use `removeRecipe(Fluid output)` instead!");
  }

  @ZenCodeType.Method
  public void removeRecipe(Fluid output) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof AlloyRecipe) {
        AlloyRecipe recipe = (AlloyRecipe) iRecipe;
        return recipe.getOutput().getFluid() == output;
      }
      return false;
    }));
  }

  @Override
  public IRecipeType<AlloyRecipe> getRecipeType() {
    return RecipeTypes.ALLOYING;
  }

}
