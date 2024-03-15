package slimeknights.tconstruct.tools.modifiers.ability.tool;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.fluid.TankModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;

import java.util.Iterator;
import java.util.List;

public class MeltingModifier extends NoLevelsModifier implements MeleeHitModifierHook {
  /** Max temperature allowed for melting items */
  private static final int MAX_TEMPERATURE = 1000;

  /** Last melting recipe used */
  private static IMeltingRecipe lastRecipe = null;
  /** Inventory used for finding recipes */
  private static final MeltingContainer inventory = new MeltingContainer();

  private TankModule tank;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    tank = new TankModule(FluidType.BUCKET_VOLUME, true);
    hookBuilder.addModule(tank);
    hookBuilder.addHook(this, TinkerHooks.MELEE_HIT);
  }

  /**
   * Gets the fluid for the given item
   * @param stack  Item
   * @param world  World instance
   * @return  Fluid
   */
  private static FluidStack meltItem(ItemStack stack, Level world) {
    inventory.setStack(stack);
    // first, update inventory
    IMeltingRecipe recipe = lastRecipe;
    if (recipe == null || !recipe.matches(inventory, world)) {
      recipe = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MELTING.get(), inventory, world).orElse(null);
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
  public void processLoot(IToolStackView tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    // if tank is full, nothing to do
    FluidStack current = tank.getFluid(tool);
    int capacity = tank.getCapacity(tool);
    if (current.getAmount() >= capacity) {
      return;
    }

    // try melting each item dropped
    Level world = context.getLevel();
    Iterator<ItemStack> iterator = generatedLoot.iterator();
    boolean isDirty = false;
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      FluidStack output = meltItem(stack, world);
      // fluid must match tank fluid
      if (!output.isEmpty() && (current.isEmpty() || current.isFluidEqual(output))) {
        // determine how many copies we can melt
        int maxCopies = Math.min((capacity - current.getAmount()) / output.getAmount(), stack.getCount());

        // if it fits in the tank, remove
        if (maxCopies > 0) {
          // fill fluid
          int amount = output.getAmount() * maxCopies;
          if (current.isEmpty()) {
            output.setAmount(amount);
            current = output;
          } else {
            current.setAmount(amount);
          }
          isDirty = true;
          // decrease items dropped
          stack.shrink(maxCopies);
          if (stack.isEmpty()) {
            iterator.remove();
          }
        }
      }
    }
    if (isDirty) {
      tank.setFluid(tool, current);
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // must have done damage, and must be fully charged
    if (damageDealt > 0 && context.isFullyCharged()) {
      // first, find the proper recipe
      LivingEntity target = context.getLivingTarget();
      if (target != null) {
        EntityMeltingRecipe recipe = EntityMeltingRecipeCache.findRecipe(context.getAttacker().level.getRecipeManager(), target.getType());
        FluidStack output;
        int damagePerOutput;
        if (recipe != null) {
          output = recipe.getOutput(target);
          damagePerOutput = recipe.getDamage();
        } else {
          output = EntityMeltingModule.getDefaultFluid();
          damagePerOutput = 2;
        }
        // recipe amount determines how much we get per hit, up to twice the recipe damage
        int fluidAmount;
        if (damageDealt < damagePerOutput * 2) {
          fluidAmount = (int)(output.getAmount() * damageDealt / damagePerOutput);
        } else {
          fluidAmount = output.getAmount() * 2;
        }

        // fluid must match that which is stored in the tank
        FluidStack fluid = tank.getFluid(tool);
        if (fluid.isEmpty()) {
          output.setAmount(fluidAmount);
          fluid = output;
        } else {
          fluid.grow(fluidAmount);
        }
        tank.setFluid(tool, fluid);
      }
    }
  }

  /** Helper for finding recipes in melting */
  private static class MeltingContainer implements IMeltingContainer {
    @Getter @Setter
    private ItemStack stack;

    @Override
    public IOreRate getOreRate() {
      return Config.COMMON.melterOreRate;
    }
  }
}
