package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.InventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.event.SmelteryEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.smeltery.logic.*;
import tconstruct.smeltery.model.CastingBlockRender;

public class SearedBlock extends InventoryBlock
{

    public SearedBlock()
    {
        super(Material.rock);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(3F);
        setResistance(20F);
        setStepSound(soundTypeMetal);
    }

    public SearedBlock(String texture)
    {
        this();
        this.texturePrefix = texture;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new CastingTableLogic();
        case 1:
            return new FaucetLogic();
        case 2:
            return new CastingBasinLogic();
        default:
            return null;
        }
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 0;
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

    /* Activation */
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ)
    {
        int md = world.getBlockMetadata(x, y, z);
        // casting table or basin
        if (md == 0 || md == 2)
        {
            return activateCastingBlock(world, x, y, z, player);
        }
        // faucet
        else if (md == 1)
        {
            if (player.isSneaking())
                return false;

            FaucetLogic logic = (FaucetLogic) world.getTileEntity(x, y, z);
            logic.setActive(true);
            return true;
        }
        else
            return super.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

    boolean activateCastingBlock (World world, int x, int y, int z, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            CastingBlockLogic logic = (CastingBasinLogic) world.getTileEntity(x, y, z);
            logic.interact(player);
            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    /* Rendering */
    @Override
    public int getRenderType ()
    {
        return CastingBlockRender.searedModel;
    }

    String texturePrefix = "";

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "castingtable_top", "castingtable_side", "castingtable_bottom", "faucet", "blockcast_top", "blockcast_side", "blockcast_bottom" };

        if (!texturePrefix.equals(""))
            for (int i = 0; i < textureNames.length; i++)
                textureNames[i] = texturePrefix + "_" + textureNames[i];

        return textureNames;
    }

    @Override
    public String getTextureDomain (int textureNameIndex)
    {
        return "tinker";
    }

    // TODO getIcon
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (meta == 0)
            return icons[getTextureIndex(side)];
        else if (meta == 2)
            return icons[getTextureIndex(side) + 4];
        else
            return icons[3];
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
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 1)
        {
            this.setBlockBounds(0, 0, 0, 1, 1, 1);
        }
        else
        {
            TileEntity te = world.getTileEntity(x, y, z);
            float xMin = 0.25F;
            float xMax = 0.75F;
            float zMin = 0.25F;
            float zMax = 0.75F;

            if (te instanceof FaucetLogic)
            {
                FaucetLogic logic = (FaucetLogic) te;
                switch (logic.getRenderDirection())
                {
                case 2:
                    zMin = 0.625F;
                    zMax = 1.0F;
                    break;
                case 3:
                    zMax = 0.375F;
                    zMin = 0F;
                    break;
                case 4:
                    xMin = 0.625F;
                    xMax = 1.0F;
                    break;
                case 5:
                    xMax = 0.375F;
                    xMin = 0F;
                    break;
                }
            }

            this.setBlockBounds(xMin, 0.25F, zMin, xMax, 0.625F, zMax);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 1)
        {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        }
        else
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof FaucetLogic)
            {
                FaucetLogic logic = (FaucetLogic) tile;
                float xMin = 0.25F;
                float xMax = 0.75F;
                float zMin = 0.25F;
                float zMax = 0.75F;

                switch (logic.getRenderDirection())
                {
                case 2:
                    zMin = 0.625F;
                    zMax = 1.0F;
                    break;
                case 3:
                    zMax = 0.375F;
                    zMin = 0F;
                    break;
                case 4:
                    xMin = 0.625F;
                    xMax = 1.0F;
                    break;
                case 5:
                    xMax = 0.375F;
                    xMin = 0F;
                    break;
                }

                return AxisAlignedBB.getBoundingBox((double) ((float) x + xMin), (double) y + 0.25, (double) ((float) z + zMin), (double) ((float) x + xMax), (double) y + 0.625, (double) ((float) z + zMax));
            }
        }

        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /* Redstone */
    @Override
    public boolean canConnectRedstone (IBlockAccess world, int x, int y, int z, int side)
    {
        return world.getBlockMetadata(x, y, z) == 1;
    }

    private boolean wasPowered = false;

    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, Block neighborBlockID)
    {
        if (world.getBlockMetadata(x, y, z) == 1)
        {
            boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
            if (!wasPowered && isPowered)
            {
                FaucetLogic logic = (FaucetLogic) world.getTileEntity(x, y, z);
                logic.setActive(true);
            }
            wasPowered = isPowered;
        }
    }

    @Override
    public TileEntity createNewTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new CastingTableLogic();
        case 1:
            return new FaucetLogic();
        case 2:
            return new CastingBasinLogic();
        default:
            return null;
        }
    }
}