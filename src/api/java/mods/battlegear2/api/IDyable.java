package mods.battlegear2.api;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

//This is a tempory fix until we get the heradry system up and running
public interface IDyable {

    /**
     * Return whether the specified ItemStack has a color.
     */
    public boolean hasColor(ItemStack par1ItemStack);


    /**
     * Return the color for the specified ItemStack.
     */
    public int getColor(ItemStack par1ItemStack);

    public void setColor(ItemStack dyable, int rgb);

    /**
     * Remove the color from the specified ItemStack.
     */
    public void removeColor(ItemStack par1ItemStack);

    public int getDefaultColor(ItemStack par1ItemStack);


}
