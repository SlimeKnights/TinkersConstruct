package slimeknights.tconstruct.tools.modules;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeLookup;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningTierToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module that makes a tool effective based on whether it has a valid melting recipe and the result would fit in the tool's tank.
 * Note the former is possible with {@link IsEffectiveModule} using {@link slimeknights.tconstruct.library.json.predicate.TinkerPredicate#CAN_MELT_BLOCK}, but the latter requires tool context.
 */
public record MeltingFluidEffectiveModule(IJsonPredicate<BlockState> predicate, int temperature, boolean ignoreTier) implements ToolModule, IsEffectiveToolHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeltingFluidEffectiveModule>defaultHooks(ToolHooks.IS_EFFECTIVE);
  public static final RecordLoadable<MeltingFluidEffectiveModule> LOADER = RecordLoadable.create(
    BlockPredicate.LOADER.directField("predicate_type", MeltingFluidEffectiveModule::predicate),
    IntLoadable.FROM_ZERO.requiredField("temperature", MeltingFluidEffectiveModule::temperature),
    BooleanLoadable.INSTANCE.defaultField("ignore_tier", false, MeltingFluidEffectiveModule::ignoreTier),
    MeltingFluidEffectiveModule::new);

  @Override
  public RecordLoadable<MeltingFluidEffectiveModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isToolEffective(IToolStackView tool, BlockState state) {
    // outer predicate must match, typically this is a very inclusive predicate
    if (predicate.matches(state)) {
      // tool must have capacity
      int capacity = ToolTankHelper.TANK_HELPER.getCapacity(tool);
      if (capacity > 0) {
        // tool must have available space
        // we could check that the output fits but that's a little unreliable as there may be an ore multiplier and the drop might be different than the block
        FluidStack currentFluid = ToolTankHelper.TANK_HELPER.getFluid(tool);
        if (capacity > currentFluid.getAmount()) {
          // new fluid must match current fluid
          FluidStack meltingResult = MeltingRecipeLookup.findResult(state.getBlock(), temperature);
          return (!meltingResult.isEmpty() && (currentFluid.isEmpty() || currentFluid.isFluidEqual(meltingResult)))
                 // tier must also match
                 && (ignoreTier || TierSortingRegistry.isCorrectTierForDrops(MiningTierToolHook.getTier(tool), state));
        }
      }
    }
    return false;
  }
}
