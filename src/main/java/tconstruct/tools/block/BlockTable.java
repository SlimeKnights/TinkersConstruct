package tconstruct.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import tconstruct.common.property.PropertyString;
import tconstruct.library.utils.TagUtil;
import tconstruct.tools.tileentity.TileTable;

public class BlockTable extends Block implements ITileEntityProvider {
  public static final PropertyString TEXTURE = new PropertyString("texture");


  public BlockTable(Material materialIn) {
    super(materialIn);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileTable();
  }

  @Override
  protected BlockState createBlockState() {
    return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{TEXTURE});
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    IExtendedBlockState extendedState = (IExtendedBlockState) state;

    TileEntity te = world.getTileEntity(pos);
    if(te != null && te instanceof TileTable) {
      TileTable table = (TileTable) te;
      return table.writeExtendedBlockState(extendedState);
    }

    return super.getExtendedState(state, world, pos);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                              ItemStack stack) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    if(tag.hasKey(TileTable.FEET_TAG)) {
      TileEntity te = world.getTileEntity(pos);
      if(te != null && te instanceof TileTable) {
        ((TileTable) te).updateTextureBlock(tag.getCompoundTag(TileTable.FEET_TAG));
      }
    }
  }

  public static ItemStack createItemstackWithBlock(BlockTable table, int tableMeta, Block block, int blockMeta) {
    ItemStack blockStack = new ItemStack(block, 1, blockMeta);

    ItemStack stack = new ItemStack(table, tableMeta);
    NBTTagCompound tag = new NBTTagCompound();
    NBTTagCompound subTag = new NBTTagCompound();
    blockStack.writeToNBT(subTag);
    tag.setTag(TileTable.FEET_TAG, subTag);
    stack.setTagCompound(tag);

    return stack;
  }
}
