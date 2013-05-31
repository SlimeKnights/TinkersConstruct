package powercrystals.minefactoryreloaded.api;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author PowerCrystals
 * 
 * Defines a ranchable entity for use in the Rancher.
 */
public interface IFactoryRanchable
{
	/**
	 * @return The entity being ranched. Must be a subtype of EntityLiving.
	 */
	public Class<?> getRanchableEntity();
	
	/**
	 * @param world The world this entity is in.
	 * @param entity The entity instance being ranched.
	 * @param rancher The rancher instance doing the ranching. Used to access the Rancher's inventory when milking cows, for example.
	 * @return A list of drops.
	 */
	public List<ItemStack> ranch(World world, EntityLiving entity, IInventory rancher);
}
