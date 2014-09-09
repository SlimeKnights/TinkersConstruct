package tconstruct.armor.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.InventoryBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import tconstruct.TConstruct;
import tconstruct.armor.modelblock.DryingRackRender;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;

public class DryingRack extends InventoryBlock
{

    public DryingRack()
    {
        super(Material.wood);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(2.0f);
        stepSound = soundTypeMetal;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int metadata)
    {
        return new DryingRackLogic();
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return null;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public int onBlockPlaced (World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        if (side > 1)
            return side;
        return meta;
    }

    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLiving living, ItemStack stack)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            int l = MathHelper.floor_double((double) (living.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            int direction = l % 2;
            if (direction == 1)
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }
    }

    /* Activation */
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        return activateDryingRack(world, x, y, z, player);
    }

    boolean activateDryingRack (World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            DryingRackLogic logic = (DryingRackLogic) world.getTileEntity(x, y, z);

            if (!logic.isStackInSlot(0))
            {
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null)
                {
                    stack = player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    logic.setInventorySlotContents(0, stack);
                }
            }
            else
            {
                if (logic.isStackInSlot(0))
                {
                    ItemStack decrStack = logic.decrStackSize(0, 1);
                    if (decrStack != null)
                        addItemToInventory(player, world, x, y, z, decrStack);
                }
            }

            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    public void addItemToInventory (EntityPlayer player, World world, int x, int y, int z, ItemStack stack)
    {
        AbilityHelper.spawnItemAtPlayer(player, stack);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        float xMin = 0F;
        float yMin = 0F;
        float zMin = 0F;
        float xMax = 1F;
        float yMax = 1F;
        float zMax = 1F;
        switch (metadata)
        {
        case 0:
            zMin = 0.375F;
            yMax = 0.25F;
            zMax = 0.625F;
            break;
        case 1:
            xMin = 0.375F;
            yMax = 0.25F;
            xMax = 0.625F;
            break;
        case 2:
            zMin = 0.75F;
            yMin = 0.75F;
            break;
        case 3:
            zMax = 0.25F;
            yMin = 0.75F;
            break;
        case 4:
            xMin = 0.75F;
            yMin = 0.75F;
            break;
        case 5:
            xMax = 0.25F;
            yMin = 0.75F;
            break;
        }
        return AxisAlignedBB.getBoundingBox((double) x + xMin, (double) y + yMin, (double) z + zMin, (double) x + xMax, (double) y + yMax, (double) z + zMax);
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        float xMin = 0F;
        float yMin = 0F;
        float zMin = 0F;
        float xMax = 1F;
        float yMax = 1F;
        float zMax = 1F;
        switch (metadata)
        {
        case 0:
            zMin = 0.375F;
            yMax = 0.25F;
            zMax = 0.625F;
            break;
        case 1:
            xMin = 0.375F;
            yMax = 0.25F;
            xMax = 0.625F;
            break;
        case 2:
            zMin = 0.75F;
            yMin = 0.75F;
            break;
        case 3:
            zMax = 0.25F;
            yMin = 0.75F;
            break;
        case 4:
            xMin = 0.75F;
            yMin = 0.75F;
            break;
        case 5:
            xMax = 0.25F;
            yMin = 0.75F;
            break;
        }
        this.setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    @Override
    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    /* Rendering */
    @Override
    public int getRenderType ()
    {
        return DryingRackRender.model;
    }

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "castingtable_top", "castingtable_side", "castingtable_bottom", "faucet", "blockcast_top", "blockcast_side", "blockcast_bottom" };

        return textureNames;
    }

    @Override
    public String getTextureDomain (int textureNameIndex)
    {
        return "tinker";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return Blocks.planks.getIcon(side, 0);
    }

    public int getTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new DryingRackLogic();
    }

    /*
     * @Override public boolean shouldSideBeRendered (IBlockAccess
     * par1IBlockAccess, int par2, int par3, int par4, int par5) { return true;
     * }
     */

    /*
     * @Override public void getSubBlocks (int id, CreativeTabs tab, List list)
     * { for (int iter = 0; iter < 3; iter++) { list.add(new ItemStack(id, 1,
     * iter)); } }
     */

    /*
     * @Override public void setBlockBoundsBasedOnState (IBlockAccess world, int
     * x, int y, int z) { int meta = world.getBlockMetadata(x, y, z); if (meta
     * != 1) { this.setBlockBounds(0, 0, 0, 1, 1, 1); } else { FaucetLogic logic
     * = (FaucetLogic) world.getBlockTileEntity(x, y, z); float xMin = 0.25F;
     * float xMax = 0.75F; float zMin = 0.25F; float zMax = 0.75F;
     * 
     * switch (logic.getRenderDirection()) { case 2: zMin = 0.625F; zMax = 1.0F;
     * break; case 3: zMax = 0.375F; zMin = 0F; break; case 4: xMin = 0.625F;
     * xMax = 1.0F; break; case 5: xMax = 0.375F; xMin = 0F; break; }
     * 
     * this.setBlockBounds(xMin, 0.25F, zMin, xMax, 0.625F, zMax); } }
     */

    /*
     * @Override public AxisAlignedBB getCollisionBoundingBoxFromPool (World
     * world, int x, int y, int z) { int meta = world.getBlockMetadata(x, y, z);
     * if (meta != 1) { return AxisAlignedBB.getBoundingBox(x, y, z, x +
     * 1, y + 1, z + 1); } else { FaucetLogic logic = (FaucetLogic)
     * world.getBlockTileEntity(x, y, z); if (logic != null) { float xMin =
     * 0.25F; float xMax = 0.75F; float zMin = 0.25F; float zMax = 0.75F;
     * 
     * switch (logic.getRenderDirection()) { case 2: zMin = 0.625F; zMax = 1.0F;
     * break; case 3: zMax = 0.375F; zMin = 0F; break; case 4: xMin = 0.625F;
     * xMax = 1.0F; break; case 5: xMax = 0.375F; xMin = 0F; break; }
     * 
     * return AxisAlignedBB.getBoundingBox((double) ((float) x + xMin),
     * (double) y + 0.25, (double) ((float) z + zMin), (double) ((float) x +
     * xMax), (double) y + 0.625, (double) ((float) z + zMax)); } }
     * 
     * return super.getCollisionBoundingBoxFromPool(world, x, y, z); }
     */

    /* Redstone */
}
