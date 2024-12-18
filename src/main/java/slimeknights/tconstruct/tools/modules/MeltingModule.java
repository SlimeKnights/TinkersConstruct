package slimeknights.tconstruct.tools.modules;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeCache;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.IOreRate;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/**
 * Module handling melting dropped items into fluids.
 * @param temperature      Maximum melting temperature
 * @param nuggetsPerMetal  Number of nuggets to produce per ingot of metal from ore recipes.
 * @param shardsPerGem     Number of quarter gems to produce per gem from ore recipes.
 * @param condition        General modifier conditions
 * @param oreRate          Extra parameter for the sake of the deprecated MeltingModifier, will be removed in 1.20.
 */
public record MeltingModule(LevelingInt temperature, LevelingInt nuggetsPerMetal, LevelingInt shardsPerGem, ModifierCondition<IToolStackView> condition, @Nullable IOreRate oreRate) implements ModifierModule, MeleeHitModifierHook, ProcessLootModifierHook, ConditionalModule<IToolStackView>, IMeltingContainer, IOreRate {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeltingModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.PROCESS_LOOT);
  /** Volatile data flag which makes a tool always melt regardless of tank space */
  public static final ResourceLocation FORCE_MELTING = TConstruct.getResource("force_melting");

  public static final RecordLoadable<MeltingModule> LOADER = RecordLoadable.create(
    LevelingInt.LOADABLE.requiredField("temperature", MeltingModule::temperature),
    LevelingInt.LOADABLE.requiredField("nuggets_per_metal", MeltingModule::nuggetsPerMetal),
    LevelingInt.LOADABLE.requiredField("shards_per_gem", MeltingModule::shardsPerGem),
    ModifierCondition.TOOL_FIELD,
    MeltingModule::new);

  /** @deprecated use {@link #MeltingModule(LevelingInt, LevelingInt, LevelingInt, ModifierCondition)} */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated(forRemoval = true)
  public MeltingModule {}

  public MeltingModule(LevelingInt temperature, LevelingInt nuggetsPerMetal, LevelingInt shardsPerGem, ModifierCondition<IToolStackView> condition) {
    this(temperature, nuggetsPerMetal, shardsPerGem, condition, null);
  }

  @Override
  public RecordLoadable<MeltingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }


  /* Melting container */
  /** Last melting recipe used */
  private static IMeltingRecipe lastRecipe = null;
  /** Current item stack being processed */
  @Setter
  private static ItemStack stack = ItemStack.EMPTY;
  /** Current modifier level being processed */
  private static int level = 0;

  @Override
  public ItemStack getStack() {
    return stack;
  }

  @Override
  public int applyOreBoost(OreRateType rate, int amount) {
    return switch (rate) {
      case METAL -> amount * nuggetsPerMetal.compute(level) / 9;
      case GEM -> amount * shardsPerGem.compute(level) / 9;
      default -> amount;
    };
  }

  @Override
  public IOreRate getOreRate() {
    return Objects.requireNonNullElse(oreRate, this);
  }


  /* Melting logic */

  /**
   * Gets the fluid for the given item
   * @param stack  Item
   * @param world  World instance
   * @return  Fluid
   */
  private FluidStack meltItem(ModifierEntry modifier, ItemStack stack, Level world) {
    level = modifier.intEffectiveLevel();
    setStack(stack);
    // first, update inventory
    IMeltingRecipe recipe = lastRecipe;
    if (recipe == null || !recipe.matches(this, world)) {
      recipe = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MELTING.get(), this, world).orElse(null);
      if (recipe == null) {
        setStack(ItemStack.EMPTY);
        return FluidStack.EMPTY;
      }
      lastRecipe = recipe;
    }
    // get the result if the temperature is right
    FluidStack result = FluidStack.EMPTY;
    if (recipe.getTemperature(this) <= temperature.compute(level)) {
      result = recipe.getOutput(this);
    }
    setStack(ItemStack.EMPTY);
    return result;
  }

  @Override
  public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
    if (!condition.matches(tool, modifier)) {
      return;
    }
    // if tank is full, nothing to do
    FluidStack current = TANK_HELPER.getFluid(tool);
    int capacity = TANK_HELPER.getCapacity(tool);
    if (current.getAmount() >= capacity) {
      return;
    }

    // allow tools to decide that we *must* melt the drops
    // for harvestable blocsk though, ignore that flag if the block was not effective (so we don't delete
    boolean forceMelt = tool.getVolatileData().getBoolean(FORCE_MELTING);
    if (forceMelt && context.hasParam(LootContextParams.BLOCK_STATE)) {
      BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
      forceMelt = tool.getHook(ToolHooks.IS_EFFECTIVE).isToolEffective(tool, state);
    }

    // try melting each item dropped
    Level world = context.getLevel();
    Iterator<ItemStack> iterator = generatedLoot.iterator();
    boolean isDirty = false;
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      FluidStack output = meltItem(modifier, stack, world);
      // fluid must match tank fluid
      if (!output.isEmpty() && (current.isEmpty() || current.isFluidEqual(output))) {
        int amount;

        // if forced to melt, melt everything regardless, fluid handler will ensure we don't overflow
        if (forceMelt) {
          amount = output.getAmount() * stack.getCount();
          iterator.remove();
        } else {
          // if not forced, then only melt what we have space for. Determine how many copies we can melt.
          int maxCopies = Math.min((capacity - current.getAmount()) / output.getAmount(), stack.getCount());
          if (maxCopies <= 0) {
            continue;
          }
          amount = output.getAmount() * maxCopies;
          // if it shrunk to empty, remove
          stack.shrink(maxCopies);
          if (stack.isEmpty()) {
            iterator.remove();
          }
        }
        // update the stored fluid
        if (current.isEmpty()) {
          output.setAmount(amount);
          current = output;
        } else {
          current.grow(amount);
        }
        isDirty = true;
      } else if (forceMelt) {
        // if forced to melt, anything unmeltable is deleted
        iterator.remove();
      }
    }
    if (isDirty) {
      TANK_HELPER.setFluid(tool, current);
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // must have done damage, and must be fully charged
    if (damageDealt > 0 && context.isFullyCharged() && condition.matches(tool, modifier)) {
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
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        if (fluid.isEmpty() || fluid.isFluidEqual(output)) {
          // recipe amount determines how much we get per hit, up to twice the recipe damage
          int fluidAmount;
          if (damageDealt < damagePerOutput * 2) {
            fluidAmount = (int)(output.getAmount() * damageDealt / damagePerOutput);
          } else {
            fluidAmount = output.getAmount() * 2;
          }

          // fluid must match that which is stored in the tank
          if (fluid.isEmpty()) {
            output.setAmount(fluidAmount);
            fluid = output;
          } else {
            fluid.grow(fluidAmount);
          }
          TANK_HELPER.setFluid(tool, fluid);
        }
      }
    }
  }


  /* Builder */
  public static Builder builder() {
    return new Builder();
  }

  @Accessors(fluent = true)
  @Setter
  public static class Builder extends ModuleBuilder.Stack<Builder> {
    private LevelingInt temperature = LevelingInt.flat(1000);
    private LevelingInt nuggetsPerMetal = LevelingInt.flat(9);
    private LevelingInt shardsPerGem = LevelingInt.flat(4);

    private Builder() {}

    /** Builds the final module */
    public MeltingModule build() {
      return new MeltingModule(temperature, nuggetsPerMetal, shardsPerGem, condition);
    }
  }
}
