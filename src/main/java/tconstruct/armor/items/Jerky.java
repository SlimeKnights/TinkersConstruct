package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.world.items.SpecialFood;

public class Jerky extends SpecialFood
{
    public static String[] textureNames = new String[] { "beef", "chicken", "pork", "mutton", "fish", "monster", "blueslime", "blood" };
    public static String[] itemNames = new String[] { "jerky.beef", "jerky.chicken", "jerky.pig", "jerky.sheep", "jerky.fish", "jerky.zombie", "jerky.blueslime", "jerky.blood" };
    public static int[] hunger = new int[] { 8, 6, 8, 6, 5, 4, 6, 4 };
    public static float[] saturation = new float[] { 1.0f, 0.8f, 1.0f, 0.8f, 0.8f, 0.4f, 1.0f, 0.4f };
    public static int[] overhaulHunger = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };

    public Jerky(boolean hungerOverhaul)
    {
        super(hungerOverhaul ? overhaulHunger : hunger, saturation, itemNames, textureNames);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:food/jerky_" + iconNames[i]);
        }
    }

    @Override
    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
        return "item.tconstruct." + unlocalizedNames[arr];
    }

    @Override
    protected void onFoodEaten (ItemStack stack, World world, EntityPlayer player)
    {
        if (stack.getItemDamage() == 7)
        {
            int duration = 0;
            PotionEffect potion = player.getActivePotionEffect(Potion.field_76434_w);
            if (potion != null)
                duration = potion.getDuration();
            player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, duration + 60 * 60, 1));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int type = stack.getItemDamage();
        switch (type)
        {
        case 6:
            list.add("\u00a7b" + StatCollector.translateToLocal("strangefood5.tooltip"));
            break;
        case 7:
            list.add("\u00a74" + StatCollector.translateToLocal("strangefood6.tooltip"));
            break;
        }
    }
}
