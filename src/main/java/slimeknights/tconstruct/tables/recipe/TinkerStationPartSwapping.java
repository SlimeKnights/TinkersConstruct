package slimeknights.tconstruct.tables.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialItemCostLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;
import java.util.Optional;

/**
 * Recipe that replaces a tool part with another
 */
@AllArgsConstructor
public class TinkerStationPartSwapping implements ITinkerStationRecipe {
  private static final ValidatedResult TOO_MANY_PARTS = ValidatedResult.failure(Util.makeTranslationKey("recipe", "part_swapping.too_many_parts"));

  @Getter
  protected final ResourceLocation id;

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !(tinkerable.getItem() instanceof ToolCore)) {
      return false;
    }
    // we have two concerns on part swapping:
    // part must be valid in the tool, and only up to one part can be swapped at once
    List<IToolPart> parts = ((ToolCore)tinkerable.getItem()).getToolDefinition().getRequiredComponents();
    boolean foundItem = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // too many items
        if (foundItem) {
          return false;
        }
        // part not in list
        Item item = stack.getItem();
        if (!(item instanceof IToolPart) || !parts.contains(item)) {
          return false;
        }
        foundItem = true;
      }
    }
    return foundItem;
  }

  /** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)}  */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    // copy the tool NBT to ensure the original tool is intact
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    List<IToolPart> parts = tool.getDefinition().getRequiredComponents();

    // prevent part swapping on large tools in small tables
    if (parts.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // not tool part, should never happen
        Item item = stack.getItem();
        if (!(item instanceof IToolPart)) {
          return ValidatedResult.PASS;
        }

        // ensure the part is valid
        IToolPart part = (IToolPart) item;
        IMaterial partMaterial = ((IToolPart)item).getMaterial(stack);
        if (partMaterial == IMaterial.UNKNOWN) {
          return ValidatedResult.PASS;
        }

        // we have a part and its not at this index, find the first copy of this part
        // means slot only matters if a tool uses a part twice
        int index = i;
        if (i >= parts.size() || parts.get(i).asItem() != item) {
          index = parts.indexOf(item);
          if (index == -1) {
            return ValidatedResult.PASS;
          }
        }

        // ensure there is a change in the part
        IMaterial toolMaterial = tool.getMaterial(index);
        if (toolMaterial == partMaterial) {
          return ValidatedResult.PASS;
        }

        // actual update
        tool = tool.copy();
        tool.replaceMaterial(index, partMaterial);

        // ensure no modifier problems
        ValidatedResult toolValidation = tool.validate();
        if (toolValidation.hasError()) {
          return toolValidation;
        }

        // if swapping in a new head, repair the tool
        if (part.getStatType().equals(HeadMaterialStats.ID)) {
          ToolStack finalTool = tool;
          // must have a registered recipe
          int cost = MaterialItemCostLookup.getTableCost(part);
          if (cost > 0) {
            // head stats determine repair amount
            Optional<HeadMaterialStats> optional = MaterialRegistry.getInstance().getMaterialStats(partMaterial.getIdentifier(), HeadMaterialStats.ID);
            optional.ifPresent(stats -> {
              // apply modifier repair boost
              float factor = cost / 4f; // vanilla ingots restore 25% durability, part head is worth 1 ingot per cost
              for (ModifierEntry entry : finalTool.getModifierList()) {
                factor = entry.getModifier().getRepairFactor(finalTool, entry.getLevel(), factor);
                if (factor <= 0) {
                  return;
                }
              }
              ToolDamageUtil.repair(finalTool, (int)(stats.getDurability() * factor));
            });
          }
        }

        return ValidatedResult.success(tool.createStack());
      }
    }
    // no item found, should never happen
    return ValidatedResult.PASS;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationPartSwappingSerializer.get();
  }
}
