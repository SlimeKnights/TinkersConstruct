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
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;

/** Common logic for different implementations of material swapping. */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MaterialSwappingRecipe implements ITinkerStationRecipe {
  protected static final RecordField<Ingredient, MaterialSwappingRecipe> TOOLS_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tools", r -> r.tools);
  protected static final RecordField<Integer, MaterialSwappingRecipe> STACK_SIZE_FIELD = IntLoadable.FROM_ONE.defaultField("max_stack_size", ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE, true, r -> r.maxStackSize);
  protected static final RecordField<List<SizedIngredient>, MaterialSwappingRecipe> EXTRA_REQUIREMENTS_FIELD = SizedIngredient.LOADABLE.list(0).defaultField("extra_requirements", List.of(), r -> r.extraRequirements);
  protected static final RecipeResult<LazyToolStack> TOO_FEW_INPUTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_few_inputs"));
  protected static final RecipeResult<LazyToolStack> TOO_FEW_PARTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_few_parts"));
  protected static final RecipeResult<LazyToolStack> TOO_MANY_PARTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_many_parts"));
  protected static final RecipeResult<LazyToolStack> INVALID_MATERIAL = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.invalid_material"));
  protected static final Component TITLE = TConstruct.makeTranslation("recipe", "part_swapping");
  protected static final Component TOOLTIP = TConstruct.makeTranslation("recipe", "part_swapping.tooltip");

  @Getter
  protected final ResourceLocation id;
  /** Tools that may use this recipe */
  protected final Ingredient tools;
  /** Max stack size that can be swapped at once */
  protected final int maxStackSize;
  /** Additional ingredients that must be present to perform this part swap. */
  protected final List<SizedIngredient> extraRequirements;

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
    return swapMaterial(inv, material, index, partValue, false);
  }

  /** Logic to actually swap the material, with an override to force swap on damaged tools (intended for manual repair logic) */
  protected RecipeResult<LazyToolStack> swapMaterial(ITinkerableContainer inv, MaterialVariantId material, int index, int partValue, boolean mayRepair) {
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
      mayRepair |= repairDurability > 0;
    }
    if (!didChange && (original.getDamage() == 0 || !mayRepair)) {
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

  /** Shrinks the part, returning false if no match */
  protected abstract boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack);

  @Override
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // keep track of where we shrunk so it does not shrink again
    BitSet used = ModifierRecipe.makeBitset(inv);
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && shrinkPart(inv, i, stack)) {
        used.set(i);
        break;
      }
    }
    // shrink remaining requirements
    ModifierRecipe.updateInputs(inv, extraRequirements, used);
  }


  /* JEI helpers */
  /** Maximum slot index supported by JEI */
  protected static final int MAX_SLOTS = 5;
  /** Int stream filter to validate the slot index */
  protected static IntPredicate VALID_SLOT = i -> i < MAX_SLOTS;

  /** Creates a new item stack with the given material. Will modify {@code tool}. */
  public ItemStack withMaterial(ItemStack tool, int index, MaterialVariant material) {
    return withMaterial(ToolStack.from(tool), index, material);
  }

  /** Creates a new item stack with the given material. Will modify {@code tool}. */
  public ItemStack withMaterial(ToolStack tool, int index, MaterialVariant material) {
    return withMaterial(tool, index, material, maxStackSize);
  }

  /** Creates a new item stack with the given material. Will modify {@code tool}. */
  public static ItemStack withMaterial(ItemStack tool, int index, MaterialVariant material, int maxStackSize) {
    return withMaterial(ToolStack.from(tool), index, material, maxStackSize);
  }

  /** Creates a new item stack with the given material. Will modify {@code tool}. */
  public static ItemStack withMaterial(ToolStack tool, int index, MaterialVariant material, int maxStackSize) {
    if (tool.getMaterials().isEmpty()) {
      MaterialNBT.Builder builder = MaterialNBT.builder();
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(tool.getDefinition());
      for (int i = 0; i < requirements.size(); i++) {
        if (i == index) {
          builder.add(material);
        } else {
          builder.add(MaterialRegistry.firstWithStatType(requirements.get(i)));
        }
      }
      tool.setMaterials(builder.build());
    } else {
      // if it has materials already just swap the one to update
      tool.replaceMaterial(index, material);
    }
    return tool.createStack(Math.min(maxStackSize, tool.getItem().getMaxStackSize()));
  }

  /** Recipe mapping a single ingredient to a part */
  @RequiredArgsConstructor
  protected class DisplayRecipe implements IDisplayToolModification {
    protected final int index;
    protected final List<ItemStack> input;
    @Getter
    protected final List<ItemStack> toolWithoutModifier, toolWithModifier;

    @Override
    public Component getTitle() {
      return TITLE;
    }

    @Override
    public Component getTooltip() {
      return TOOLTIP;
    }

    @Override
    public ResourceLocation getRecipeId() {
      return getId();
    }

    @Override
    public int getInputCount() {
      // need 1 input for the part, and 1 for each extra requirement
      // if it's just the part by itself though, ensure we have an index for each location before it
      return Math.min(index, extraRequirements.size()) + 1;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      if (slot == index) {
        return input;
      }
      // place extra requirements around the part by offsetting if the slot is after the index
      if (slot > index) {
        slot--;
      }
      if (slot < extraRequirements.size()) {
        return extraRequirements.get(slot).getMatchingStacks();
      }
      return List.of();
    }
  }

  /** Display recipe linking the input to the output slot */
  protected class LinkedDisplayRecipe extends DisplayRecipe {
    private final int[] outputLinks;
    public LinkedDisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier) {
      super(index, input, toolWithoutModifier, toolWithModifier);
      this.outputLinks = new int[] {index};
    }

    @Override
    public int[] linkToOutput() {
      return outputLinks;
    }
  }
}
