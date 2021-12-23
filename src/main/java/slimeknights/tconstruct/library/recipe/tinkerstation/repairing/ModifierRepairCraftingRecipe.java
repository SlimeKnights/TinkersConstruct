package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer.IModifierRepairRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class ModifierRepairCraftingRecipe extends SpecialRecipe implements IModifierRepairRecipe {
  @Getter
  private final Modifier modifier;
  @Getter
  private final Ingredient ingredient;
  @Getter
  private final int repairAmount;
  public ModifierRepairCraftingRecipe(ResourceLocation idIn, Modifier modifier, Ingredient ingredient, int repairAmount) {
    super(idIn);
    this.modifier = modifier;
    this.ingredient = ingredient;
    this.repairAmount = repairAmount;
  }

  /**
   * Gets the tool stack and the repair kit material from the crafting grid
   * @param inv  Crafting inventory
   * @return  Relevant inputs, or null if invalid
   */
  @Nullable
  protected Pair<ToolStack, Integer> getRelevantInputs(CraftingInventory inv) {
    ToolStack tool = null;
    int itemsFound = 0;
    int modifierLevel = 0;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      // repair kit - update material
      if (TinkerTags.Items.DURABILITY.contains(stack.getItem())) {
        // cannot repair multiple tools
        if (tool != null) {
          return null;
        }
        // tool must be damaged
        tool = ToolStack.from(stack);
        if (!tool.isBroken() && tool.getDamage() == 0) {
          return null;
        }
        // tool must have the modifier
        modifierLevel = tool.getModifierLevel(modifier);
        if (modifierLevel == 0) {
          return null;
        }

        // if we found a stack, add it to our count
      } else if (ingredient.test(stack)) {
        itemsFound++;
      } else {
        // unknown item input
        return null;
      }
    }
    // failed to find a tool or item? failed
    if (tool == null || itemsFound == 0) {
      return null;
    }
    return Pair.of(tool, repairAmount * itemsFound * modifierLevel);
  }

  @Override
  public boolean matches(CraftingInventory inv, World world) {
    return getRelevantInputs(inv) != null;
  }

  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    Pair<ToolStack, Integer> inputs = getRelevantInputs(inv);
    if (inputs == null) {
      TConstruct.LOG.error("Recipe repair on {} failed to find items after matching", getId());
      return ItemStack.EMPTY;
    }

    // scale the repair based on the modifiers
    float repairAmount = inputs.getSecond();
    ToolStack tool = inputs.getFirst();
    for (ModifierEntry entry : tool.getModifierList()) {
      repairAmount = entry.getModifier().getRepairFactor(tool, entry.getLevel(), repairAmount);
      if (repairAmount <= 0) {
        // failed to repair
        return ItemStack.EMPTY;
      }
    }

    // repair the tool
    tool = tool.copy();
    ToolDamageUtil.repair(tool, (int)repairAmount);
    return tool.createStack();
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
    NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    // step 1: find out how much we need to repair
    Pair<ToolStack, Integer> inputs = getRelevantInputs(inv);
    int repairPerItem = 0;
    int repairNeeded = 0;
    if (inputs != null) {
      ToolStack tool = inputs.getFirst();
      repairNeeded = tool.getDamage();
      float repairFloat = tool.getModifierLevel(modifier) * repairAmount;
      if (repairFloat > 0) {
        for (ModifierEntry entry : tool.getModifierList()) {
          repairFloat = entry.getModifier().getRepairFactor(tool, entry.getLevel(), repairFloat);
          if (repairFloat <= 0) {
            break;
          }
        }
        repairPerItem = (int)repairFloat;
      }
    }

    // step 2: consume as many items as are needed to do the repair
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (ingredient.test(stack)) {
        // if done repairing, leave the items
        if (repairNeeded <= 0) {
          continue;
        }
        repairNeeded -= repairPerItem;
      }
      if (stack.hasContainerItem()) {
        list.set(i, stack.getContainerItem());
      }
    }

    return list;
  }

  @Override
  public boolean canFit(int width, int height) {
    return (width * height) >= 2;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingModifierRepair.get();
  }
}
