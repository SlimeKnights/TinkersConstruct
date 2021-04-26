package slimeknights.tconstruct.tables.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.function.IntConsumer;

/** Recipe for repairing tools */
@RequiredArgsConstructor
public class TinkerStationRepairRecipe implements ITinkerStationRecipe {
  protected static final ValidatedResult FULLY_REPAIRED = ValidatedResult.failure(Util.makeTranslationKey("recipe", "tool_repair.fully_repaired"));
  /** No action int consumer for recipe result */
  private static final IntConsumer NO_ACTION = i -> {};

  @Getter
  private final ResourceLocation id;

  /**
   * Checks if the tool can be repaired with the given material
   * @param tool      Tool to check
   * @param material  Material to try
   * @return  True if the tool can be repaired with the given material
   */
  public static boolean canRepairWith(ToolStack tool, IMaterial material) {
    for (int part : tool.getDefinition().getRepairParts()) {
      if (tool.getMaterial(part) == material) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the material for the given slot
   * @param inv   Inventory instance
   * @param slot  Slot
   * @return  Material amount
   */
  protected IMaterial getMaterialFrom(ITinkerStationInventory inv, int slot) {
    // try repair kit first
    ItemStack item = inv.getInput(slot);
    if (item.getItem() == TinkerToolParts.repairKit.get()) {
      return IMaterialItem.getMaterialFromStack(item);
    }
    // material recipe fallback
    MaterialRecipe recipe = inv.getInputMaterial(slot);
    if (recipe != null) {
      return recipe.getMaterial();
    }
    return IMaterial.UNKNOWN;
  }

  /** Gets the amount to repair per item */
  protected float repairFactorPerItem(ITinkerStationInventory inv, int slot, IMaterial repairMaterial) {
    ItemStack stack = inv.getInput(slot);
    // repair kit first
    if (stack.getItem() == TinkerToolParts.repairKit.get()) {
      // multiply by 2 (part cost), divide again by 4 (vanilla ingots restore 25%), leaves divide by 2
      return MaterialRecipe.getHeadDurability(repairMaterial.getIdentifier()) / 2f;
    }
    // material recipe fallback
    MaterialRecipe recipe = inv.getInputMaterial(slot);
    if (recipe != null) {
      return recipe.getRepairPerItem();
    }
    return 0;
  }

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    // must be repairable
    ItemStack tinkerable = inv.getTinkerableStack();
    // TODO: repairable tag instead?
    if (tinkerable.isEmpty() || !TinkerTags.Items.MULTIPART_TOOL.contains(tinkerable.getItem())) {
      return false;
    }

    // validate materials
    IMaterial material = null;
    ToolStack tool = ToolStack.from(tinkerable);
    for (int i = 0; i < inv.getInputCount(); i++) {
      // skip empty slots
      ItemStack stack = inv.getInput(i);
      if (stack.isEmpty()) {
        continue;
      }

      // ensure we have a material
      IMaterial inputMaterial = getMaterialFrom(inv, i);
      if (inputMaterial == IMaterial.UNKNOWN) {
        return false;
      }

      // on first match, store and validate the material. For later matches, just ensure material matches
      if (material == null) {
        material = inputMaterial;
        if (!canRepairWith(tool, material)) {
          return false;
        }
      } else if (material != inputMaterial) {
        return false;
      }
    }

    // must have a material (will only be null if all slots were empty at this point)
    return material != null;
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    if (tool.getDefinition() == ToolDefinition.EMPTY) {
      return ValidatedResult.PASS;
    }
    // ensure input needs repair
    if (!tool.isBroken() && tool.getDamage() == 0) {
      return FULLY_REPAIRED;
    }

    // first, determine how much we can repair
    IMaterial primaryMaterial = tool.getMaterial(tool.getDefinition().getRepairParts()[0]);
    int repairNeeded = tool.getDamage();
    int repairRemaining = repairNeeded;

    // iterate stacks, adding up amount we can repair, assumes the material is correct per #matches()
    for (int i = 0; i < inv.getInputCount() && repairRemaining > 0; i++) {
      repairRemaining -= repairFromSlot(tool, primaryMaterial, inv, repairRemaining, i, NO_ACTION);
    }

    // did we actually repair something?
    if (repairRemaining < repairNeeded) {
      tool = tool.copy();
      ToolDamageUtil.repair(tool, repairNeeded - repairRemaining);

      // repair remaining can be negative
      return ValidatedResult.success(tool.createStack());
    }

    // for some odd reason, did not repair anything
    return ValidatedResult.PASS;
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    ToolStack inputTool = ToolStack.from(inv.getTinkerableStack());
    ToolStack resultTool = ToolStack.from(result);

    // iterate stacks, removing items as we repair
    int repairRemaining = inputTool.getDamage() - resultTool.getDamage();
    IMaterial primaryMaterial = inputTool.getMaterial(inputTool.getDefinition().getRepairParts()[0]);
    for (int i = 0; i < inv.getInputCount() && repairRemaining > 0; i++) {
      final int slot = i;
      repairRemaining -= repairFromSlot(inputTool, primaryMaterial, inv, repairRemaining, i, count -> inv.shrinkInput(slot, count));
    }

    if (repairRemaining > 0) {
      TConstruct.log.error("Recipe repair on {} consumed too few items. {} durability unaccounted for", result, repairRemaining);
    }
  }

  /**
   * Gets the amount to repair from the given slot
   * @param tool            Tool instance
   * @param primaryMaterial Material of the primary head
   * @param inv             Inventory instance
   * @param repairNeeded    Amount of remaining repair needed
   * @param slot            Input slot
   * @param amountConsumer  Action to perform on repair, input is the amount consumed
   * @return  Repair from this slot
   */
  private int repairFromSlot(ToolStack tool, IMaterial primaryMaterial, ITinkerStationInventory inv, int repairNeeded, int slot, IntConsumer amountConsumer) {
    ItemStack stack = inv.getInput(slot);
    if (!stack.isEmpty()) {
      // we have a recipe with matching stack, find out how much we can repair
      IMaterial repairMaterial = getMaterialFrom(inv, slot);
      float durabilityPerItem = repairFactorPerItem(inv, slot, repairMaterial);
      if (repairMaterial != IMaterial.UNKNOWN && durabilityPerItem > 0) {
        // if not the primary material, reduced effectiveness
        if (repairMaterial != primaryMaterial) {
          durabilityPerItem /= tool.getDefinition().getBaseStatDefinition().getPrimaryHeadWeight();
        }

        // adjust the factor based on modifiers
        // main example is wood, +25% per level
        for (ModifierEntry entry : tool.getModifierList()) {
          durabilityPerItem = entry.getModifier().getRepairFactor(tool, entry.getLevel(), durabilityPerItem);
          if (durabilityPerItem <= 0) {
            return 0;
          }
        }

        // apply this recipe as many times as we need (if stack has more than enough to repair) or can (if stack will not fully repair)
        int applied = Math.min(stack.getCount(), (int)Math.ceil(repairNeeded / durabilityPerItem));
        amountConsumer.accept(applied);
        return (int)(applied * durabilityPerItem);
      }
    }

    return 0;
  }


  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationRepairSerializer.get();
  }

  /** @deprecated Use {@link #getCraftingResult(ITinkerStationInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }
}
