package tconstruct.plugins.minefactoryreloaded;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class Drinkables implements ILiquidDrinkHandler
{

    @Override
    public void onDrink (EntityPlayer player)
    {
        //boost health when drink
        if (player != null)
        {
            PotionEffect potion = player.getActivePotionEffect(Potion.field_76434_w);
            int duration = 0;
            if (potion != null)
                duration = potion.duration;
            player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, duration + 20 * 30, 0));
        }

    }

}
