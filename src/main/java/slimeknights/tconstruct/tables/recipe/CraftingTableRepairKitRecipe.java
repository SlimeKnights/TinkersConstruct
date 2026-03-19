package slimeknights.tconstruct.tables.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.IRepairKitItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;

/** Recipe using repair kits in the crafting table */
public class CraftingTableRepairKitRecipe extends CustomRecipe {
  public CraftingTableRepairKitRecipe(ResourceLocation id) {
    super(id, CraftingBookCategory.EQUIPMENT);
  }

  /**
   * Checks if the tool is valid for this recipe
   * @param stack  Tool to check
   * @return  True if valid
   */
  protected boolean toolMatches(ItemStack stack) {
    return stack.is(TinkerTags.Items.DURABILITY);
  }

  protected record ToolRepair(ItemStack tool, ItemStack repairKit) {}

  /**
   * Gets the tool stack and the repair kit material from the crafting grid
   * @param inv  Crafting inventory
   * @return  Relevant inputs, or null if invalid
   */
  @Nullable
  protected ToolRepair getRelevantInputs(CraftingContainer inv) {
    ItemStack tool = null;
    ItemStack repairKit = null;
    for (int i = 0; i < inv.getContainerSize(); i++) {
      ItemStack stack = inv.getItem(i);
      if (stack.isEmpty()) {
        continue;
      }
      // repair kit - update material
      if (stack.getItem() instanceof IRepairKitItem kit && kit.canRepairInCraftingTable()) {
        // already found repair kit
        if (repairKit != null) {
          return null;
        }
        repairKit = stack;
      } else if (toolMatches(stack)) {
        // cannot repair multiple tools
        if (tool != null) {
          return null;
        }
        tool = stack;
      } else {
        // unknown item input
        return null;
      }
    }
    if (tool == null || repairKit == null) {
      return null;
    }
    return new ToolRepair(tool, repairKit);
  }

  @Override
  public boolean matches(CraftingContainer inv, Level worldIn) {
    // no match
    ToolRepair inputs = getRelevantInputs(inv);
    if (inputs == null) {
      return false;
    }
    // if the material is invalid
    MaterialId inputMaterial = IMaterialItem.getMaterialFromStack(inputs.repairKit).getId();
    if (inputMaterial.equals(IMaterial.UNKNOWN_ID)) {
      return false;
    }
    // tool must be damaged and be repairable with this material
    IToolStackView tool = ToolStack.from(inputs.tool);
    return (tool.isBroken() || tool.getDamage() > 0) && MaterialRepairToolHook.canRepairWith(tool, inputMaterial);
  }

  /** Gets the amount to repair for the given material */
  protected float getRepairAmount(IToolStackView tool, ItemStack repairStack) {
    return MaterialRepairToolHook.repairAmount(tool, IMaterialItem.getMaterialFromStack(repairStack).getId());
  }

  @Override
  public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
    ToolRepair inputs = getRelevantInputs(inv);
    if (inputs == null) {
      TConstruct.LOG.error("Recipe repair on {} failed to find items after matching", getId());
      return ItemStack.EMPTY;
    }

    // first identify materials and durability
    ToolStack tool = ToolStack.from(inputs.tool);
    // vanilla says 25% durability per ingot, repair kits are worth 2 ingots
    float repairAmount = getRepairAmount(tool, inputs.repairKit);
    if (repairAmount <= 0) {
      return ItemStack.EMPTY;
    }

    // add in repair kit value
    repairAmount *= (inputs.repairKit.getItem() instanceof IRepairKitItem kit ? kit.getRepairAmount() : Config.COMMON.repairKitAmount.get().floatValue()) / MaterialRecipe.INGOTS_PER_REPAIR;
    // adjust the factor based on modifiers
    // main example is wood, +25% per level
    for (ModifierEntry entry : tool.getModifierList()) {
      repairAmount = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, repairAmount);
      if (repairAmount <= 0) {
        // failed to repair
        return ItemStack.EMPTY;
      }
    }

    // repair the tool
    tool = tool.copy();
    ToolDamageUtil.repair(tool, (int)repairAmount);
    // return final stack
    return tool.copyStack(inputs.tool);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.craftingTableRepairSerializer.get();
  }
}
