package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SearedSlab extends SlabBase
{
    public SearedSlab()
    {
        //TODO material.rock
        super(Material.field_151576_e);
        this.func_149647_a(TConstructRegistry.blockTab);
        func_149711_c(12F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a (IIconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a (int side, int meta)
    {
        meta = meta % 8;
        if (meta == 0)
            return TRepo.smeltery.func_149691_a(side, 2);
        if (meta <= 3)
            return TRepo.smeltery.func_149691_a(side, meta + 3);

        return TRepo.smeltery.func_149691_a(side, meta + 4);
    }

    @Override
    public void func_149666_a (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 8; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
