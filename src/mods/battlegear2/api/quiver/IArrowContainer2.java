package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IArrowContainer2 {

    /**
     * Returns the maximum amout of slots in the arrow container
     * @param container {@link #ItemStack} representing this item
     * @return the amount of slots
     */
    public int getSlotCount(ItemStack container);

    /**
     * Returns the currently selected slot in the arrow container
     * @param container {@link #ItemStack} representing this item
     * @return the currently selected slot
     */
    public int getSelectedSlot(ItemStack container);

    /**
     * Sets the currently selected slot to the given value
     * @param container {@link #ItemStack} representing this item
     * @param newSlot the new slot index
     */
    public void setSelectedSlot(ItemStack container, int newSlot);

    /**
     * Returns the itemStack in the currently selected slot
     * @param container The {@link #ItemStack} representing this item
     * @param slot the slot index
     * @return The {@link #ItemStack} in the given slot.
     */
    public ItemStack getStackInSlot(ItemStack container, int slot);

    /**
     * Sets places the given item stack in the give slot
     * @param container {@link #ItemStack} representing this item
     * @param slot the slot index
     * @param container {@link #ItemStack} representing the new stack
     */
    public void setStackInSlot(ItemStack container, int slot, ItemStack stack);


    /**
     *
     * @param container The {@link #ItemStack} representing this item
     * @param bow The bow trying to use this container
     * @param player The {@link #EntityPlayer} using the bow
     * @return true if the item contains at least one arrow in the selected slot
     */
    public boolean hasArrowFor(ItemStack container, ItemStack bow, EntityPlayer player, int slot);

    /**
     * The arrow spawned when bow is used with this non empty container equipped
     * @param container The {@link #ItemStack} representing this item
     * @param charge Amount of charge in the bow, ranging from 0.2F to 2.0F
     * @param player The {@link #EntityPlayer} using the bow
     * @param world
     * @return the arrow entity to spawn when bow is used
     */
    public EntityArrow getArrowType(ItemStack container, World world, EntityPlayer player, float charge);
    /**
     * Action to take after an arrow has been fired
     * Usually equal to removing an arrow from the container
     * @param player The {@link #EntityPlayer} using the bow
     * @param world
     * @param container The {@link #ItemStack} representing this item
     * @param bow The bow which fired
     * @param arrow the arrow fired
     */
    public void onArrowFired(World world, EntityPlayer player, ItemStack container, ItemStack bow, EntityArrow arrow);
    /**
     * Called before the arrow is fired from this container
     * @param arrowEvent Used to decide bow damage, bow sound and arrow enchantment
     */
    public void onPreArrowFired(QuiverArrowEvent arrowEvent);
    /**
     * Called when the container is put on a crafting bench with other items
     * @param container The {@link #ItemStack} representing this item
     * @param arrowStack The {@link #ItemStack} representing other items
     * @return True to receive {@link #addArrows(ItemStack, ItemStack)}
     */
    public boolean isCraftableWithArrows(ItemStack contaner, ItemStack arrowStack);

    /**
     * Crafts the item with the items from {@link #isCraftableWithArrows(ItemStack, ItemStack)}
     * @param container The {@link #ItemStack} representing this item
     * @param arrows Another valid item on the crafting bench
     * @return Number of arrows that couldn't fit in
     */
    public ItemStack addArrows(ItemStack container, ItemStack newStack);



}
