package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.blocks.logic.ToolForgeLogic;
import mods.tinker.tconstruct.client.block.TableForgeRender;
import mods.tinker.tconstruct.client.block.TableRender;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.blocks.InventoryBlock;
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

public class ToolForgeBlock extends InventoryBlock
{
    public ToolForgeBlock(int id, Material material)
    {
        super(id, material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(2f);
        this.setStepSound(Block.soundWoodFootstep);
    }

    //Block.hasComparatorInputOverride and Block.getComparatorInputOverride

    String[] textureNames = { "toolforge_iron", "toolforge_gold", "toolforge_diamond", "toolforge_emerald", "toolforge_cobalt", "toolforge_ardite", "toolforge_manyullyn", "toolforge_copper",
            "toolforge_bronze", "toolforge_tin", "toolforge_aluminum", "toolforge_alubrass", "toolforge_alumite", "toolforge_steel" };

    /* Rendering */
    @Override
    public String[] getTextureNames ()
    {
        return textureNames;
    }

    Icon textureTop;

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        textureTop = iconRegister.registerIcon("tinker:toolforge_top");
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (side == 1)
        {
            return textureTop;
        }
        if (side == 0)
        {
            switch (meta)
            {
            case 0: return Block.blockIron.getIcon(side, 0);
            case 1: return Block.blockGold.getIcon(side, 0);
            case 2: return Block.blockDiamond.getIcon(side, 0);
            case 3: return Block.blockEmerald.getIcon(side, 0);
            default: return TContent.metalBlock.getIcon(side, meta - 4);
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
    public int getRenderType ()
    {
        return TableForgeRender.model;
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {

        return new ToolForgeLogic();
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int md = world.getBlockMetadata(x, y, z);
        return TProxyCommon.toolForge;
        //return -1;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
