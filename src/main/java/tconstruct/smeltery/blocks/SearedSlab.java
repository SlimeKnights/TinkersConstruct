package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import tconstruct.blocks.SlabBase;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.TinkerSmeltery;

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
            return TinkerSmeltery.smeltery.getIcon(side, 2);
        if (meta <= 3)
            return TinkerSmeltery.smeltery.getIcon(side, meta + 3);

        return TinkerSmeltery.smeltery.getIcon(side, meta + 4);
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