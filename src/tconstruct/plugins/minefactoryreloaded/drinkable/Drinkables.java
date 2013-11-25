package tconstruct.plugins.minefactoryreloaded.drinkable;

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
            player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 20 * 15, 0));
        }

    }

}
