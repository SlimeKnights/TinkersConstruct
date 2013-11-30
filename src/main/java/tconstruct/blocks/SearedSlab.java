package tconstruct.blocks;

import java.util.List;

import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SearedSlab extends SlabBase
{
    public SearedSlab(int id)
    {
        super(id, Material.rock);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(12F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        meta = meta % 8;
        if (meta == 0)
            return TRepo.smeltery.getIcon(side, 2);
        if (meta <= 3)
            return TRepo.smeltery.getIcon(side, meta + 3);

        return TRepo.smeltery.getIcon(side, meta + 4);
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 8; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
