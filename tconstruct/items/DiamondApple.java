package tconstruct.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DiamondApple extends ItemFood
{
    public Icon[] icons;
    public String[] textureNames = new String[] { "apple_diamond" };
    public String[] itemNames = new String[] { "apple.diamond" };

    public DiamondApple(int id)
    {
        super(id, 4, 2.0F, false);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setAlwaysEdible();
    }

    protected void onFoodEaten (ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            int duration = 0;
            PotionEffect potion;

            potion = player.getActivePotionEffect(Potion.resistance);
            if (potion != null)
                duration = potion.duration;
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, duration + 60 * 20, 0));

            potion = player.getActivePotionEffect(Potion.digSpeed);
            if (potion != null)
                duration = potion.duration;
            player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, duration + 60 * 20, 0));

            potion = player.getActivePotionEffect(Potion.damageBoost);
            if (potion != null)
                duration = potion.duration;
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, duration + 60 * 20, 0));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIconFromDamage (int meta)
    {
        return icons[meta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

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
