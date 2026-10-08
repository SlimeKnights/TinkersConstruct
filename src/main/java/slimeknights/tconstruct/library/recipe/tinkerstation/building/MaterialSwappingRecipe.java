package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterialUser;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.CraftCountModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolTinkering;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.BitSet;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

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
    return CraftCountModifierHook.maxStackSize(tool, count);
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
    setMaterials(tool, index, material);
    return tool.createStack(maxStackSize(tool));
  }

  /** Sets the materials on the given tool using the passed material */
  public static void setMaterials(ToolStack tool, int index, MaterialVariant material) {
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
  }

  /** Creates a stack with the max size from the given materials and focus, running the material stack size hook as needed. */
  protected ItemStack copyMaterials(MaterialIdNBT materials, ItemLike focus) {
    return CraftCountModifierHook.copyMaterials(materials, focus, maxStackSize);
  }

  /** Creates a stack with the item from the given tool and the passed materials. */
  protected ItemStack copyMaterials(IToolStackView tool, MaterialNBT materials) {
    ToolStack copy = ToolStack.createTool(tool.getItem(), tool.getDefinition(), materials);
    return copy.createStack(maxStackSize(tool));
  }

  /** Recipe mapping a single ingredient to a part */
  @RequiredArgsConstructor
  protected class DisplayRecipe implements IDisplayToolTinkering {
    public static final Component TITLE = TConstruct.makeTranslation("recipe", "part_swapping");
    public static final Component TOOLTIP = TConstruct.makeTranslation("recipe", "part_swapping.tooltip");
    public static final Component MATERIAL_TITLE = TConstruct.makeTranslation("recipe", "material_swapping");
    public static final Component MATERIAL_TOOLTIP = TConstruct.makeTranslation("recipe", "material_swapping.tooltip");

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
      return id;
    }

    @Override
    public int getMaxToolSize() {
      return maxStackSize;
    }

    @Override
    public int getMaxToolSize(ItemStack stack) {
      return maxStackSize(ToolStack.from(stack));
    }

    @Override
    public boolean isTool(ItemStack check) {
      return tools.test(check);
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

    /** Replaces the given material on the stack before creating a stack. */
    protected ItemStack replaceMaterial(MaterialIdNBT materials, MaterialVariantId replacement, ItemStack focus) {
      return copyMaterials(materials.replaceMaterial(index, replacement), focus.getItem());
    }

    /** Helper to create a stack with replaced material. Will modify the tool stack instance. */
    protected ItemStack replaceMaterial(ToolStack tool, MaterialVariant replacement, ItemStack focus) {
      tool.replaceMaterial(index, replacement);
      return tool.updateStack(focus.copyWithCount(maxStackSize(tool)), true);
    }

    /** Creates an input for the given materials list */
    protected List<ItemStack> createDisplayStack(MaterialIdNBT materials, ItemStack focus) {
      return List.of(replaceMaterial(materials, ToolBuildHandler.getRenderMaterial(0), focus));
    }

    /** Creates the list for the focus as an input */
    protected List<ItemStack> focusInput(ItemStack focus) {
      return List.of(focus.copyWithCount(getMaxToolSize(focus)));
    }
  }

  /** Display recipe linking the input to the output slot */
  protected class LinkedDisplayRecipe extends DisplayRecipe {
    private final int[] outputLinks;
    protected final List<MaterialVariant> materials;
    public LinkedDisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, List<MaterialVariant> materials) {
      super(index, input, toolWithoutModifier, toolWithModifier);
      this.outputLinks = new int[] {index};
      this.materials = materials;
    }

    @Override
    public int[] linkToOutput() {
      return outputLinks;
    }


    /* Dynamic focus */

    /** Gets a stream of animation indices without the given material */
    protected IntStream indicesWithout(MaterialVariantId material) {
      return IntStream.range(0, materials.size()).filter(i -> !materials.get(i).sameVariant(material));
    }

    @Override
    public List<ItemStack> getToolWithoutModifier(ItemStack focus, boolean focusOutput) {
      // skip inputs that are not the tool
      if (!focus.isEmpty() && (focusOutput || isTool(focus))) {
        MaterialIdNBT materials = MaterialIdNBT.from(focus);
        // for inputs, display the focus itself provided we have at least 1 material that is not the current material
        if (!focusOutput) {
          MaterialVariantId material = materials.getMaterial(index);
          if (indicesWithout(material).findAny().isPresent()) {
            return focusInput(focus);
          }
        }
        // otherwise, display a generic render tool with all other materials copied
        return createDisplayStack(materials, focus);
      }
      return toolWithoutModifier;
    }

    /** Gets the stacks for the output focus with the modifier, copying materials but discarding stack data. */
    protected List<ItemStack> getOutputFocusWithModifier(IMaterialUser materialUser, ItemStack focus) {
      // if the focus is the output, duplicate just the materials so it's the simplest version of the recipe
      MaterialIdNBT materials = MaterialIdNBT.from(focus);
      if (materialUser.canUseMaterial(materials.getMaterial(index).getId())) {
        return List.of(copyMaterials(materials, focus.getItem()));
      } else {
        // on the chance the result stack isn't usable, duplicate the rest of the materials as an animation over parts
        return this.materials.stream().map(newMaterial -> replaceMaterial(materials, newMaterial.getVariant(), focus)).toList();
      }
    }

    /** Common code for handling an input focus tool with the given modifier. */
    protected List<ItemStack> getInputFocusWithModifier(ItemStack focus) {
      ToolStack tool = ToolStack.copyFrom(focus);
      return getInputFocusWithModifier(tool, tool.getMaterial(index).getVariant(), focus);
    }

    /** Common code for handling an input focus tool with the given modifier, replacing the material but copying over stack data. */
    protected List<ItemStack> getInputFocusWithModifier(ToolStack tool, MaterialVariantId material, ItemStack focus) {
      // if focusing on an input tool, output is the input with the new material. need to filter our list of options to just new ones
      List<ItemStack> results = materials.stream()
        .filter(newMaterial -> !newMaterial.sameVariant(material))
        .map(newMaterial -> replaceMaterial(tool, newMaterial, focus))
        .toList();
      if (!results.isEmpty()) {
        return results;
      } else {
        // create a new tool with the same materials for each material option
        return this.materials.stream().map(newMaterial -> copyMaterials(tool, tool.getMaterials().replaceMaterial(index, newMaterial))).toList();
      }
    }
  }

  /** Display recipe with a tool part. Used to implement dynamic focus */
  protected class PartDisplayRecipe extends LinkedDisplayRecipe {
    protected final IMaterialItem part;
    public PartDisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, List<MaterialVariant> materials, IMaterialItem part) {
      super(index, input, toolWithoutModifier, toolWithModifier, materials);
      this.part = part;
    }


    /* Dynamic focus */

    @Override
    public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
      if (slot == index && !focus.isEmpty() && (focusOutput || isTool(focus))) {
        MaterialVariantId material = MaterialIdNBT.from(focus).getMaterial(index);
        // if focusing on the output, display just the part that makes that output, assuming its usable
        if (focusOutput) {
          if (part.canUseMaterial(material.getId())) {
            return List.of(part.withMaterialForDisplay(material));
          }
        } else  {
          // if focusing on the input, and focus is a tool, display all parts that are not the original material
          // if focus is a part, no work to do (focus link takes care of that)
          List<ItemStack> parts = indicesWithout(material).mapToObj(input::get).toList();
          // if we have no parts, best we can do is just display the full list
          if (!parts.isEmpty()) return parts;
        }
      }
      return getDisplayItems(slot);
    }

    @Override
    public List<ItemStack> getToolWithModifier(ItemStack focus, boolean focusOutput) {
      if (!focus.isEmpty()) {
        if (focusOutput) {
          return getOutputFocusWithModifier(part, focus);
        } else if (isTool(focus)) {
          return getInputFocusWithModifier(focus);
        }
      }
      return toolWithModifier;
    }
  }
}
