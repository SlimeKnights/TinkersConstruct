package slimeknights.tconstruct.tools.modifiers.ability;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;
import slimeknights.tconstruct.library.recipe.melting.IMeltingInventory;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.smeltery.tileentity.module.EntityMeltingModule;

import java.util.Iterator;
import java.util.List;

public class MeltingModifier extends TankModifier {
  /** Max temperature allowed for melting items */
  private static final int MAX_TEMPERATURE = 1000;

  /** Last melting recipe used */
  private static IMeltingRecipe lastRecipe = null;
  /** Inventory used for finding recipes */
  private static final MeltingInventory inventory = new MeltingInventory();

  public MeltingModifier() {
    super(0xFFD800, MaterialValues.METAL_BLOCK);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // display name without the level, single use
    return super.getDisplayName();
  }

  /**
   * Gets the fluid for the given item
   * @param stack  Item
   * @param world  World instance
   * @return  Fluid
   */
  private static FluidStack meltItem(ItemStack stack, World world) {
    inventory.setStack(stack);
    // first, update inventory
    IMeltingRecipe recipe = lastRecipe;
    if (recipe == null || !recipe.matches(inventory, world)) {
      recipe = world.getRecipeManager().getRecipe(RecipeTypes.MELTING, inventory, world).orElse(null);
      if (recipe == null) {
        inventory.setStack(ItemStack.EMPTY);
        return FluidStack.EMPTY;
      }
      lastRecipe = recipe;
    }
    // get the result if the temperature is right
    FluidStack result = FluidStack.EMPTY;
    if (recipe.getTemperature(inventory) <= MAX_TEMPERATURE) {
      result = recipe.getOutput(inventory);
    }
    inventory.setStack(ItemStack.EMPTY);
    return result;
  }

  @Override
  public List<ItemStack> processLoot(IModifierToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if tank is full, nothing to do
    FluidStack current = getFluid(tool);
    int capacity = getCapacity(tool);
    if (current.getAmount() >= capacity) {
      return generatedLoot;
    }

    // try melting each item dropped
    World world = context.getWorld();
    Iterator<ItemStack> iterator = generatedLoot.iterator();
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      FluidStack output = meltItem(stack, world);
      // fluid must match tank fluid
      if (!output.isEmpty() && (current.isEmpty() || current.isFluidEqual(output))) {
        // determine how many copies we can melt
        int maxCopies = Math.min((capacity - current.getAmount()) / output.getAmount(), stack.getCount());

        // if it fits in the tank, remove
        if (maxCopies > 0) {
          FluidStack filled = fill(tool, current, output, output.getAmount() * maxCopies);
          // update current fluid stack for next iteration
          if (!filled.isEmpty()) {
            current = filled;
            // decrease items dropped
            stack.shrink(maxCopies);
            if (stack.isEmpty()) {
              iterator.remove();
            }
          }
        }
      }
    }
    return generatedLoot;
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // must have done damage, and must be fully charged
    if (damageDealt > 0 && context.isFullyCharged()) {
      // first, find the proper recipe
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        EntityMeltingRecipe recipe = EntityMeltingRecipeCache.findRecipe(context.getAttacker().getEntityWorld().getRecipeManager(), target.getType());
        FluidStack output;
        int damagePerOutput;
        if (recipe != null) {
          output = recipe.getOutput(target);
          damagePerOutput = recipe.getDamage();
        } else {
          output = EntityMeltingModule.getDefaultFluid();
          damagePerOutput = 2;
        }
        // recipe amount determines how much we get per hit, only scale (downwards) if we did not reach the damage threshold
        int fluidAmount = output.getAmount();
        if (damageDealt < damagePerOutput) {
          fluidAmount = (int)(fluidAmount * damageDealt / damagePerOutput);
        }

        // fluid must match that which is stored in the tank
        fill(tool, getFluid(tool), output, fluidAmount);
      }
    }
    return 0;
  }

  /** Helper for finding recipes in melting */
  private static class MeltingInventory implements IMeltingInventory {
    @Getter @Setter
    private ItemStack stack;

    @Override
    public int getNuggetsPerOre() {
      return Config.COMMON.melterNuggetsPerOre.get();
    }
  }
}
