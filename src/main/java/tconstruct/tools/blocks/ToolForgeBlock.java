package tconstruct.tools.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.blocks.abstracts.InventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.model.TableRender;
import tconstruct.world.TinkerWorld;

public class ToolForgeBlock extends InventoryBlock
{

    public ToolForgeBlock(Material material)
    {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.setStepSound(Block.soundTypeMetal);
    }

    String[] textureNames = { "toolforge_iron", "toolforge_gold", "toolforge_diamond", "toolforge_emerald", "toolforge_cobalt", "toolforge_ardite", "toolforge_manyullyn", "toolforge_copper", "toolforge_bronze", "toolforge_tin", "toolforge_aluminum", "toolforge_alubrass", "toolforge_alumite", "toolforge_steel" };

    /* Rendering */
    @Override
    public String[] getTextureNames ()
    {
        return textureNames;
    }

    @Override
    public String getTextureDomain (int textureNameIndex)
    {
        return "tinker";
    }

    IIcon textureTop;

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        super.registerBlockIcons(iconRegister);
        textureTop = iconRegister.registerIcon("tinker:toolforge_top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        if (side == 1)
        {
            return textureTop;
        }
        if (side == 0)
        {
            switch (meta)
            {
            case 0:
                return Blocks.iron_block.getIcon(side, 0);
            case 1:
                return Blocks.gold_block.getIcon(side, 0);
            case 2:
                return Blocks.diamond_block.getIcon(side, 0);
            case 3:
                return Blocks.emerald_block.getIcon(side, 0);
            default:
                return TinkerWorld.metalBlock.getIcon(side, meta - 4);
            }
        }

        return this.icons[meta];
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
    public boolean isSideSolid (IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public int getRenderType ()
    {
        return TableRender.model;
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int metadata)
    {
        return new ToolForgeLogic();
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int md = world.getBlockMetadata(x, y, z);
        return ToolProxyCommon.toolForgeID;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
