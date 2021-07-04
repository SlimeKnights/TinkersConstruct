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
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.TinkerStation")
public class TinkerStationManager implements IRecipeManager {

  private final String defaultError = "recipe.tconstruct.modifier.requirements_error";

  @ZenCodeType.Method
  public void addOverslimeModifierRecipe(String name, IIngredient ingredient, int restoreAmount) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    OverslimeModifierRecipe recipe = new OverslimeModifierRecipe(id, ingredient.asVanillaIngredient(), restoreAmount);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addModifierRecipeInternal(name, inputs, toolRequired, modifierResult, modifierResultLevel, maxLevel, 0, 0, modifierRequirements, minMatch, requirementsError);
  }

  @ZenCodeType.Method
  public void addUpgradeModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addModifierRecipeInternal(name, inputs, toolRequired, modifierResult, modifierResultLevel, maxLevel, upgradeSlots, 0, modifierRequirements, minMatch, requirementsError);
  }

  @ZenCodeType.Method
  public void addAbilityModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int abilitySlots, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addModifierRecipeInternal(name, inputs, toolRequired, modifierResult, modifierResultLevel, maxLevel, 0, abilitySlots, modifierRequirements, minMatch, requirementsError);
  }

  @ZenCodeType.Method
  public void addIncrementalModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, IItemStack leftover, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addIncrementalModifierRecipeInternal(name, input, amountPerInput, neededPerLevel, toolRequirement, modifierResult, modifierResultLevel, maxLevel, 0, 0, leftover, modifierRequirements, minMatch, requirementsError);
  }

  @ZenCodeType.Method
  public void addIncrementalUpgradeModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, IItemStack leftover, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addIncrementalModifierRecipeInternal(name, input, amountPerInput, neededPerLevel, toolRequirement, modifierResult, modifierResultLevel, maxLevel, upgradeSlots, 0, leftover, modifierRequirements, minMatch, requirementsError);
  }

  @ZenCodeType.Method
  public void addIncrementalAbilityModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int abilitySlots, IItemStack leftover, @ZenCodeType.Optional IData modifierRequirements, @ZenCodeType.OptionalInt(-1) int minMatch, @ZenCodeType.OptionalString(defaultError) String requirementsError) {
    addIncrementalModifierRecipeInternal(name, input, amountPerInput, neededPerLevel, toolRequirement, modifierResult, modifierResultLevel, maxLevel, 0, abilitySlots, leftover, modifierRequirements, minMatch, requirementsError);
  }

  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Tinker Station Recipes by an IItemStack output! Use `removeByName(String name)` instead!");
  }

  @ZenCodeType.Method
  public void removeRecipe(String modifierId) {
    Modifier modifier = CRTHelper.getModifier(modifierId);

    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof IDisplayModifierRecipe) {
        IDisplayModifierRecipe recipe = (IDisplayModifierRecipe) iRecipe;
        return recipe.getDisplayResult().getModifier().getId().equals(modifier.getId());
      }
      return false;
    }));

  }

  @Override
  public IRecipeType<ITinkerStationRecipe> getRecipeType() {
    return RecipeTypes.TINKER_STATION;
  }

  private ModifierMatch makeMatch(IData modifierRequirements, int minMatch) {
    ModifierMatch entry = ModifierMatch.ALWAYS;
    if (modifierRequirements != null && !modifierRequirements.asMap().isEmpty()) {

      ModifierMatch[] modifierMatches = modifierRequirements.asMap().entrySet().stream().map(stringIDataEntry -> ModifierMatch.entry(CRTHelper.getModifier(stringIDataEntry.getKey()), stringIDataEntry.getValue().asNumber().getInt())).toArray(ModifierMatch[]::new);
      if (minMatch < 0) {
        minMatch = modifierMatches.length;
      }
      entry = ModifierMatch.list(minMatch, modifierMatches);
    }
    return entry;
  }

  private void addIncrementalModifierRecipeInternal(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IItemStack leftover, IData modifierRequirements, int minMatch, String requirementsError) {
    name = fixRecipeName(name);
    Modifier resultModifier = CRTHelper.getModifier(modifierResult);
    checkIncrementalModifier(maxLevel, modifierResultLevel, amountPerInput, neededPerLevel, resultModifier);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    ModifierMatch entry = makeMatch(modifierRequirements, minMatch);
    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    IncrementalModifierRecipe recipe = new IncrementalModifierRecipe(id, input.asVanillaIngredient(), amountPerInput, neededPerLevel, toolRequirement.asVanillaIngredient(), entry, requirementsError, result, maxLevel, upgradeSlots, abilitySlots, leftover.getInternal());
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  private void addModifierRecipeInternal(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IData modifierRequirements, int minMatch, String requirementsError) {
    name = fixRecipeName(name);
    checkModifier(maxLevel, modifierResultLevel);
    Modifier resultModifier = CRTHelper.getModifier(modifierResult);
    ModifierMatch entry = makeMatch(modifierRequirements, minMatch);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<SizedIngredient> collect = Arrays.stream(inputs).map(iIngredientWithAmount -> SizedIngredient.of(iIngredientWithAmount.getIngredient().asVanillaIngredient(), iIngredientWithAmount.getAmount())).collect(Collectors.toList());
    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    ModifierRecipe recipe = new ModifierRecipe(id, collect, toolRequired.asVanillaIngredient(), entry, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  private void checkModifier(int maxLevel, int modifierResultLevel) {
    if (maxLevel <= 0) {
      throw new IllegalArgumentException("maxLevel has to be >= 0! Currently: " + maxLevel);
    }
    if (modifierResultLevel <= 0) {
      throw new IllegalArgumentException("modifierResultLevel has to be >= 0! Currently: " + modifierResultLevel);
    }
    if (modifierResultLevel > maxLevel) {
      throw new IllegalArgumentException("maxLevel cannot be bigger than modifierResultLevel!");
    }
  }

  private void checkIncrementalModifier(int maxLevel, int modifierResultLevel, int amountPerInput, int neededPerLevel, Modifier resultModifier) {
    checkModifier(maxLevel, modifierResultLevel);
    if (amountPerInput <= 0) {
      throw new IllegalArgumentException("amountPerInput has to be > 0! Currently: " + amountPerInput);
    }
    if (neededPerLevel <= 0) {
      throw new IllegalArgumentException("neededPerLevel has to be > 0! Currently: " + neededPerLevel);
    }
    if (!(resultModifier instanceof IncrementalModifier)) {
      throw new IllegalArgumentException("Cannot use non-incremental modifier in an Incremental Modifier recipe!");
    }
  }
}
