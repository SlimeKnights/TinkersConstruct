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
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Partbuilder")
public class PartBuilderManager implements IRecipeManager {
  @ZenCodeType.Method
  public void addItemRecipe(String name, String materialId, String pattern, int cost, IItemStack output) {
    if(cost < 1) {
      throw new IllegalArgumentException("PartBuilder `cost` needs to be more than or equal to `1`. Provided: " + cost);
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", fixRecipeName(name));
    MaterialId material = CRTHelper.getMaterialId(materialId);
    Pattern partPattern = new Pattern(pattern);
    ItemOutput itemOutput = ItemOutput.fromStack(output.getInternal());
    ItemPartRecipe recipe = new ItemPartRecipe(id, material, partPattern, cost, itemOutput);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }
  
  @ZenCodeType.Method
  public void addMaterialRecipe(String name, String pattern, int cost, Item output, int outputCount) {
    if(!(output instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(output).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    if(cost < 1) {
      throw new IllegalArgumentException("PartBuilder `cost` needs to be more than or equal to `1`. Provided: " + cost);
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Pattern partPattern = new Pattern(pattern);
    PartRecipe recipe = new PartRecipe(id, "", partPattern, cost, ((IMaterialItem) output), outputCount);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }
  
  @Override
  public IRecipeType<IPartBuilderRecipe> getRecipeType() {
    return RecipeTypes.PART_BUILDER;
  }
}
