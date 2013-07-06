package mods.tinker.tconstruct.library.tools;

import net.minecraft.item.ItemStack;

public class BowstringMaterial
{
    public final int materialID;
    public final ItemStack input;
    public final ItemStack craftingItem;
    public final float durabilityModifier;
    public final float drawspeedModifier;
    public final float flightSpeedModifier;
    
    public BowstringMaterial(int materialID, ItemStack input, ItemStack craftingItem, float durability, float drawspeed, float flightspeed)
    {
        this.materialID = materialID;
        this.input = input;
        this.craftingItem = craftingItem;
        this.durabilityModifier = durability;
        this.drawspeedModifier = drawspeed;
        this.flightSpeedModifier = flightspeed;
    }
}
