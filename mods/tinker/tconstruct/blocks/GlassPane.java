package mods.tinker.tconstruct.blocks;

import java.util.List;

import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GlassPane extends PaneBase
{
    static String blockTextures[] = { "glass_clear", "soulglass", "soulglass_clear" };

    public GlassPane(int id)
    {
        super(id, Material.glass, "glass/", blockTextures);
        this.setHardness(0.3F);
        this.setStepSound(soundGlassFootstep);
        this.setUnlocalizedName("tconstruct.glasspane");
        this.setCreativeTab(TConstructRegistry.blockTab);
    }
}
