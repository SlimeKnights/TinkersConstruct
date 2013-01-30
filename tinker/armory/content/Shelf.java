package tinker.armory.content;

import java.util.List;

import tinker.armory.client.RenderShelf;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Shelf extends BlockContainer
{
	public Shelf(int id, Material material) 
	{
		super(id, material);
	}

	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    public int getRenderType()
    {
        return RenderShelf.shelfModelID;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
    	return new ToolrackLogic();
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int meta)
    {
    	return 6;
    }
    
    public void addCollidingBlockToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7)
    {
        this.setBounds(var1.getBlockMetadata(var2, var3, var4));
        super.addCollidingBlockToList(var1, var2, var3, var4, var5, var6, var7);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4)
    {
        this.setBounds(var1.getBlockMetadata(var2, var3, var4));
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int size, float hitX, float hitY, float hitZ)
    {
    	System.out.println("Activating tool rack");
        if (world.isRemote)
        {
        	System.out.println("World's remote");
            return true;
        }
        else
        {
            ToolrackLogic logic = (ToolrackLogic)world.getBlockTileEntity(x, y, z);

            //if (logic != null && player instanceof EntityPlayerMP)
            if (logic != null)
            {
            	System.out.println("Logic exists");
                int facing = world.getBlockMetadata(x, y, z) / 4;
                byte slot = 0;
                double var13 = (double)x + 0.5D - player.posX;
                double var15 = (double)z + 0.5D - player.posZ;

                if (facing == 0)
                {
                    var15 -= 0.25D;
                }
                else if (facing == 1)
                {
                    var13 -= 0.25D;
                }
                else if (facing == 2)
                {
                    var15 += 0.25D;
                }
                else if (facing == 3)
                {
                    var13 += 0.25D;
                }

                double var17 = var13 + var15 * Math.tan(Math.toRadians((double)player.rotationYaw));
                double var19 = var15 - var13 * Math.tan(Math.toRadians((double)(player.rotationYaw + 90.0F)));

                if (facing == 0 && var17 < 0.0D || facing == 1 && var19 > 0.0D || facing == 2 && var17 > 0.0D || facing == 3 && var19 < 0.0D)
                {
                    slot = 1;
                }

                ItemStack stack = player.getCurrentEquippedItem();

                if (!logic.isItemInColumn(slot) && stack != null && logic.canHoldItem(stack))
                {
                	//logic.setInventorySlotContents(slot, stack);
                    stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    logic.setInventorySlotContents(slot, stack);
                }
                else
                {
                    ItemStack insideStack = logic.takeItemInColumn(slot);

                    if (insideStack == null)
                    {
                        insideStack = logic.takeItemInColumn(1 - slot);
                    }

                    if (insideStack != null)
                    {
                        this.spawnItem(world, x, y, z, insideStack);
                    }
                }

                world.markBlockForUpdate(x, y, z);
            }

            return true;
        }
    }
    
    protected void spawnItem(World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityItem var6 = new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, stack);
            var6.delayBeforeCanPickup = 5;
            int var7 = world.getBlockMetadata(x, y, z) & 3;
            var6.motionX = var6.motionY = var6.motionZ = 0.0D;

            if (var7 == 0)
            {
                var6.motionZ = 0.05D;
            }
            else if (var7 == 1)
            {
                var6.motionX = 0.05D;
            }
            else if (var7 == 2)
            {
                var6.motionZ = -0.05D;
            }
            else if (var7 == 3)
            {
                var6.motionX = -0.05D;
            }

            world.spawnEntityInWorld(var6);
        }
    }
    
    public void setBounds(int metadata)
    {
        int facing = metadata / 4;

        if (facing == 0)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.875F, 0.125F);
        }
        /*else if (facing == 1)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.0F, 0.25F, 0.9F, 1.0F);
        }
        else if (facing == 2)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.75F, 1.0F, 0.9F, 1.0F);
        }
        else if (facing == 3)
        {
            this.setBlockBounds(0.75F, 0.2F, 0.0F, 1.0F, 0.9F, 1.0F);
        }*/
    }
}
