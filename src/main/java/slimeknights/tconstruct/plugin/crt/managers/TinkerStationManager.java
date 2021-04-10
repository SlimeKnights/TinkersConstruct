package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IIngredientWithAmount;
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
    ModifierId resultId = ModifierId.tryCreate(modifierResult);
    if (resultId == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + modifierResult);
    }
    Modifier resultModifier = TinkerRegistries.MODIFIERS.getValue(resultId);
    if (resultModifier == null) {
      throw new IllegalArgumentException("Modifier does not exist! Provided: " + resultId);
    }

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
    ModifierId resultId = ModifierId.tryCreate(modifierResult);
    if (resultId == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + modifierResult);
    }
    Modifier resultModifier = TinkerRegistries.MODIFIERS.getValue(resultId);
    if (resultModifier == null) {
      throw new IllegalArgumentException("Modifier does not exist! Provided: " + resultId);
    }
    List<ModifierMatch> modifierMatches = new ArrayList<>();
    for (String requirementKey : modifierRequirements.asMap().keySet()) {
      ModifierId requirementId = ModifierId.tryCreate(requirementKey);
      if (requirementId == null) {
        throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + requirementKey);
      }
      Modifier requirementModifier = TinkerRegistries.MODIFIERS.getValue(requirementId);
      if (requirementModifier == null) {
        throw new IllegalArgumentException("Modifier does not exist! Provided: " + requirementId);
      }
      modifierMatches.add(ModifierMatch.entry(requirementModifier, modifierRequirements.asMap().get(requirementKey).asNumber().getInt()));
    }
    ModifierMatch entry = ModifierMatch.list(minMatch, modifierMatches.toArray(new ModifierMatch[0]));

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<SizedIngredient> collect = Arrays.stream(inputs).map(iIngredientWithAmount -> SizedIngredient.of(iIngredientWithAmount.getIngredient().asVanillaIngredient(), iIngredientWithAmount.getAmount())).collect(Collectors.toList());

    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    ModifierRecipe recipe = new ModifierRecipe(id, collect, toolRequired.asVanillaIngredient(), entry, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addIncrementalModifierRecipe(String name, IIngredientWithAmount[] inputs, IIngredient toolRequired, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots) {

    name = fixRecipeName(name);
    ModifierId resultId = ModifierId.tryCreate(modifierResult);
    if (resultId == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + modifierResult);
    }
    Modifier resultModifier = TinkerRegistries.MODIFIERS.getValue(resultId);
    if (resultModifier == null) {
      throw new IllegalArgumentException("Modifier does not exist! Provided: " + resultId);
    }

    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    List<SizedIngredient> collect = Arrays.stream(inputs).map(iIngredientWithAmount -> SizedIngredient.of(iIngredientWithAmount.getIngredient().asVanillaIngredient(), iIngredientWithAmount.getAmount())).collect(Collectors.toList());
    ModifierMatch entry = ModifierMatch.ALWAYS;
    ModifierEntry result = new ModifierEntry(resultModifier, modifierResultLevel);
    ModifierRecipe recipe = new ModifierRecipe(id, collect, toolRequired.asVanillaIngredient(), entry, "", result, maxLevel, upgradeSlots, abilitySlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public IRecipeType<ITinkerStationRecipe> getRecipeType() {
    return RecipeTypes.TINKER_STATION;
  }

}
