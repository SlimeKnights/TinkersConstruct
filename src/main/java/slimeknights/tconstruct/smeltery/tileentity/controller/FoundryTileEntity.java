package slimeknights.tconstruct.smeltery.tileentity.controller;

import net.minecraft.block.BlockState;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.module.ByproductMeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.FoundryMultiblock;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.tileentity.multiblock.HeatingStructureMultiblock.StructureData;

import javax.annotation.Nullable;

public class FoundryTileEntity extends HeatingStructureTileEntity {
  /** Fluid capacity per internal block */
  private static final int CAPACITY_PER_BLOCK = FluidValues.INGOT * 8;
  /** Number of wall blocks needed to increase the fuel cost by 1
   * this is a bit higher than the smeltery as the structure uses more blocks, balances out in larger structures */
  private static final int BLOCKS_PER_FUEL = 18;

  public FoundryTileEntity() {
    super(TinkerSmeltery.foundry.get(), TConstruct.makeTranslation("gui", "foundry"));
  }

  @Override
  protected HeatingStructureMultiblock<?> createMultiblock() {
    return new FoundryMultiblock(this);
  }


  @Override
  protected MeltingModuleInventory createMeltingInventory() {
    return new ByproductMeltingModuleInventory(this, tank, Config.COMMON.foundryNuggetsPerOre::get);
  }

  @Override
  protected void heat() {
    if (structure == null || world == null) {
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
              if (meltingInventory.canHeat(fuelModule.findFuel(false))) {
                fuelModule.findFuel(true);
              }
            }
          }
          break;
        // second tick: melt items
        case 1:
          if (fuelModule.hasFuel()) {
            meltingInventory.heatItems(fuelModule.getTemperature());
          } else {
            meltingInventory.coolItems();
          }
          break;
        // fourth tick: consume fuel, update fluids
        case 3: {
          // update the active state
          boolean hasFuel = fuelModule.hasFuel();
          BlockState state = getBlockState();
          if (state.get(ControllerBlock.ACTIVE) != hasFuel) {
            world.setBlockState(pos, state.with(ControllerBlock.ACTIVE, hasFuel));
          }
          fuelModule.decreaseFuel(fuelRate);
          break;
        }
      }
    }
  }

  @Override
  protected void setStructure(@Nullable StructureData structure) {
    super.setStructure(structure);
    if (structure != null) {
      int dx = structure.getInnerX(), dy = structure.getInnerY(), dz = structure.getInnerZ();
      // tank capacity includes walls and floor
      tank.setCapacity(CAPACITY_PER_BLOCK * (dx + 2) * (dy + 1) * (dz + 2));
      // item capacity uses just inner space
      meltingInventory.resize(dx * dy * dz, dropItem);
      // fuel rate: every 20 blocks in the wall makes the fuel cost 1 more
      // perimeter: to prevent double counting, frame just added on X and floor
      fuelRate = 1 + (2 * ((dx+2) * dy) + 2 * (dy * dz) + ((dx+2) * (dz+2))) / BLOCKS_PER_FUEL;
    }
  }
}
