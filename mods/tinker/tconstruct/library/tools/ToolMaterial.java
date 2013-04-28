package mods.tinker.tconstruct.library.tools;
/**
 * This class is temporary. A full material registry is planned
 */
public class ToolMaterial
{    
    //mining level, durability, mining speed, baseDamage, handle modifier, Reinforced level, shoddy/spiny level, color/style on name
    
	public final String materialName;
	public final int craftingTier; //Not used
	public final int harvestLevel;
	public final int durability;
	public final int miningspeed; // <-- divided by 100
	public final int attack;
	public final float handleModifier;
	public final int reinforced;
	public final float shoddy;
	public final String tipStyle;
	public final String ability;
	
	public ToolMaterial(String name, int tier, int level, int durability, int speed, int damage, float handle, int reinforced, float shoddy)
    {
    	this(name, reinforced, reinforced, reinforced, reinforced, reinforced, shoddy, reinforced, shoddy, "", "");
    }

    public ToolMaterial(String name, int tier, int level, int durability, int speed, int damage, float handle, int reinforced, float shoddy, String style, String ability)
    {
    	this.materialName = name;
    	this.craftingTier = tier;
        this.harvestLevel = level;
        this.durability = durability;
        this.miningspeed = speed;
        this.attack = damage;
        this.handleModifier = handle;
        this.reinforced = reinforced;
        this.shoddy = shoddy;
        this.tipStyle = style;
        this.ability = ability;
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
    
    public String style()
    {
    	return this.tipStyle;
    }
    
    public String ability()
    {
    	return this.ability;
    }
}
