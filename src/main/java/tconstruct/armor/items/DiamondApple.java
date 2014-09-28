package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class DiamondApple extends ItemFood
{
    public IIcon[] icons;
    public String[] textureNames = new String[] { "food/apple_diamond" };
    public String[] itemNames = new String[] { "apple.diamond" };

    public DiamondApple()
    {
        super(4, 2.0F, false);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten (ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            int duration = 0;
            PotionEffect potion;

            potion = player.getActivePotionEffect(Potion.resistance);
            if (potion != null)
                duration = potion.getDuration();
            player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, duration + 60 * 40, 4));

            potion = player.getActivePotionEffect(Potion.resistance);
            if (potion != null)
                duration = potion.getDuration();
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, duration + 60 * 20, 0));

            potion = player.getActivePotionEffect(Potion.digSpeed);
            if (potion != null)
                duration = potion.getDuration();
            player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, duration + 60 * 20, 0));

            potion = player.getActivePotionEffect(Potion.damageBoost);
            if (potion != null)
                duration = potion.getDuration();
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, duration + 60 * 20, 0));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage (int meta)
    {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    /* Name override */
    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        return (new StringBuilder()).append("item.food.").append(itemNames[itemstack.getItemDamage()]).toString();
    }
}
