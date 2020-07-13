package slimeknights.tconstruct.library.tools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ICraftMod;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.library.tools.nbt.ToolItemNBT;
import slimeknights.tconstruct.tools.IToolPart;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class ToolBuildHandler {

  @Nonnull
  public static ItemStack tryToRepairTool(List<IRecipe<ISingleItemInventory>> recipes, NonNullList<ItemStack> stacks, ItemStack toolStack, boolean removeItems) {
    if (toolStack == ItemStack.EMPTY || !(toolStack.getItem() instanceof IRepairable)) {
      return ItemStack.EMPTY;
    }

    // obtain a working copy of the items if the originals shouldn't be modified
    if (!removeItems) {
      stacks = Util.copyItemStackList(stacks);
    }

    List<MaterialRecipe> materialRecipes = Lists.newArrayList();

    for (IRecipe<ISingleItemInventory> recipe : recipes) {
      if (recipe instanceof MaterialRecipe) {
        materialRecipes.add((MaterialRecipe) recipe);
      }
    }

    return ((IRepairable) toolStack.getItem()).repair(materialRecipes, toolStack, stacks);
  }

  @Nonnull
  public static ItemStack tryToReplaceToolParts(ItemStack toolStack, final NonNullList<ItemStack> toolPartsIn, final boolean removeItems) throws TinkerGuiException {
    if (toolStack == ItemStack.EMPTY || !(toolStack.getItem() instanceof ToolCore)) {
      return ItemStack.EMPTY;
    }

    NonNullList<ItemStack> inputItems = ItemStackList.of(Util.copyItemStackList(toolPartsIn));

    // todo readd Events?
    /*if(!TinkerEvent.OnToolPartReplacement.fireEvent(inputItems, toolStack)) {
      // event cancelled
      return ItemStack.EMPTY;
    }*/

    // technically we don't need a deep copy here, but meh. less code.
    final NonNullList<ItemStack> toolParts = Util.copyItemStackList(inputItems);

    TreeMap<Integer, Integer> assigned = new TreeMap<Integer, Integer>();
    ToolCore tool = (ToolCore) toolStack.getItem();
    List<IMaterial> materials = ToolData.from(toolStack).getMaterials();
    List<IMaterial> copyOfMaterials = new ArrayList<>(materials);

    // assigning each tool part to a slot in the tool
    for (int i = 0; i < toolParts.size(); i++) {
      ItemStack part = toolParts.get(i);

      if (part.isEmpty()) {
        continue;
      }

      if (!(part.getItem() instanceof IToolPart)) {
        // invalid item for tool part replacement
        return ItemStack.EMPTY;
      }

      int candidate = -1;

      List<PartMaterialRequirement> requirements = tool.getToolDefinition().getRequiredComponents();
      for (int j = 0; j < requirements.size(); j++) {
        PartMaterialRequirement requirement = requirements.get(j);

        MaterialId partMaterial = ((IToolPart) part.getItem()).getMaterial(part).getIdentifier();
        IMaterial material = copyOfMaterials.get(j);
        if (requirement.isValid(part) && !partMaterial.equals(material.getIdentifier())) {
          candidate = j;
          // if a tool has multiple of the same parts we may want to replace another one as the currently selected
          // for that purpose we only allow to overwrite the current selection if the input slot is a later one than the current one
          if (i <= j) {
            break;
          }
        }
      }

      // no assignment found for a part. Invalid input.
      if (candidate < 0) {
        return ItemStack.EMPTY;
      }
      assigned.put(i, candidate);
    }

    // did we assign nothing?
    if (assigned.isEmpty()) {
      return ItemStack.EMPTY;
    }

    assigned.forEach((i, j) -> {
      MaterialId materialId = ((IToolPart) toolParts.get(i).getItem()).getMaterial(toolParts.get(i)).getIdentifier();
      copyOfMaterials.set(j, MaterialRegistry.getMaterial(materialId));
      if (removeItems) {
        if (i < toolPartsIn.size() && !toolPartsIn.get(i).isEmpty()) {
          toolPartsIn.get(i).shrink(1);
        }
      }
    });

    ItemStack copyToCheck = ToolBuildHandler.buildItemFromMaterials(tool, copyOfMaterials);
    /* TODO TRAITS/MODIFIERS
    // this includes traits
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(toolStack);
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String id = modifiers.getStringTagAt(i);
      IModifier mod = TinkerRegistry.getModifier(id);

      boolean canApply = false;
      try {
        // will throw an exception if it can't apply
        canApply = mod != null && mod.canApply(copyToCheck, copyToCheck);
      } catch(TinkerGuiException e) {
        // try again with more modifiers, in case something modified them (tinkers tool leveling)
        // ensure that free modifiers are present (
        if(ToolHelper.getFreeModifiers(copyToCheck) < ToolCore.DEFAULT_MODIFIERS) {
          ItemStack copyWithModifiers = copyToCheck.copy();
          NBTTagCompound nbt = TagUtil.getToolTag(copyWithModifiers);
          nbt.setInteger(Tags.FREE_MODIFIERS, ToolCore.DEFAULT_MODIFIERS);
          TagUtil.setToolTag(copyWithModifiers, nbt);
          canApply = mod.canApply(copyWithModifiers, copyWithModifiers);
        }
      }
      if(!canApply) {
        throw new TinkerGuiException();
      }
    }*/

    // TODO FINISH UP REPLACING PARTS

    ItemStack output = toolStack.copy();
    /*TagUtil.setBaseMaterialsTagList(output, materialList);
    NBTTagCompound tag = TagUtil.getTagSafe(output);
    rebuildTool(tag, (TinkersItem) output.getItem());
    output.setTagCompound(tag);*/

    // check if the output has enough durability. we only allow it if the result would not be broken
    if (output.getDamage() > output.getMaxDamage()) {
      throw new TinkerGuiException(new TranslationTextComponent("gui.tconstruct.error.not_enough_durability", output.getDamage() - output.getMaxDamage()).getFormattedText());
    }

    return output;
  }

  /**
   * Takes an array of Itemstacks and tries to build a tool with it. Amount of itemstacks has to match exactly.
   *
   * @param stacks Input.
   * @return The built tool or null if none could be built.
   */
  public static ItemStack tryToBuildTool(NonNullList<ItemStack> stacks, String name, Collection<ToolCore> possibleTools) {
    for (Item item : possibleTools) {
      if (!(item instanceof ToolCore)) {
        continue;
      }
      ItemStack output = ToolBuildHandler.buildItemFromStacks(stacks, (ToolCore) item);
      if (!output.isEmpty()) {
        // name the item
        if (name != null && !name.isEmpty()) {
          output.setDisplayName(new StringTextComponent(name));
        }

        return output;
      }
    }

    return ItemStack.EMPTY;
  }

  /**
   * Builds an ItemStack of this tool with the given materials from the ItemStacks, if possible.
   *
   * @param stacks Items to build with. Have to be in the correct order and contain material items.
   * @return The built item or null if invalid input.
   */
  public static ItemStack buildItemFromStacks(NonNullList<ItemStack> stacks, ToolCore tool) {
    List<PartMaterialRequirement> requiredComponents = tool.getToolDefinition().getRequiredComponents();

    if (stacks.size() != requiredComponents.size() || !canBeBuiltFromParts(stacks, requiredComponents)) {
      return ItemStack.EMPTY;
    }

    List<IMaterial> materials = stacks.stream()
      .filter(stack -> !stack.isEmpty())
      .map(IMaterialItem::getMaterialFromStack)
      .collect(Collectors.toList());

    return buildItemFromMaterials(tool, materials);
  }

  @VisibleForTesting
  protected static ItemStack buildItemFromMaterials(ToolCore tool, List<IMaterial> materials) {
    StatsNBT stats = tool.buildToolStats(materials);

    ToolData toolData = new ToolData(
      new ToolItemNBT(tool),
      new MaterialNBT(materials),
      stats
    );

    ItemStack output = new ItemStack(tool);
    output.setTag(toolData.serializeToNBT());
    return output;
  }

  private static boolean canBeBuiltFromParts(NonNullList<ItemStack> stacks, List<PartMaterialRequirement> requiredComponents) {
    return Streams.zip(requiredComponents.stream(), stacks.stream(), PartMaterialRequirement::isValid).allMatch(Boolean::booleanValue);
  }

  /**
   * Takes a tool and an array of itemstacks and tries to modify the tool with those.
   * If removeItems is true, the items used in the process will be removed from the array.
   *
   * @param stacks       Items to modify the tool with
   * @param tool   The tool
   * @param removeItems If true the applied items will be removed from the array
   * @return The modified tool or null if something went wrong or no modifier applied.
   * @throws TinkerGuiException Thrown when not matching modifiers could be applied. Contains extra-information why the process failed.
   */
  @Nonnull
  public static ItemStack tryModifyTool(NonNullList<ItemStack> stacks, ItemStack tool, boolean removeItems)
    throws TinkerGuiException {
    ItemStack copy = tool.copy();
    ArrayList<ICraftMod> modifiers = TinkerModifiers.getAllModifiers();
    ItemStack[] inputs = new ItemStack[modifiers.size()];//(ItemStack[]) stacks.toArray();
    for (int i = 0; i < inputs.length; i++) {
      inputs[i] = stacks.get(i);
      System.out.println("Input "+(i)+": "+inputs[i]+", source: "+stacks.get(i));
    }
    int[] slotsOpen = new int[]{3, 1, 0, 1};

    for (int i = 0; i < modifiers.size(); i++) {
      if (modifiers.get(i).canApply(copy, inputs, slotsOpen)) {
        modifiers.get(i).apply(tool, copy);
      }
    }
    return copy;
  }

  private ToolBuildHandler() {
  }
}
