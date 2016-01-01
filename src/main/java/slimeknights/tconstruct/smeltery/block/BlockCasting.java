package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.block.BlockInventory;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class BlockCasting extends BlockInventory {

  public BlockCasting() {
    super(Material.rock);
    setHardness(3F);
    setResistance(20F);
    setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileCastingTable();
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    // no gui
    return false;
  }
}
