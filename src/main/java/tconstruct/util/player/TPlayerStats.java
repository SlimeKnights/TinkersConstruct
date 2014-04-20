package tconstruct.util.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;

public class TPlayerStats
{
    public WeakReference<EntityPlayer> player;
    
    public int level;
    public int bonusHealth;
    public int damage;
    public int hunger;
    public int previousDimension;
    public int mineSpeed;
    
    public boolean climbWalls;
    public boolean activeGoggles = true;
    
    public boolean beginnerManual;
    public boolean materialManual;
    public boolean smelteryManual;
    public boolean battlesignBonus;
    
    public ArmorExtended armor;
    public KnapsackInventory knapsack;
}
