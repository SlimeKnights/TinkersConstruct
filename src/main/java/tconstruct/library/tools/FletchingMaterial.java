package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class FletchingMaterial extends CustomMaterial
{
    public final float accuracy;
    public final float breakChance;
    public final float mass;
    public final float durabilityModifier;

    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance, float mass, float durabilityModifier, int color)
    {
        super(materialID, value, input, craftingItem, color);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.mass = mass;
        this.durabilityModifier = durabilityModifier;
    }

    @Deprecated
    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        this(materialID, value, input, craftingItem, accuracy, breakChance, mass, 1f, 0xffffffff);
    }

    public FletchingMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float mass, float durabilityModifier, int color)
    {
        super(materialID, value, oredict, craftingItem, color);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.mass = mass;
        this.durabilityModifier = durabilityModifier;
    }

    @Deprecated
    public FletchingMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        this(materialID, value, oredict, craftingItem, accuracy, breakChance, mass, 1f, 0xffffffff);
    }
}
