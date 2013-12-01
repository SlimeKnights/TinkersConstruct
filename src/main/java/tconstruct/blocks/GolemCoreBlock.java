package tconstruct.blocks;

import tconstruct.TConstruct;
import tconstruct.blocks.logic.GolemCoreLogic;
import tconstruct.client.block.GolemCoreRender;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.abstracts.InventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class GolemCoreBlock extends InventoryBlock
{
    public GolemCoreBlock(int id)
    {
        super(id, Material.rock);
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    public boolean isOpaqueCube ()
    {
        return false;
    }

    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    public int getRenderType ()
    {
        return GolemCoreRender.model;
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote)
        {
            GolemCoreLogic logic = (GolemCoreLogic) world.getBlockTileEntity(x, y, z);

            if (!logic.isStackInSlot(0))
            {
                ItemStack stack = player.getCurrentEquippedItem();
                stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                logic.setInventorySlotContents(0, stack);
            }
            else
            {
                ItemStack stack = logic.decrStackSize(0, 1);
                if (stack != null)
                    addItemToInventory(player, world, x, y, z, stack);
            }

            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    protected void addItemToInventory (EntityPlayer player, World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote)
        {
            EntityItem entityitem = new EntityItem(world, (double) x + 0.5D, (double) y + 0.9325D, (double) z + 0.5D, stack);
            world.spawnEntityInWorld(entityitem);
            entityitem.onCollideWithPlayer(player);
        }
    }

    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        return icons[0];
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new GolemCoreLogic();
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return null; //Not sure if gui block or not, probably not
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public String[] getTextureNames ()
    {
        return new String[] { "golemcore" };
    }
}
