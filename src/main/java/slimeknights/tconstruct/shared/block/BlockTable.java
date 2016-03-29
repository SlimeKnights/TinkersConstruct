package slimeknights.tconstruct.shared.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.property.PropertyString;
import slimeknights.mantle.property.PropertyUnlistedDirection;
import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class BlockTable extends BlockInventory implements ITileEntityProvider {

  public static final PropertyString TEXTURE = new PropertyString("texture");
  public static final PropertyTableItem INVENTORY = new PropertyTableItem();
  public static final PropertyUnlistedDirection FACING = new PropertyUnlistedDirection("facing", EnumFacing.Plane.HORIZONTAL);


  public BlockTable(Material materialIn) {
    super(materialIn);
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer()
  {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    // table without inventory by default
    return new TileTable("tile.table", 0, 0);
  }

  @Override
  public boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    // no gui by default
    return false;
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{TEXTURE, INVENTORY, FACING});
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
    super.onBlockPlacedBy(world, pos, state, placer, stack);

    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    TileEntity te = world.getTileEntity(pos);
    if(te != null && te instanceof TileTable) {
      TileTable table = (TileTable) te;
      NBTTagCompound feetTag = tag.getCompoundTag(TileTable.FEET_TAG);
      if(feetTag == null) {
        feetTag = new NBTTagCompound();
      }

      table.updateTextureBlock(feetTag);
      table.setFacing(placer.getHorizontalFacing().getOpposite());

      // check if we also have an inventory
      if(tag.hasKey("inventory")) {
        TileInventory.readInventoryFromNBT(table, tag.getCompoundTag("inventory"));
      }
    }
  }

  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    // we pull up a few calls to this point in time because we still have the TE here
    // the execution otherwise is equivalent to vanilla order
    this.onBlockDestroyedByPlayer(world, pos, state);
    if(willHarvest) {
      this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
    }

    // clear the inventory if we kept it on the item
    // otherwise we'd dupe it since it'd also spell when we set the block to air
    if(keepInventory(state)) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileInventory) {
        ((TileInventory) te).clear();
      }
    }

    world.setBlockToAir(pos);
    // return false to prevent the above called functions to be called again
    // side effect of this is that no xp will be dropped. but it shoudln't anyway from a table :P
    return false;
  }

  protected boolean keepInventory(IBlockState state) {
    return false;
  }

  @Override
  // save the block data from the table to the item on drop. Only works because of removedByPlayer fix above :I
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    List<ItemStack> items = super.getDrops(world, pos, state, fortune);

    // get block data from the block
    TileEntity te = world.getTileEntity(pos);
    if(te != null && te instanceof TileTable) {
      TileTable table = (TileTable) te;
      NBTTagCompound data = table.getTextureBlock();

      for(ItemStack item : items) {
        // save the data from the block onto the item
        if(item.getItem() == Item.getItemFromBlock(this)) {
          NBTTagCompound tag = TagUtil.getTagSafe(item);
          tag.setTag(TileTable.FEET_TAG, data);
          item.setTagCompound(tag);

          // save inventory?
          if(keepInventory(state)) {
            NBTTagCompound inventoryTag = new NBTTagCompound();
            TileInventory.writeInventoryToNBT(table, inventoryTag);
            tag.setTag("inventory", inventoryTag);
          }
        }
      }
    }

    return items;
  }

  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
    List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
    if(drops.size() > 0) {
      return drops.get(0);
    }

    return super.getPickBlock(state, target, world, pos, player);
  }

  public static ItemStack createItemstack(BlockTable table, int tableMeta, Block block, int blockMeta) {
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


  /* Bounds */
  private static ImmutableList<AxisAlignedBB> BOUNDS_Table = ImmutableList.of(
      new AxisAlignedBB(0, 0.75, 0, 1, 1, 1),
      new AxisAlignedBB(0,    0, 0,    0.25, 0.75, 0.25),
      new AxisAlignedBB(0.75, 0, 0,    1,    0.75, 0.25),
      new AxisAlignedBB(0.75, 0, 0.75, 1,    0.75, 1),
      new AxisAlignedBB(0,    0, 0.75, 0.25, 0.75, 1)
  );

  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
    // basically the same BlockStairs does
    // Raytrace through all AABBs (plate, legs) and return the nearest one
    return raytraceMultiAABB(BOUNDS_Table, pos, start, end);
  }

  public static RayTraceResult raytraceMultiAABB(List<AxisAlignedBB> aabbs, BlockPos pos, Vec3d start, Vec3d end) {
    List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

    for (AxisAlignedBB axisalignedbb : aabbs)
    {
      list.add(rayTrace2(pos, start, end, axisalignedbb));
    }

    RayTraceResult raytraceresult1 = null;
    double d1 = 0.0D;

    for (RayTraceResult raytraceresult : list)
    {
      if (raytraceresult != null)
      {
        double d0 = raytraceresult.hitVec.squareDistanceTo(end);

        if (d0 > d1)
        {
          raytraceresult1 = raytraceresult;
          d1 = d0;
        }
      }
    }

    return raytraceresult1;
  }

  // Block.raytrace
  private static RayTraceResult rayTrace2(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox)
  {
    Vec3d vec3d = start.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    Vec3d vec3d1 = end.subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
    return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), raytraceresult.sideHit, pos);
  }
}
