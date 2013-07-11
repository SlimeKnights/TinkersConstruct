package mods.tinker.tconstruct.blocks;

import java.util.Random;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.FrypanLogic;
import mods.tinker.tconstruct.client.block.FrypanRender;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EquipBlock extends InventoryBlock
{

    public EquipBlock(int id, Material material)
    {
        super(id, material);
        this.setHardness(0.3f);
        this.setBlockBounds(0, 0, 0, 1, 0.25f, 1);
        //this.setCreativeTab(ToolConstruct.materialTab);
    }

    @Override
    public String[] getTextureNames ()
    {
        return new String[] { "toolstation_top" };
    }

    public Icon getIcon (int side, int meta)
    {
        return Block.blockIron.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister par1IconRegister)
    {
        //this.blockIcon = par1iconRegister.registerIcon(Block.blockIron.getUnlocalizedName());
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
    public int getRenderType ()
    {
        return FrypanRender.frypanModelID;
    }

    @Override
    public int idDropped (int par1, Random par2Random, int par3)
    {
        return 0;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        return new FrypanLogic();
    }

    public void randomDisplayTick (World world, int x, int y, int z, Random random)
    {
        if (isActive(world, x, y, z))
        {
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.25F + (random.nextFloat() * 6F) / 16F;
            float f2 = (float) z + 0.5F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            world.spawnParticle("smoke", f, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", f, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
        }
    }

    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        return !isActive(world, x, y, z) ? 0 : 9;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return TConstruct.proxy.frypanGuiID;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }
}
