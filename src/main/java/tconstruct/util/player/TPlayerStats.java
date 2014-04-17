package tconstruct.util.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import tconstruct.common.TFoodStats;

public class TPlayerStats
{
    public WeakReference<EntityPlayer> player;
    
    public int level;
    public int bonusHealth;
    public int bonusHealthClient;
    public int damage;
    public int hunger;
    public int previousDimension;
    public int mineSpeed;
    
    public boolean climbWalls;
    private int maxHunger;
    
    public boolean beginnerManual;
    public boolean materialManual;
    public boolean smelteryManual;
    public boolean battlesignBonus;
    
    public ArmorExtended armor;
    public KnapsackInventory knapsack;
    
    public void setMaxHunger(int h)
    {
        maxHunger = h;
        ((TFoodStats) player.get().foodStats).setMaxFoodLevel(h);
    }
}
