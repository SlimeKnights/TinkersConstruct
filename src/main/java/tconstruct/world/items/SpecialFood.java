package tconstruct.world.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class SpecialFood extends ItemFood
{
    protected int[] hunger;
    protected float[] saturation;
    protected String[] unlocalizedNames;
    protected String[] iconNames;
    protected IIcon[] icons;

    public SpecialFood(int[] hunger, float[] saturation, String[] textureNames, String[] iconNames)
    {
        super(0, 0, false);
        this.hunger = hunger;
        this.saturation = saturation;
        this.unlocalizedNames = textureNames;
        this.iconNames = iconNames;
    }

    @Override
    public int getHealAmount (ItemStack stack)
    {
        return hunger[stack.getMetadata()];
    }

    @Override
    public float getSaturationModifier (ItemStack stack)
    {
        return saturation[stack.getMetadata()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        if (meta >= icons.length)
            return icons[0];
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + iconNames[i]);
        }
    }

    @Override
    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getMetadata(), 0, unlocalizedNames.length);
        return getUnlocalizedName() + "." + unlocalizedNames[arr];
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < unlocalizedNames.length; i++)
            list.add(new ItemStack(b, 1, i));
    }
}
