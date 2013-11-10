package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.blocks.logic.TerraformerLogic;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Terraformer extends BlockContainer
{
    public Terraformer(int id)
    {
        super(id, Material.iron);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setStepSound(soundMetalFootstep);
    }

    @Override
    public TileEntity createNewTileEntity (World world)
    {
        return new TerraformerLogic();
    }

    @Override
    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < textureNames.length - 1; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Rendering */

    @SideOnly(Side.CLIENT)
    public Icon[] icons;

    @SideOnly(Side.CLIENT)
    static String[] textureNames = { "crystal_machine_top", "terrafreezer", "terrafumer", "terrawaver", "terraleecher", "terragrower", "terranether", "terralighter", "terracrystal" };

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (int side, int meta)
    {
        if (side == 0 || side == 1)
            return icons[0];
        return icons[meta + 1];
    }
}
