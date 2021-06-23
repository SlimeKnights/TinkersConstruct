package slimeknights.tconstruct.plugin.crt.managers.base;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidUtil;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.ICastingManager")
public interface ICastingManager extends IRecipeManager {
  
  ItemCastingRecipe makeItemCastingRecipe(ResourceLocation id, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumeCast, boolean switchSlots);

  ContainerFillingRecipe makeContainerFillingRecipe(ResourceLocation idIn, int fluidAmount, Item containerIn);
  
  MaterialCastingRecipe makeMaterialCastingRecipe(ResourceLocation id, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumeCast, boolean switchSlots);

  CompositeCastingRecipe makeCompositeCastingRecipe(ResourceLocation id, IMaterialItem result, int itemCost);
  
  @ZenCodeType.Method
  default void addItemCastingRecipe(String name, IIngredient cast, CTFluidIngredient fluidIngredient, IItemStack result, int coolingTime, boolean consumeCast, boolean switchSlots) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient castIngredient = cast.asVanillaIngredient();
    FluidIngredient fluid = CRTHelper.mapFluidIngredient(fluidIngredient);
    ItemOutput itemOutput = ItemOutput.fromStack(result.getInternal());
    ItemCastingRecipe recipe = makeItemCastingRecipe(id, castIngredient, fluid, itemOutput, coolingTime, consumeCast, switchSlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Item Casting"));
  }
  
  @ZenCodeType.Method
  default void addContainerFillingRecipe(String name, int fluidAmount, Item containerIn) {
    if(!FluidUtil.getFluidHandler(containerIn.getDefaultInstance()).isPresent()) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(containerIn).getCommandString() + " is not a fluid container!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    ContainerFillingRecipe recipe = makeContainerFillingRecipe(id, fluidAmount, containerIn);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Container Filling"));
  }

  @ZenCodeType.Method
  default void addMaterialCastingRecipe(String name, IIngredient cast, int itemCost, Item result, boolean consumeCast, boolean switchSlots) {
    if(!(result instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(result).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient castIngredient = cast.asVanillaIngredient();
    MaterialCastingRecipe recipe = makeMaterialCastingRecipe(id, castIngredient, itemCost, (IMaterialItem) result, consumeCast, switchSlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Material Casting"));
  }

  @ZenCodeType.Method
  default void addCompositeCastingRecipe(String name, Item result, int itemCost) {
    if(!(result instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(result).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    CompositeCastingRecipe recipe = makeCompositeCastingRecipe(id, (IMaterialItem) result, itemCost);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Composite Part Casting"));
  }
}
