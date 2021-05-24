package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.entity.CTEntityIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Severing")
public class SeveringManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, CTEntityIngredient ingredient, IItemStack output) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    SeveringRecipe recipe = new SeveringRecipe(id, CRTHelper.mapEntityIngredient(ingredient), ItemOutput.fromStack(output.getInternal()));
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public void removeRecipe(IItemStack output) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof SeveringRecipe) {
        return output.matches(new MCItemStackMutable(((SeveringRecipe) iRecipe).getOutput()));
      }
      return false;
    }));
  }

  @ZenCodeType.Method
  public void removeRecipe(CTEntityIngredient input) {
    EntityIngredient ingredient = CRTHelper.mapEntityIngredient(input);
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof SeveringRecipe) {
        return ((SeveringRecipe) iRecipe).getInputs().stream().anyMatch(ingredient);
      }
      return false;
    }));
  }

  @Override
  public IRecipeType<SeveringRecipe> getRecipeType() {
    return RecipeTypes.SEVERING;
  }
}
