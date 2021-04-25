package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.entity.CTEntityIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Beheading")
public class BeheadingManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, CTEntityIngredient ingredient, IItemStack output) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    BeheadingRecipe recipe = new BeheadingRecipe(id, CRTHelper.mapEntityIngredient(ingredient), ItemOutput.fromStack(output.getInternal()));
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public void removeRecipe(IItemStack output) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof BeheadingRecipe) {
        return output.matches(new MCItemStackMutable(((BeheadingRecipe) iRecipe).getOutput()));
      }
      return false;
    }));
  }

  @ZenCodeType.Method
  public void removeRecipe(CTEntityIngredient input) {
    EntityIngredient ingredient = CRTHelper.mapEntityIngredient(input);
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof BeheadingRecipe) {
        return ((BeheadingRecipe) iRecipe).getInputs().stream().anyMatch(ingredient);
      }
      return false;
    }));
  }

  @Override
  public IRecipeType<BeheadingRecipe> getRecipeType() {
    return RecipeTypes.BEHEADING;
  }
}
