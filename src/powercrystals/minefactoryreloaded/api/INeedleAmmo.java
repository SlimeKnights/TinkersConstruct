package powercrystals.minefactoryreloaded.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface INeedleAmmo
{
	public boolean onHitEntity(EntityPlayer owner, Entity hit, double distance);
	public void onHitBlock(EntityPlayer owner, World world, int x, int y, int z, int side, double distance);
	public float getSpread();
}
