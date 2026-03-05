package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

/** Common logic for different implementations of material swapping. */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MaterialSwappingRecipe implements ITinkerStationRecipe {
  protected static final RecordField<Ingredient, MaterialSwappingRecipe> TOOLS_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tools", r -> r.tools);
  protected static final RecordField<Integer, MaterialSwappingRecipe> STACK_SIZE_FIELD = IntLoadable.FROM_ONE.defaultField("max_stack_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, true, r -> r.maxStackSize);
  protected static final RecipeResult<LazyToolStack> TOO_FEW_INPUTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_few_inputs"));
  protected static final RecipeResult<LazyToolStack> TOO_FEW_PARTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_few_parts"));
  protected static final RecipeResult<LazyToolStack> TOO_MANY_PARTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_many_parts"));
  protected static final RecipeResult<LazyToolStack> INVALID_MATERIAL = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.invalid_material"));

  @Getter
  protected final ResourceLocation id;
  /** Tools that may use this recipe */
  protected final Ingredient tools;
  /** Max stack size that can be swapped at once */
  protected final int maxStackSize;

  @Override
  public int shrinkToolSlotBy() {
    return maxStackSize;
  }

  /** Gets the max stack size for the given tool, calling the modifier hook */
  protected static int maxStackSize(IToolStackView tool, float count) {
    for (ModifierEntry entry : tool.getModifiers()) {
      count = entry.getHook(ModifierHooks.CRAFT_COUNT).modifyCraftCount(tool, entry, count);
      if (count <= 0) {
        return 0;
      }
    }
    return (int) count;
  }

  /** Gets the max stack size for the given tool, calling the modifier hook */
  protected int maxStackSize(IToolStackView tool) {
    return maxStackSize(tool, maxStackSize);
  }

  @Override
  public int shrinkToolSlotBy(LazyToolStack result, ITinkerStationContainer inv) {
    // if the output is shrinking, we want to ensure we take the minumum amount needed for that output
    // for example, if its reducing by 50%, just consuming the full amount might consume 3 arrows to produce 1 (instead of 2 to produce 1)
    int outputMax = maxStackSize(result.getTool());
    return maxStackSize(inv.getTinkerable(), result.getSize() * maxStackSize / (float) outputMax);
  }

  /** Logic to actually swap the material */
  protected RecipeResult<LazyToolStack> swapMaterial(ITinkerableContainer inv, MaterialVariantId material, int index, int partValue) {
    // ensure we have enough items to get a result
    ToolStack original = inv.getTinkerable();
    int shrink = maxStackSize(original);
    if (shrink <= 0) {
      return TOO_FEW_INPUTS;
    }

    // this should never happen, means bad recipe, but give a nice error at least
    List<MaterialStatsId> statTypes = ToolMaterialHook.stats(original.getDefinition());
    if (index >= statTypes.size()) {
      return TOO_FEW_PARTS;
    }

    // ensure there is a change in the part or we are repairing the tool, note we compare variants so you could swap oak head for birch head
    MaterialVariant toolMaterial = original.getMaterial(index);
    boolean didChange = !toolMaterial.sameVariant(material);
    float repairDurability = 0;
    if (partValue > 0) {
      repairDurability = partValue * MaterialRepairModule.getDurability(null, material.getId(), statTypes.get(index));
    }
    if (!didChange && (original.getDamage() == 0 || repairDurability == 0)) {
      return RecipeResult.pass();
    }

    // actual update
    ToolStack copy = original.copy();

    // determine which modifiers are going to be removed
    if (didChange) {
      // do the actual part replacement
      copy.replaceMaterial(index, material);
    }

    // if swapping in a new head, repair the tool (assuming the give stats type can repair)
    // ideally we would validate before repairing, but don't want to create the stack before repairing
    if (repairDurability > 0) {
      // takes 3 ingots for a full repair, however count the head cost in the repair amount
      repairDurability /= MaterialRecipe.INGOTS_PER_REPAIR;
      if (repairDurability > 0) {
        for (ModifierEntry entry : copy.getModifierList()) {
          repairDurability = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(copy, entry, repairDurability);
          if (repairDurability <= 0) {
            break;
          }
        }
      }
      if (repairDurability > 0) {
        ToolDamageUtil.repair(copy, (int)repairDurability);
      }
    }

    // ensure no modifier problems after removing
    // modifier validation, handles modifier requirements
    Component error = copy.tryValidate();
    if (error != null) {
      return RecipeResult.failure(error);
    }
    if (didChange) {
      error = ModifierRemovalHook.onRemoved(original, copy);
      if (error != null) {
        return RecipeResult.failure(error);
      }
    }

    // need to scale our result based on the stack size differential, e.g. if the input max is 8 and the output 4, result should be halved
    ItemStack originalStack = inv.getTinkerableStack();
    int outputMax = maxStackSize(copy);
    int resultSize = Math.min(originalStack.getCount() * outputMax / shrink, outputMax);
    if (resultSize <= 0) {
      return TOO_FEW_INPUTS;
    }

    // everything worked, so good to go
    return LazyToolStack.successCopy(copy, resultSize, originalStack);
  }
}
