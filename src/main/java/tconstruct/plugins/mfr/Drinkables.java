package tconstruct.plugins.mfr;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.*;
import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class Drinkables implements ILiquidDrinkHandler
{

    @Override
    public void onDrink (EntityLivingBase entity)
    {
        //boost health when drink
        if (entity != null)
        {
            PotionEffect potion = entity.getActivePotionEffect(Potion.field_76434_w);
            int duration = 0;
            if (potion != null)
                duration = potion.getDuration();
            entity.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, duration + 20 * 30, 0));
        }

    }

}
