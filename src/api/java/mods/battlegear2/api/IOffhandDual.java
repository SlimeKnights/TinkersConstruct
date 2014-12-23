package mods.battlegear2.api;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IOffhandDual {
	/**
     * Returns true if this item can be dual wielded in the offhand slot
     * @param off The {@link ItemStack} holding this item
     */
    public boolean isOffhandHandDual(ItemStack off);
    
    /**
     * Perform any function when this item is held in the offhand and the user right clicks an entity.
     * This is generally used to attack an entity with the offhand item.
     * If this is the case the {@link PlayerEventChild.OffhandAttackEvent#parent} field should
     * be canceled (or {@link PlayerEventChild.OffhandAttackEvent#cancelParent} field left at true, to prevent any default right clicking events (Eg Villager Trading)
     *
     * @param event        the OffhandAttackEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandAttackEntity(PlayerEventChild.OffhandAttackEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks "Air".
     * Note: Called first on client-side, then on server side if {@link PlayerInteractEvent} is not cancelled and mainhandItem is not null,
     * following Forge rules for PlayerInteractEvent with Action==RIGHT_CLICK_AIR
     * Note: PlayerInteractEvent is already cancelled beforehand, and will be cancelled after if this method returns false
     * Note: Above issues will be fixed in next Minecraft versions by replacing first arg with a shallow copy
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandClickAir(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks a block.
     * Note: this will happen prior to the activation of any activation functions of blocks
     * Note: Called first on client-side, then on server side if {@link PlayerInteractEvent} is not cancelled
     * Note: {@link PlayerInteractEvent#useItem} is already set on {@link Event.Result#DENY} before reaching this method, in order to avoid mainhandItem usage
     *
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandClickBlock(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    @SuppressWarnings("unused")
    /**
     * Perform any passive effects on each game tick when this item is held in the offhand
     * @deprecated See {@link Item#onUpdate(ItemStack, World, Entity, int, boolean)}
     * @param effectiveSide the effective side the method was called from
     * @param mainhandItem  the {@link ItemStack} currently being held in the right hand
     * @param offhandItem   the {@link ItemStack} currently being held in the left hand
     */
    public void performPassiveEffects(Side effectiveSide, ItemStack mainhandItem, ItemStack offhandItem);
}
