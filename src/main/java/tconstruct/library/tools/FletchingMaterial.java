package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class FletchingMaterial extends CustomMaterial
{
    public final float accuracy;
    public final float breakChance;
    public final float durabilityModifier;

    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance, float duribalityModifier, int color)
    {
        super(materialID, value, input, craftingItem, color);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.durabilityModifier = duribalityModifier;
    }

    @Deprecated
    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        this(materialID, value, input, craftingItem, accuracy, 0.01f, 1f, 0xffffffff);
    }

    public FletchingMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float duribalityModifier, int color)
    {
        super(materialID, value, oredict, craftingItem, color);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
        this.durabilityModifier = duribalityModifier;
    }

    @Deprecated
    public FletchingMaterial(int materialID, int value, String oredict, ItemStack craftingItem, float accuracy, float breakChance, float mass)
    {
        this(materialID, value, oredict, craftingItem, accuracy, 0.01f, 1f, 0xffffffff);
    }
}
