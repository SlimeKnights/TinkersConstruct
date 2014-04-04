package tconstruct.blocks;

import java.util.List;

import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SearedSlab extends SlabBase
{
    public SearedSlab()
    {
        super(Material.rock);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(12F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        meta = meta % 8;
        if (meta == 0)
            return TRepo.smeltery.getIcon(side, 2);
        if (meta <= 3)
            return TRepo.smeltery.getIcon(side, meta + 3);

        return TRepo.smeltery.getIcon(side, meta + 4);
    }

    @Override
    public void getSubBlocks (Item id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 8; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}