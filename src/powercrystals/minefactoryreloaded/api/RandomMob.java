package powercrystals.minefactoryreloaded.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.WeightedRandomItem;

public class RandomMob extends WeightedRandomItem
{
	private Entity _mob;
	
	public RandomMob(Entity savedMob, int weight)
	{
		super(weight);
		_mob = savedMob;
	}
	
	public Entity getMob()
	{
		if(_mob == null) return null;
		return _mob;
	}
}
