package tconstruct.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

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

  @Override
  public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    // we pull up a few calls to this point in time because we still have the TE here
    // the execution otherwise is equivalent to vanilla order
    IBlockState state = world.getBlockState(pos);
    this.onBlockDestroyedByPlayer(world, pos, state);
    if(willHarvest) {
      this.harvestBlock(world, player, pos, state, world.getTileEntity(pos));
    }


    world.setBlockToAir(pos);
    // return false to prevent the above called functions to be called again
    // side effect of this is that no xp will be dropped. but it shoudln't anyway from a table :P
    return false;
  }

  @Override
  // save the block data from the table to the item on drop. Only works because of removedByPlayer fix above :I
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    List<ItemStack> items = super.getDrops(world, pos, state, fortune);

    // get block data from the block
    TileEntity te = world.getTileEntity(pos);
    if(te != null && te instanceof TileTable) {
      NBTTagCompound data = ((TileTable) te).getTextureBlock();

      for(ItemStack item : items) {
        // save the data from the block onto the item
        if(item.getItem() == Item.getItemFromBlock(this)) {
          NBTTagCompound tag = new NBTTagCompound();
          tag.setTag(TileTable.FEET_TAG, data);
          item.setTagCompound(tag);
        }
      }
    }

    return items;
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
    List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
    if(drops.size() > 0) {
      return drops.get(0);
    }

    return super.getPickBlock(target, world, pos, player);
  }

  public static ItemStack createItemstackWithBlock(BlockTable table, int tableMeta, Block block, int blockMeta) {
    ItemStack stack = new ItemStack(table, 1, tableMeta);

    if(block != null) {
      ItemStack blockStack = new ItemStack(block, 1, blockMeta);
      NBTTagCompound tag = new NBTTagCompound();
      NBTTagCompound subTag = new NBTTagCompound();
      blockStack.writeToNBT(subTag);
      tag.setTag(TileTable.FEET_TAG, subTag);
      stack.setTagCompound(tag);
    }

    return stack;
  }
}
