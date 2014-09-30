package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class BowstringMaterial extends CustomMaterial
{
    public final float durabilityModifier;
    public final float drawspeedModifier;
    public final float flightSpeedModifier;

    public BowstringMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float durability, float drawspeed, float flightspeed, int color)
    {
        super(materialID, value, input, craftingItem, color);
        this.durabilityModifier = durability;
        this.drawspeedModifier = drawspeed;
        this.flightSpeedModifier = flightspeed;
    }

    @Deprecated
    public BowstringMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float durability, float drawspeed, float flightspeed)
    {
        super(materialID, value, input, craftingItem);
        this.durabilityModifier = durability;
        this.drawspeedModifier = drawspeed;
        this.flightSpeedModifier = flightspeed;
    }
}
