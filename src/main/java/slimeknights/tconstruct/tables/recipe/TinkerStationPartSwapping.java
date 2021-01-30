package slimeknights.tconstruct.tables.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

/**
 * Recipe that replaces a tool part with another
 */
@AllArgsConstructor
public class TinkerStationPartSwapping implements ITinkerStationRecipe {
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
    return true;
  }

  /** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)}  */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack getCraftingResult(ITinkerStationInventory inv) {
    // copy the tool NBT to ensure the original tool is intact
    ToolStack tool = ToolStack.copyFrom(inv.getTinkerableStack());
    List<IToolPart> parts = tool.getDefinition().getRequiredComponents();
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // not tool part, should never happen
        Item item = stack.getItem();
        if (!(item instanceof IToolPart)) {
          return ItemStack.EMPTY;
        }

        // ensure the part is valid
        IMaterial partMaterial = ((IToolPart)item).getMaterial(stack);
        if (partMaterial == IMaterial.UNKNOWN) {
          return ItemStack.EMPTY;
        }

        // we have a part and its not at this index, find the first copy of this part
        // means slot only matters if a tool uses a part twice
        int index = i;
        if (i >= parts.size() || parts.get(i).asItem() != item) {
          index = parts.indexOf(item);
          if (index == -1) {
            return ItemStack.EMPTY;
          }
        }

        // ensure there is a change in the part
        IMaterial toolMaterial = tool.getMaterial(index);
        if (toolMaterial == partMaterial) {
          return ItemStack.EMPTY;
        }

        // actual update
        tool.replaceMaterial(index, partMaterial);
        return tool.createStack();
      }
    }
    // no item found, should never happen
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationPartSwappingSerializer.get();
  }
}
