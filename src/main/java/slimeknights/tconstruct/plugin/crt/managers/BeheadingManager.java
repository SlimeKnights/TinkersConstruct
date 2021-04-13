package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;

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
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Beheading Recipes by an IItemStack output! Use `removeRecipe(MCEntityType input)` instead!");
  }
  
  @ZenCodeType.Method
  public void removeRecipe(MCEntityType input) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if(iRecipe instanceof BeheadingRecipe) {
        return ((BeheadingRecipe) iRecipe).getInputs().stream().anyMatch(entityType -> entityType == input.getInternal());
      }
      return false;
    }));
  }
  
  @Override
  public IRecipeType<BeheadingRecipe> getRecipeType() {
    return RecipeTypes.BEHEADING;
  }
}
