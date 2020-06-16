package slimeknights.tconstruct.library.tools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.library.tools.nbt.ToolItemNBT;
import slimeknights.tconstruct.tables.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tables.recipe.part.PartRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class ToolBuildHandler {

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
      .map(MaterialItem::getMaterialFromStack)
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

  public static ItemStack tryToBuildToolPart(@Nonnull PartRecipe partRecipe, @Nullable MaterialRecipe materialRecipe, ItemStack patternStack, ItemStack materialStack, boolean removeItems) throws TinkerGuiException {
    Item part = partRecipe.getCraftingResult().getItem();

    if (part == null || !(part instanceof MaterialItem)) {
      throw new TinkerGuiException(new TranslationTextComponent("gui.error.invalid_pattern").getFormattedText());
    }

    if (materialRecipe != null) {
      IMaterial material = materialRecipe.getMaterial();

      if (material == IMaterial.UNKNOWN) {
        throw new TinkerGuiException(new TranslationTextComponent("gui.error.unknown_material").getFormattedText());
      }

      int itemCount = 1;

      int neededMaterial = partRecipe.getCost();
      float costPerPart = materialRecipe.getValue() / (float) materialRecipe.getNeeded();
      float currentValue = costPerPart;

      while (currentValue < (float) neededMaterial && itemCount != materialStack.getCount()) {
        currentValue += costPerPart;
        itemCount++;
      }

      ItemStack output = ItemStack.EMPTY;

      if (material.isCraftable() && currentValue >= partRecipe.getCost()) {
        output = ((MaterialItem) part).getItemstackWithMaterial(material);

        if (output.isEmpty()) {
          output = ItemStack.EMPTY;
        }

        if (removeItems) {
          patternStack.shrink(1);
          materialStack.shrink(itemCount);
        }
      }

      return output;
    } else {
      throw new TinkerGuiException(new TranslationTextComponent("gui.error.invalid_recipe").getFormattedText());
    }
  }

  private ToolBuildHandler() {
  }
}
