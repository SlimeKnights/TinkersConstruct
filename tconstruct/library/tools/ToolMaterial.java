package tconstruct.library.tools;

/*
 * Dynamic substitute for an enum. It carries a lot of information
 */
public class ToolMaterial
{
    //mining level, durability, mining speed, baseDamage, handle modifier, Reinforced level, shoddy/spiny level, color/style on name

    public final String materialName;
    public final String displayName;
    public final int harvestLevel;
    public final int durability;
    public final int miningspeed; // <-- divided by 100
    public final int attack;
    public final float handleModifier;
    public final int reinforced;
    public final float stonebound;
    public final String tipStyle;
    public final String ability;

    public ToolMaterial(String name, int level, int durability, int speed, int damage, float handle, int reinforced, float stonebound, String style, String ability)
    {
        this(name, name + " ", level, durability, speed, damage, handle, reinforced, stonebound, style, ability);
    }

    public ToolMaterial(String name, String displayName, int level, int durability, int speed, int damage, float handle, int reinforced, float stonebound, String style, String ability)
    {
        this.materialName = name;
        this.displayName = displayName;
        this.harvestLevel = level;
        this.durability = durability;
        this.miningspeed = speed;
        this.attack = damage;
        this.handleModifier = handle;
        this.reinforced = reinforced;
        this.stonebound = stonebound;
        this.tipStyle = style;
        this.ability = ability;
    }

    public String name ()
    {
        return materialName;
    }

    public int durability ()
    {
        return this.durability;
    }

    public int toolSpeed ()
    {
        return this.miningspeed;
    }

    public int attack ()
    {
        return this.attack;
    }

    public int harvestLevel ()
    {
        return this.harvestLevel;
    }

    public float handleDurability ()
    {
        return this.handleModifier;
    }

    public int reinforced ()
    {
        return this.reinforced;
    }

    public float shoddy ()
    {
        return this.stonebound;
    }

    public String style ()
    {
        return this.tipStyle;
    }

    public String ability ()
    {
        return this.ability;
    }
}
