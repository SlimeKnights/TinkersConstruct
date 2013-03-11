package mods.tinker.tconstruct.entity;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

public class Crystal extends EntityCreeper
{

	public Crystal(World par1World)
	{
		super(par1World);
		this.texture = "/tinkertextures/mob/crystalwater.png";
	}

	@Override
	public int getMaxHealth ()
	{
		return 100;
	}

}
