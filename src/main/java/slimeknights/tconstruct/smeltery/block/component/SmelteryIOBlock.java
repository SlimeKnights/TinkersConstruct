package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import java.util.function.Supplier;

/**
 * Base class for IO smeltery blocks, orientable and activable
 */
public class SmelteryIOBlock extends OrientableSmelteryBlock {
  public static final BooleanProperty ACTIVE = ControllerBlock.ACTIVE;

  /**
   * Creates a new instance
   * @param properties Properties
   * @param tileEntity Tile entity supplier
   */
  public SmelteryIOBlock(Settings properties, Supplier<? extends SmelteryComponentTileEntity> tileEntity) {
    super(properties, tileEntity);
    this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block,BlockState> builder) {
    super.appendProperties(builder);
    builder.add(ACTIVE);
  }
}
