package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.plugin.crt.managers.base.ICastingManager;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.CastingBasin")
public class CastingBasinManager implements ICastingManager {
  
  @Override
  public ItemCastingRecipe makeItemCastingRecipe(ResourceLocation id, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumeCast, boolean switchSlots) {
    return new ItemCastingRecipe.Basin(id, "", cast, fluid, result, coolingTime, consumeCast, switchSlots);
  }
  
  @Override
  public CompositeCastingRecipe makeCompositeCastingRecipe(ResourceLocation id, MaterialId inputId, FluidIngredient fluid, MaterialId outputId, int coolingTemperature) {
    return new CompositeCastingRecipe.Basin(id, inputId, fluid, outputId, coolingTemperature);
  }
  
  @Override
  public ContainerFillingRecipe makeContainerFillingRecipe(ResourceLocation id, int fluidAmount, Item containerIn) {
    return new ContainerFillingRecipe.Basin(id, "", fluidAmount, containerIn);
  }
  
  @Override
  public MaterialCastingRecipe makeMaterialCastingRecipe(ResourceLocation id, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumeCast, boolean switchSlots) {
    return new MaterialCastingRecipe.Basin(id, "", cast, fluidAmount, result, consumeCast, switchSlots);
  }
  
  @Override
  public IRecipeType<ICastingRecipe> getRecipeType() {
    return RecipeTypes.CASTING_BASIN;
  }
  
  
}
