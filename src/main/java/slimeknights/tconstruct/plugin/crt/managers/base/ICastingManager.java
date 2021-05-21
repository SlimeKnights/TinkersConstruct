package slimeknights.tconstruct.plugin.crt.managers.base;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.item.*;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidUtil;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.*;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.*;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.ICastingManager")
public interface ICastingManager extends IRecipeManager {
  
  ItemCastingRecipe makeItemCastingRecipe(ResourceLocation id, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumeCast, boolean switchSlots);
  
  CompositeCastingRecipe makeCompositeCastingRecipe(ResourceLocation id, MaterialId inputId, FluidIngredient fluid, MaterialId outputId, int coolingTemperature);
  
  ContainerFillingRecipe makeContainerFillingRecipe(ResourceLocation idIn, int fluidAmount, Item containerIn);
  
  MaterialCastingRecipe makeMaterialCastingRecipe(ResourceLocation id, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumeCast, boolean switchSlots);
  
  
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
  default void addCompositeCastingRecipe(String name, String materialId, CTFluidIngredient fluidIngredient, String outputMaterialId, int coolingTemperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialId inputMaterialId = CRTHelper.getMaterialId(materialId);
    FluidIngredient fluid = CRTHelper.mapFluidIngredient(fluidIngredient);
    MaterialId outputMatId = CRTHelper.getMaterialId(outputMaterialId);
    CompositeCastingRecipe recipe = makeCompositeCastingRecipe(id, inputMaterialId, fluid, outputMatId, coolingTemperature);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Composite Casting"));
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
  default void addMaterialCastingRecipe(String name, IIngredient cast, int fluidAmount, Item result, boolean consumeCast, boolean switchSlots) {
    if(!(result instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(result).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient castIngredient = cast.asVanillaIngredient();
    MaterialCastingRecipe recipe = makeMaterialCastingRecipe(id, castIngredient, fluidAmount, (IMaterialItem) result, consumeCast, switchSlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Material Casting"));
  }
  
  
}
