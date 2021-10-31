package slimeknights.tconstruct.tables.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;

import javax.annotation.Nullable;

/** Recipe using repair kits in the crafting table */
public class CraftingTableRepairKitRecipe extends SpecialRecipe {
  public CraftingTableRepairKitRecipe(ResourceLocation id) {
    super(id);
  }

  /**
   * Checks if the tool is valid for this recipe
   * @param stack  Tool to check
   * @return  True if valid
   */
  protected boolean toolMatches(ItemStack stack) {
    Item item = stack.getItem();
    return TinkerTags.Items.MULTIPART_TOOL.contains(item) && TinkerTags.Items.DURABILITY.contains(item);
  }

  /**
   * Gets the tool stack and the repair kit material from the crafting grid
   * @param inv  Crafting inventory
   * @return  Relevant inputs, or null if invalid
   */
  @Nullable
  protected Pair<ToolStack, IMaterial> getRelevantInputs(CraftingInventory inv) {
    ToolStack tool = null;
    IMaterial material = null;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack.isEmpty()) {
        continue;
      }
      // repair kit - update material
      if (stack.getItem() == TinkerToolParts.repairKit.get()) {
        // already found repair kit
        if (material != null) {
          return null;
        }
        IMaterial inputMaterial = IMaterialItem.getMaterialFromStack(stack);
        // if the material is invalid, also fail
        if (inputMaterial == IMaterial.UNKNOWN) {
          return null;
        }
        material = inputMaterial;
      } else if (toolMatches(stack)) {
        // cannot repair multiple tools
        if (tool != null) {
          return null;
        }
        // tool must be damaged
        tool = ToolStack.from(stack);
        if (!tool.isBroken() && tool.getDamage() == 0) {
          return null;
        }
      } else {
        // unknown item input
        return null;
      }
    }
    if (tool == null || material == null) {
      return null;
    }
    return Pair.of(tool, material);
  }

  @Override
  public boolean matches(CraftingInventory inv, World worldIn) {
    Pair<ToolStack, IMaterial> inputs = getRelevantInputs(inv);
    return inputs != null && TinkerStationRepairRecipe.getRepairIndex(inputs.getFirst(), inputs.getSecond()) >= 0;
  }

  /** Gets the amount to repair for the given material */
  protected float getRepairAmount(IModifierToolStack tool, IMaterial repairMaterial) {
    MaterialStatsId repairStats = TinkerStationRepairRecipe.getDefaultStatsId(tool, repairMaterial);
    float repairAmount = MaterialRecipe.getRepairDurability(repairMaterial.getIdentifier(), repairStats) * 2 / MaterialRecipe.INGOTS_PER_REPAIR;
    if (repairAmount > 0) {
      repairAmount *= TinkerStationRepairRecipe.getRepairWeight(tool, repairMaterial);
    }
    return repairAmount;
  }

  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {
    Pair<ToolStack, IMaterial> inputs = getRelevantInputs(inv);
    if (inputs == null) {
      TConstruct.LOG.error("Recipe repair on {} failed to find items after matching", getId());
      return ItemStack.EMPTY;
    }

    // first identify materials and durability
    ToolStack tool = inputs.getFirst().copy();
    // vanilla says 25% durability per ingot, repair kits are worth 2 ingots
    float repairAmount = getRepairAmount(tool, inputs.getSecond());
    if (repairAmount > 0) {
      // adjust the factor based on modifiers
      // main example is wood, +25% per level
      for (ModifierEntry entry : tool.getModifierList()) {
        repairAmount = entry.getModifier().getRepairFactor(tool, entry.getLevel(), repairAmount);
        if (repairAmount <= 0) {
          // failed to repair
          return tool.createStack();
        }
      }

      // repair the tool
      ToolDamageUtil.repair(tool, (int)repairAmount);
    }
    // return final stack
    return tool.createStack();
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.craftingTableRepairSerializer.get();
  }
}
