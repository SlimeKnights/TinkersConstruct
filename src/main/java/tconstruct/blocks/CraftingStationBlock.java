package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.client.block.TableRender;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.abstracts.InventoryBlock;

public class CraftingStationBlock extends InventoryBlock
{
    public CraftingStationBlock(Material material)
    {
        super(material);
        this.func_149647_a(TConstructRegistry.blockTab);
        this.func_149711_c(2f);
        this.field_149762_H = Block.field_149766_f;
    }

    //Block.hasComparatorInputOverride and Block.getComparatorInputOverride

    /* Rendering */
    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "craftingstation_top", "craftingstation_side", "craftingstation_bottom" };

        return textureNames;
    }

    @Override
    public IIcon func_149691_a (int side, int meta)
    {
        return icons[meta * 3 + getTextureIndex(side)];
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
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public int func_149645_b ()
    {
        return TableRender.tabelModelID;
    }

    @Override
    public boolean func_149646_a (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return TProxyCommon.craftingStationID;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public void func_149666_a (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 1; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

	@Override
	public TileEntity func_149915_a(World arg0, int arg1) {
		return new CraftingStationLogic();
	}
}
