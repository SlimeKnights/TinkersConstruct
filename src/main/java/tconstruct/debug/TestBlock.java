package tconstruct.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TestBlock extends Block {
  public static final PropertyBool up = PropertyBool.create("up");
  public static final PropertyBool down = PropertyBool.create("down");
  public static final PropertyBool north = PropertyBool.create("north");
  public static final PropertyBool east = PropertyBool.create("east");
  public static final PropertyBool south = PropertyBool.create("south");
  public static final PropertyBool west = PropertyBool.create("west");

  public TestBlock() {
    super(Material.rock);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, up, down, north, east, south, west);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    if(worldIn.getBlockState(pos.up()).getBlock() == this)
      state = state.withProperty(up, true);
    if(worldIn.getBlockState(pos.down()).getBlock() == this)
      state = state.withProperty(down, true);
    if(worldIn.getBlockState(pos.east()).getBlock() == this)
      state = state.withProperty(east, true);
    if(worldIn.getBlockState(pos.west()).getBlock() == this)
      state = state.withProperty(west, true);
    if(worldIn.getBlockState(pos.south()).getBlock() == this)
      state = state.withProperty(south, true);
    if(worldIn.getBlockState(pos.north()).getBlock() == this)
      state = state.withProperty(north, true);

    return state;
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                  EntityPlayer playerIn, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
    if(!worldIn.isRemote) {
      switch (side) {
        case UP:
          worldIn.setBlockState(pos, state.cycleProperty(up));
          break;
        case DOWN:
          worldIn.setBlockState(pos, state.cycleProperty(down));
          break;
        case NORTH:
          worldIn.setBlockState(pos, state.cycleProperty(north));
          break;
        case EAST:
          worldIn.setBlockState(pos, state.cycleProperty(east));
          break;
        case SOUTH:
          worldIn.setBlockState(pos, state.cycleProperty(south));
          break;
        case WEST:
          worldIn.setBlockState(pos, state.cycleProperty(west));
          break;
      }

      return true;
    }
    return false;
  }
}
