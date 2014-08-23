package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class FletchingMaterial extends CustomMaterial
{
    public final float accuracy;
    public final float breakChance;
    public final float mass;

    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        super(materialID, value, input, craftingItem);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.mass = mass;
    }

    public FletchingMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        super(materialID, value, oredict, craftingItem);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.mass = mass;
    }
}
