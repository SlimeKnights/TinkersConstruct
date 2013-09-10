package mods.battlegear2.api.weapons;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IBattlegearWeapon {

    /**
     * Returns true if the weapon will allow other weapons to be placed in the partner offhand slot
     */
    public boolean willAllowOffhandWeapon();

    /**
     * Will allow shield
     */
    public boolean willAllowShield();

    /**
     * Returns true if the weapon be dual wielded in the offhand slot
     */
    public boolean isOffhandHandDualWeapon();

    /**
     * Returns true if the weapon should always be sheathed on the back, false if it should be sheathed on the hip
     */
    public boolean sheatheOnBack();

    /**
     * Perform any function when the item is held in the offhand and the user right clicks an entity.
     * This is generally used to attack an entity with the offhand item. If this is the case the event should
     * be canceled to prevent any default right clicking events (Eg Villager Trading)
     *
     * @param event        the OffhandAttackEvent that was generated
     * @param mainhandItem the ItemStack currently being held in the right hand
     * @param offhandItem  the ItemStack currently being held in the left hand
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandAttackEntity(OffhandAttackEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when the item is held in the offhand and the user right clicks "Air".
     *
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the ItemStack currently being held in the right hand
     * @param offhandItem  the ItemStack currently being held in the left hand
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandClickAir(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when the item is held in the offhand and the user right clicks a block.
     * Note that this will happen prior to the activation of any activation functions of blocks
     *
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the ItemStack currently being held in the right hand
     * @param offhandItem  the ItemStack currently being held in the left hand
     */
    public boolean offhandClickBlock(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any passive effects on each game tick when the item is held in the offhand
     *
     * @param effectiveSide the effective side the method was called from
     * @param mainhandItem  the ItemStack currently being held in the right hand
     * @param offhandItem   the ItemStack currently being held in the left hand
     */
    public void performPassiveEffects(Side effectiveSide, ItemStack mainhandItem, ItemStack offhandItem);

}