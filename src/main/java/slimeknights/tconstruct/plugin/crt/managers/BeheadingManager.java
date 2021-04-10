package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Beheading")
public class BeheadingManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, MCEntityType ingredient, IItemStack output) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    BeheadingRecipe recipe = new BeheadingRecipe(id, EntityIngredient.of(ingredient.getInternal()), ItemOutput.fromStack(output.getInternal()));
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public IRecipeType<BeheadingRecipe> getRecipeType() {
    return RecipeTypes.BEHEADING;
  }
}
