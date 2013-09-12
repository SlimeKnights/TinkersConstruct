package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
@Deprecated
public interface IArrowContainer{

	/**
	 * 
	 * @param stack The {@link #ItemStack} representing this item
	 * @param bow The bow trying to use this container
	 * @param player The {@link #EntityPlayer} using the bow
	 * @return true if the item contains at least one arrow
	 */
	public boolean hasArrowFor(ItemStack stack, ItemStack bow, EntityPlayer player);
	/**
	 * The arrow spawned when bow is used with this non empty container equipped
	 * @param stack The {@link #ItemStack} representing this item
	 * @param charge Amount of charge in the bow, ranging from 0.2F to 2.0F
	 * @param player The {@link #EntityPlayer} using the bow
	 * @param world 
	 * @return the arrow entity to spawn when bow is used
	 */
	public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge);
	/**
	 * Action to take after an arrow has been fired
	 * Usually equal to removing an arrow from the container
	 * @param player The {@link #EntityPlayer} using the bow
	 * @param world 
	 * @param stack The {@link #ItemStack} representing this item
	 * @param bow The bow which fired
	 * @param arrow the arrow fired
	 */
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow);
	/**
	 * Called before the arrow is fired from this container
	 * @param arrowEvent Used to decide bow damage, bow sound and arrow enchantment
	 */
	public void onPreArrowFired(QuiverArrowEvent arrowEvent);
	/**
	 * Called when the container is put on a crafting bench with vanilla arrows
	 * @param stack
	 * @return True to receive {@link #addArrows(ItemStack, int)}
	 */
	public boolean isCraftableWithArrows(ItemStack stack);
	/**
	 * Crafts the item with vanilla arrows
	 * @param stack
	 * @param arrows Number of vanilla arrows on the crafting bench
	 * @return Number of vanilla arrows that couldn't fit in
	 */
	public int addArrows(ItemStack stack, int arrows);
}
