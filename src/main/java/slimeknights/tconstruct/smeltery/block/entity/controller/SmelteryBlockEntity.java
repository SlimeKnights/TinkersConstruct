package slimeknights.tconstruct.smeltery.block.entity.controller;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MultiAlloyingModule;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.SmelteryAlloyTank;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock.StructureData;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.SmelteryMultiblock;

import javax.annotation.Nullable;

public class SmelteryBlockEntity extends HeatingStructureBlockEntity {
  /** Fluid capacity per internal block */
  private static final int CAPACITY_PER_BLOCK = FluidValues.INGOT * 12;
  /** Number of wall blocks needed to increase the fuel cost by 1 */
  private static final int BLOCKS_PER_FUEL = 15;
  /** Name of the UI */
  private static final Component NAME = TConstruct.makeTranslation("gui", "smeltery");

  /** Module handling alloys */
  private final SmelteryAlloyTank alloyTank = new SmelteryAlloyTank(tank);
  @Getter
  private final MultiAlloyingModule alloyingModule = new MultiAlloyingModule(this, alloyTank);

  public SmelteryBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerSmeltery.smeltery.get(), pos, state, NAME);
  }

  @Override
  protected HeatingStructureMultiblock<?> createMultiblock() {
    return new SmelteryMultiblock(this);
  }

  @Override
  protected MeltingModuleInventory createMeltingInventory() {
    return new MeltingModuleInventory(this, tank, Config.COMMON.smelteryOreRate);
  }

  @Override
  protected boolean isDebugItem(ItemStack stack) {
    return stack.is(Items.SMELTERY_DEBUG);
  }

  @Override
  protected void heat() {
    if (structure == null || level == null) {
      return;
    }

    // the next set of behaviors all require fuel, skip if no tanks
    if (structure.hasTanks()) {
      // every second, interact with entities, will consume fuel if needed
      boolean entityMelted = false;
      if (tick == 12) {
        entityMelted = entityModule.interactWithEntities();
      }
      // run in four phases alternating each tick, so each thing runs once every 4 ticks
      switch (tick % 4) {
        // first tick, find fuel if needed
        case 0:
          if (!fuelModule.hasFuel()) {
            // if we melted something already, we need fuel
            if (entityMelted) {
              fuelModule.findFuel(true);
            } else {
              // both alloying and melting need to know the temperature
              int possibleTemp = fuelModule.findFuel(false);
              alloyTank.setTemperature(possibleTemp);
              if (meltingInventory.canHeat(possibleTemp) || alloyingModule.canAlloy()) {
                fuelModule.findFuel(true);
              }
            }
          }
          break;
        // second tick: melt items
        case 1:
          if (fuelModule.hasFuel()) {
            meltingInventory.heatItems(fuelModule.getTemperature(), fuelModule.getRate());
          } else {
            meltingInventory.coolItems();
          }
          break;
        // third tick: alloy alloys
        case 2:
          if (fuelModule.hasFuel()) {
            alloyTank.setTemperature(fuelModule.getTemperature());
            alloyingModule.doAlloy();
          }
          break;
        // fourth tick: consume fuel, update fluids
        case 3: {
          // update the active state
          boolean hasFuel = fuelModule.hasFuel();
          BlockState state = getBlockState();
          if (state.getValue(ControllerBlock.ACTIVE) != hasFuel) {
            level.setBlockAndUpdate(worldPosition, state.setValue(ControllerBlock.ACTIVE, hasFuel));
          }
          fuelModule.decreaseFuel(fuelRate);
          break;
        }
      }
    } else {
      cool();
    }
  }

  @Override
  protected void setStructure(@Nullable StructureData structure) {
    super.setStructure(structure);
    if (structure != null) {
      int dx = structure.getInnerX(), dy = structure.getInnerY(), dz = structure.getInnerZ();
      int size = dx * dy * dz;
      tank.setCapacity(CAPACITY_PER_BLOCK * size);
      meltingInventory.resize(size, dropItem);
      // fuel rate: every 15 blocks in the wall makes the fuel cost 1 more
      // perimeter: 2 of the X and the Z wall, one of the floor
      fuelRate = 1 + ((2 * (dx * dy) + 2 * (dy * dz) + (dx * dz))) / BLOCKS_PER_FUEL;
    }
  }

  @Override
  public void notifyFluidsChanged(FluidChange type, FluidStack fluid) {
    super.notifyFluidsChanged(type, fluid);

    // adding a new fluid means recipes that previously did not match might match now
    // can ignore removing a fluid as that is handled internally by the module
    if (type == FluidChange.ADDED) {
      alloyingModule.clearCachedRecipes();
    }
  }
}
