package slimeknights.tconstruct.tools.modifiers.ability;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;
import slimeknights.tconstruct.library.recipe.melting.IMeltingInventory;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.smeltery.tileentity.module.EntityMeltingModule;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class MeltingModifier extends SingleUseModifier {
  /** Key for storing fluid in NBT */
  private static final ResourceLocation FLUID_KEY = Util.getResource("melting_fluid");
  /** Key for storing determining max capacity */
  public static final ResourceLocation CAPACITY_KEY = Util.getResource("melter_capacity");

  /** Max capacity from melting */
  private static final int DEFAULT_CAPACITY = MaterialValues.METAL_BLOCK;
  /** Function to parse fluid from NBT */
  private static final BiFunction<CompoundNBT, String, FluidStack> PARSE_FLUID = (nbt, key) -> FluidStack.loadFluidStackFromNBT(nbt.getCompound(key));
  /** Last melting recipe used */
  private static IMeltingRecipe lastRecipe = null;
  /** Inventory used for finding recipes */
  private static final MeltingInventory inventory = new MeltingInventory();

  public MeltingModifier() {
    super(0xFFD800);
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
        return FluidStack.EMPTY;
      }
      lastRecipe = recipe;
    }
    // next, get result from recipe
    FluidStack result = recipe.getOutput(inventory);
    inventory.setStack(ItemStack.EMPTY);
    return result;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, ITooltipFlag flag, boolean detailed) {
    FluidStack current = tool.getPersistentData().get(FLUID_KEY, PARSE_FLUID);
    if (!current.isEmpty()) {
      tooltip.add(applyStyle(current.getDisplayName().copyRaw().appendString(": " + current.getAmount() + " / " + DEFAULT_CAPACITY)));
    }
  }

  /** Gets the current capacity of the melter tank */
  public static int getCapacity(IModDataReadOnly volatileData) {
    if (!volatileData.contains(CAPACITY_KEY, NBT.TAG_ANY_NUMERIC)) {
      return DEFAULT_CAPACITY;
    }
    return volatileData.getInt(CAPACITY_KEY);
  }

  /** Increases teh melter tank's capacity by the given amount */
  public static void addCapacity(ModDataNBT volatileData, int amount) {
    volatileData.putInt(CAPACITY_KEY, getCapacity(volatileData) + amount);
  }

  /** Fills the tool with the given fluid */
  private static FluidStack fillTool(IModifierToolStack tool, FluidStack current, FluidStack output, int amount) {
    int capacity = getCapacity(tool.getVolatileData());
    if (current.isEmpty()) {
      // cap fluid at capacity, store in tool
      output.setAmount(Math.min(amount, capacity));
      tool.getPersistentData().put(FLUID_KEY, output.writeToNBT(new CompoundNBT()));
      return output;
    } else if (current.isFluidEqual(output)) {
      // boost fluid by amount and store
      current.setAmount(Math.min(current.getAmount() + amount, capacity));
      tool.getPersistentData().put(FLUID_KEY, current.writeToNBT(new CompoundNBT()));
      return current;
    }
    return FluidStack.EMPTY;
  }

  @Override
  public List<ItemStack> processLoot(IModifierToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if tank is full, nothing to do
    FluidStack current = tool.getPersistentData().get(FLUID_KEY, PARSE_FLUID);
    if (current.getAmount() == DEFAULT_CAPACITY) {
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
        int maxCopies = Math.min((DEFAULT_CAPACITY - current.getAmount()) / output.getAmount(), stack.getCount());

        // if it fits in the tank, remove
        if (maxCopies > 0) {
          FluidStack filled = fillTool(tool, current, output, output.getAmount() * maxCopies);
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
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    // must have done damage, and must be fully charged
    if (damageDealt > 0 && fullyCharged) {
      // first, find the proper recipe
      EntityMeltingRecipe recipe = EntityMeltingRecipeCache.findRecipe(attacker.getEntityWorld().getRecipeManager(), target.getType());
      FluidStack output;
      int damagePerOutput;
      if (recipe != null) {
        output = recipe.getOutput(target);
        damagePerOutput = recipe.getDamage();
      } else {
        output = EntityMeltingModule.getDefaultFluid();
        damagePerOutput = 2;
      }
      // scale the fluid based on the amount, only produce half as much as the smeltery
      int fluidAmount = (int)(output.getAmount() * damageDealt / damagePerOutput);

      // fluid must match that which is stored in the tank
      fillTool(tool, tool.getPersistentData().get(FLUID_KEY, PARSE_FLUID), output, fluidAmount);
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
