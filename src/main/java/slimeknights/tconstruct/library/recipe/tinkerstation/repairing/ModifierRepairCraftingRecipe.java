package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeCraftingTableRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeCraftingTableRecipe.ToolFound;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Predicate;

public class ModifierRepairCraftingRecipe extends CustomRecipe implements IModifierRepairRecipe {
  public static final RecordLoadable<ModifierRepairCraftingRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), MODIFIER_FIELD, INGREDIENT_FIELD, REPAIR_AMOUNT_FIELD, ModifierRepairCraftingRecipe::new);
  private static final Predicate<ItemStack> TOOLS = stack -> stack.is(TinkerTags.Items.DURABILITY);

  @Getter
  private final ModifierId modifier;
  @Getter
  private final Ingredient ingredient;
  @Getter
  private final int repairAmount;
  public ModifierRepairCraftingRecipe(ResourceLocation idIn, ModifierId modifier, Ingredient ingredient, int repairAmount) {
    super(idIn, CraftingBookCategory.EQUIPMENT);
    this.modifier = modifier;
    this.ingredient = ingredient;
    this.repairAmount = repairAmount;
  }

  @Override
  public boolean matches(CraftingContainer inv, Level world) {
    ToolFound inputs = OverslimeCraftingTableRecipe.findTool(inv, TOOLS, ingredient);
    if (inputs == null) {
      return false;
    }
    // tool must have the modifier and be damaged
    IToolStackView tool = ToolStack.from(inputs.tool());
    return (tool.isBroken() || tool.getDamage() > 0) && tool.getModifierLevel(modifier) > 0;
  }

  @Override
  public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
    ToolFound inputs = OverslimeCraftingTableRecipe.findTool(inv, TOOLS, ingredient);
    if (inputs == null) {
      TConstruct.LOG.error("Recipe repair on {} failed to find items after matching", getId());
      return ItemStack.EMPTY;
    }

    // scale the repair based on the modifiers
    ToolStack tool = ToolStack.from(inputs.tool());
    float repairAmount = inputs.itemsFound() * this.repairAmount * tool.getModifierLevel(modifier);
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
    return tool.copyStack(inputs.tool(), 1);
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
    NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    // step 1: find out how much we need to repair
    ToolFound inputs = OverslimeCraftingTableRecipe.findTool(inv, TOOLS, ingredient);
    int repairPerItem = 0;
    int repairNeeded = 0;
    if (inputs != null) {
      ToolStack tool = ToolStack.from(inputs.tool());
      repairNeeded = tool.getDamage();
      float repairFloat = tool.getModifierLevel(modifier) * repairAmount;
      if (repairFloat > 0) {
        for (ModifierEntry entry : tool.getModifierList()) {
          repairFloat = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, repairFloat);
          if (repairFloat <= 0) {
            break;
          }
        }
        repairPerItem = (int)repairFloat;
      }
    }

    // step 2: consume as many items as are needed to do the repair
    return OverslimeCraftingTableRecipe.getRemainingItems(inv, ingredient, repairNeeded, repairPerItem);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return (width * height) >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingModifierRepair.get();
  }
}
