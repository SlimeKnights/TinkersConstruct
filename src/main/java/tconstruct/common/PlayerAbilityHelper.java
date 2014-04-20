package tconstruct.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tconstruct.TConstruct;
import tconstruct.util.player.TPlayerStats;

public class PlayerAbilityHelper
{

    public static void toggleGoggles (EntityPlayer player)
    {
        TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
        System.out.println("Toggling goggle overlay to " + !stats.activeGoggles);
        stats.activeGoggles = !stats.activeGoggles;
        if (!stats.activeGoggles)
        {
            player.removePotionEffect(Potion.nightVision.id);
        }
        else
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
        }
    }

}
