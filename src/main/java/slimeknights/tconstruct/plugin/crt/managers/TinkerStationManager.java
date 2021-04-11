package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IIngredientWithAmount;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipeByOutput;
import com.blamejared.crafttweaker.impl.data.ListData;
import com.blamejared.crafttweaker.impl.data.MapData;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.TinkerStation")
public class TinkerStationManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addOverslimeModifierRecipe(String name, IIngredient ingredient, int restoreAmount) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    OverslimeModifierRecipe recipe = new OverslimeModifierRecipe(id, ingredient.asVanillaIngredient(), restoreAmount);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots) {
    name = fixRecipeName(name);
    Modifier resultModifier = getModifier(modifierResult);

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<SizedIngredient> collect = Arrays.stream(inputs).map(iIngredientWithAmount -> SizedIngredient.of(iIngredientWithAmount.getIngredient().asVanillaIngredient(), iIngredientWithAmount.getAmount())).collect(Collectors.toList());
    ModifierMatch entry = ModifierMatch.ALWAYS;
    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    ModifierRecipe recipe = new ModifierRecipe(id, collect, toolRequired.asVanillaIngredient(), entry, "", result, maxLevel, upgradeSlots, abilitySlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IData modifierRequirements, int minMatch, String requirementsError) {

    name = fixRecipeName(name);
    Modifier resultModifier = getModifier(modifierResult);
    ModifierMatch[] modifierMatches = modifierRequirements.asMap().entrySet().stream().map(stringIDataEntry -> ModifierMatch.entry(getModifier(stringIDataEntry.getKey()), stringIDataEntry.getValue().asNumber().getInt())).toArray(ModifierMatch[]::new);
    ModifierMatch entry = ModifierMatch.list(minMatch, modifierMatches);

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<SizedIngredient> collect = Arrays.stream(inputs).map(iIngredientWithAmount -> SizedIngredient.of(iIngredientWithAmount.getIngredient().asVanillaIngredient(), iIngredientWithAmount.getAmount())).collect(Collectors.toList());

    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    ModifierRecipe recipe = new ModifierRecipe(id, collect, toolRequired.asVanillaIngredient(), entry, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addIncrementalModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IItemStack leftover) {
    name = fixRecipeName(name);
    Modifier resultModifier = getModifier(modifierResult);

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    ModifierMatch entry = ModifierMatch.ALWAYS;
    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    IncrementalModifierRecipe recipe = new IncrementalModifierRecipe(id, input.asVanillaIngredient(), amountPerInput, neededPerLevel, toolRequirement.asVanillaIngredient(), entry, "", result, maxLevel, upgradeSlots, abilitySlots, leftover.getInternal());
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addIncrementalModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IItemStack leftover, IData modifierRequirements, int minMatch, String requirementsError) {
    name = fixRecipeName(name);
    Modifier resultModifier = getModifier(modifierResult);

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    ModifierMatch[] modifierMatches = modifierRequirements.asMap().entrySet().stream().map(stringIDataEntry -> ModifierMatch.entry(getModifier(stringIDataEntry.getKey()), stringIDataEntry.getValue().asNumber().getInt())).toArray(ModifierMatch[]::new);
    ModifierMatch entry = ModifierMatch.list(minMatch, modifierMatches);

    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    IncrementalModifierRecipe recipe = new IncrementalModifierRecipe(id, input.asVanillaIngredient(), amountPerInput, neededPerLevel, toolRequirement.asVanillaIngredient(), entry, requirementsError, result, maxLevel, upgradeSlots, abilitySlots, leftover.getInternal());
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }


  private Modifier getModifier(String name) {
    ModifierId resultId = ModifierId.tryCreate(name);
    if (resultId == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + name);
    }
    Modifier resultModifier = TinkerRegistries.MODIFIERS.getValue(resultId);
    if (resultModifier == null) {
      throw new IllegalArgumentException("Modifier does not exist! Provided: " + resultId);
    }
    return resultModifier;
  }

  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Tinker Station Recipes by an IItemStack output! Use `removeByName(String name)` instead!");
  }

  @Override
  public IRecipeType<ITinkerStationRecipe> getRecipeType() {
    return RecipeTypes.TINKER_STATION;
  }

}
