package tconstruct.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpecialFood extends ItemFood
{
    int[] hunger;
    float[] saturation;
    String[] unlocalizedNames;
    String[] iconNames;
    Icon[] icons;

    public SpecialFood(int id, int[] hunger, float[] saturation, String[] textureNames, String[] iconNames)
    {
        super(id, 0, 0, false);
        this.hunger = hunger;
        this.saturation = saturation;
        this.unlocalizedNames = textureNames;
        this.iconNames = iconNames;
    }

    @Override
    public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
    {
        stack.stackSize--;
        int damage = stack.getItemDamage();
        player.getFoodStats().addStats(hunger[damage], saturation[damage]);
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        this.onFoodEaten(stack, world, player);
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + iconNames[i]);
        }
    }

    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
        return getUnlocalizedName() + "." + unlocalizedNames[arr];
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < unlocalizedNames.length; i++)
            list.add(new ItemStack(id, 1, i));
    }
}
