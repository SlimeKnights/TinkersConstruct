package slimeknights.tconstruct.smeltery.tileentity;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.component.TankTileEntity;

import slimeknights.tconstruct.smeltery.tileentity.component.TankTileEntity.ITankBlock;

/** Extension of {@link TankTileEntity} that uses no TESR, forcing the model fluid render, its more efficient for decoration */
public class LanternTileEntity extends TankTileEntity {
  public LanternTileEntity() {
    this(TinkerSmeltery.searedLantern.get());
  }

  /** Main constructor */
  public LanternTileEntity(ITankBlock block) {
    super(TinkerSmeltery.lantern.get(), block);
  }

  @Override
  public boolean isFluidInModel() {
    return true;
  }
}
