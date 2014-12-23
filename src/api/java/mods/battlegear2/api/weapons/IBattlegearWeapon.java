package mods.battlegear2.api.weapons;

import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandDual;
import mods.battlegear2.api.ISheathed;

/**
 * A generic flag for weapon
 * <strong>Not</strong> necessary for an item to be wielded in battlegear slots
 */
public interface IBattlegearWeapon extends ISheathed,IOffhandDual,IAllowItem{

}