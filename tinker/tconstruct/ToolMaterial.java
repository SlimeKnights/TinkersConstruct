package tinker.tconstruct;
/**
 * This class is temporary. A full material registry is planned
 */
public class ToolMaterial
{
	/*Wood       (0,   59,  200, 0,  1.0F, 0, 0),
    Stone      (1,  131,  400, 1,  0.5F, 0, 1f),
    Iron       (2,  250,  600, 2,  1.3F, 1, 0),
    Flint      (1,  171,  525, 2,  0.7F, 0, 1f),
    Cactus     (1,  150,  500, 2,  1.0F, 0, -1f),
    Bone       (1,  200,  500, 2,  1.0F, 0, 0),
    Obsidian   (3,   89,  700, 2,  0.8F, 3, 0),
    Netherrack (2,  131,  400, 1,  1.2F, 0, 1f),
    Slime      (3, 1500,  150, 0,  5.0F, 0, 0),
    Paper      (0,  131,  200, 0,  0.1F, 0, 0),
    Cobalt     (4,  800,  800, 3,  1.8F, 2, 0),
    Ardite     (4,  800,  800, 3,  1.8F, 0, 0),
    Manyullyn  (5, 1200, 1000, 4,  2.5F, 0, 0),
    Copper     (1,  180,  500, 2,  1.15F, 0, 0),
    Bronze     (2,  250,  600, 2,  1.3F, 1, 0),
    Demonite   (5, 1790,  900, 5, 1.66F, 0, 0),
    Holyshell  (5, 1000,  700, 1,  1.5F, 0, 0);*/
    
    //mining level, durability, mining speed, baseDamage, handle modifier, Reinforced level, shoddy/spiny level
    
	public final String materialName;
	public final int craftingTier;
	public final int harvestLevel;
	public final int durability;
	public final int miningspeed; // <-- divided by 100
	public final int attack;
	public final float handleModifier;
	public final int reinforced;
	public final float shoddy;

    public ToolMaterial(String name, int tier, int level, int dur, int speed, int damage, float handle, int unb, float shd)
    {
    	this.materialName = name;
    	this.craftingTier = tier;
        this.harvestLevel = level;
        this.durability = dur;
        this.miningspeed = speed;
        this.attack = damage;
        this.handleModifier = handle;
        this.reinforced = unb;
        this.shoddy = shd;
    }
    
    public String name()
    {
    	return materialName;
    }
    
    public int tier()
    {
    	return craftingTier;
    }
    
    public int durability()
    {
        return this.durability;
    }
    
    public int toolSpeed()
    {
        return this.miningspeed;
    }
    
    public int attack()
    {
        return this.attack;
    }
    
    public int harvestLevel()
    {
        return this.harvestLevel;
    }
    
    public float handleDurability()
    {
    	return this.handleModifier;
    }
    
    public int reinforced()
    {
        return this.reinforced;
    }
    
    public float shoddy()
    {
    	return this.shoddy;
    }
}
