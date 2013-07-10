package mods.tinker.tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class FletchingMaterial extends CustomMaterial
{
    public final float accuracy;
    public final float breakChance;
    public FletchingMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float accuracy, float breakChance)
    {
        super(materialID, value, input, craftingItem);
        this.accuracy = accuracy;
        this.breakChance = breakChance;
    }
}
