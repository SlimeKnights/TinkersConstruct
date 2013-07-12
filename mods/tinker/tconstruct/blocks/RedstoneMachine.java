package mods.tinker.tconstruct.blocks;

import static net.minecraftforge.common.ForgeDirection.UP;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.DrawbridgeLogic;
import mods.tinker.tconstruct.blocks.logic.FirestarterLogic;
import mods.tinker.tconstruct.client.block.MachineRender;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import mods.tinker.tconstruct.library.util.IActiveLogic;
import mods.tinker.tconstruct.library.util.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class RedstoneMachine extends InventoryBlock
{
    public RedstoneMachine(int id)
    {
        super(id, Material.iron);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(12);
        setStepSound(soundMetalFootstep);
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new DrawbridgeLogic();
        case 1:
            return new FirestarterLogic();
        default:
            return null;
        }
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return TConstruct.proxy.drawbridgeGui;
        }
        return null;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    /* Rendering */

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "drawbridge_top", "drawbridge_side", "drawbridge_bottom", "drawbridge_top_face", "drawbridge_side_face", "drawbridge_bottom_face", "firestarter_top",
                "firestarter_side", "firestarter_bottom" };

        return textureNames;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/" + textureNames[i]);
        }
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta == 0)
        {
            if (side == 5)
                return icons[5];
            return icons[getTextureIndex(side)];
        }
        if (meta == 1)
        {
            return icons[getTextureIndex(side) + 6];
        }
        return icons[0];
    }

    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            DrawbridgeLogic drawbridge = (DrawbridgeLogic) logic;
            ItemStack stack = drawbridge.getStackInSlot(1);
            if (stack != null && stack.itemID < 4096)
            {
                Block block = Block.blocksList[stack.itemID];
                if (block != null && block.renderAsNormalBlock())
                    return block.getIcon(side, stack.getItemDamage());
            }
            if (side == direction)
            {
                return icons[getTextureIndex(side) + 3];
            }
            else
            {
                return icons[getTextureIndex(side)];
            }
        }

        if (meta == 1)
        {
            if (side == direction)
            {
                return icons[6];
            }
            else if (side / 2 == direction / 2)
            {
                return icons[8];
            }
            return icons[7];
        }
        return icons[0];
    }

    public int getTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
    }

    public int getRenderType ()
    {
        return MachineRender.model;
    }

    public boolean isFireSource (World world, int x, int y, int z, int metadata, ForgeDirection side)
    {
        if (metadata == 1)
            return side == UP;
        return false;
    }

    /*@Override
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
    }*/

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 2; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Redstone */
    public boolean canConnectRedstone (IBlockAccess world, int x, int y, int z, int side)
    {
        return false;
    }

    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborBlockID)
    {
        IActiveLogic logic = (IActiveLogic) world.getBlockTileEntity(x, y, z);
        logic.setActive(world.isBlockIndirectlyGettingPowered(x, y, z));
    }
}
