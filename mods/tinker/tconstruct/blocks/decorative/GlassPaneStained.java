package mods.tinker.tconstruct.blocks.decorative;

import java.util.List;

import mods.tinker.tconstruct.blocks.PaneBase;
import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GlassPaneStained extends PaneBase
{
    static String blockTextures[] = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    public GlassPaneStained(int id)
    {
        super(id, Material.glass, "glass/", assembleBlockTextures());
        this.setHardness(0.3F);
        this.setStepSound(soundGlassFootstep);
        this.setUnlocalizedName("tconstruct.glasspanestained");
        this.setCreativeTab(TConstructRegistry.blockTab);
    }

    private static String[] assembleBlockTextures ()
    {
        String[] textures = new String[blockTextures.length];
        for (int i = 0; i < blockTextures.length; i++)
        {
            textures[i] = "stainedglass_" + blockTextures[i];
        }
        return textures;
    }

    @Override
    public int getRenderBlockPass ()
    {
        return 1;
    }

    @Override
    public int damageDropped (int par1)
    {
        return par1;
    }
}
