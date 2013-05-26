package mods.tinker.tconstruct.plugins.minefactoryreloaded.grindables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.api.IFactoryGrindable;
import powercrystals.minefactoryreloaded.api.MobDrop;

public class GrindableStandard implements IFactoryGrindable
{
	private Class<?> _grindableClass;
	private List<MobDrop> _drops;
	
	public GrindableStandard(Class<?> entityToGrind, MobDrop[] dropStacks)
	{
		_grindableClass = entityToGrind;
		_drops = new ArrayList<MobDrop>();
		for(MobDrop d : dropStacks)
		{
			_drops.add(d);
		}
	}
	
	public GrindableStandard(Class<?> entityToGrind, ItemStack dropStack)
	{
		_grindableClass = entityToGrind;
		_drops = new ArrayList<MobDrop>();
		_drops.add(new MobDrop(10, dropStack));
	}
	
	public GrindableStandard(Class<?> entityToGrind)
	{
		_grindableClass = entityToGrind;
		_drops = new ArrayList<MobDrop>();
	}
	
	@Override
	public Class<?> getGrindableEntity()
	{
		return _grindableClass;
	}
	
	@Override
	public List<MobDrop> grind(World world, EntityLiving entity, Random random)
	{
		return _drops;
	}
}
