package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Partbuilder")
public class PartBuilderManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, String pattern, int cost, Item output, int outputCount) {
    if (!(output instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(output).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    ResourceLocation partPattern = new ResourceLocation(pattern);
    PartRecipe recipe = new PartRecipe(id, "", partPattern, cost, ((IMaterialItem) output), outputCount);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Part Builder Recipes by an IItemStack output! Use `removeByName(String name)` instead!");
  }

  @Override
  public IRecipeType<PartRecipe> getRecipeType() {
    return RecipeTypes.PART_BUILDER;
  }

}
