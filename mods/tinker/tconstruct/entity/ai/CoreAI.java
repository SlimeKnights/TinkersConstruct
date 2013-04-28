package mods.tinker.tconstruct.entity.ai;

import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.entity.Entity;

public class CoreAI
{
	public void initAI (GolemBase golem, boolean flag)
	{
	}
	
	public void idle (GolemBase golem)
	{
	}
	

	public void update (GolemBase golem)
	{
	}

	public void lateUpdate (GolemBase golem)
	{
	}

	public void interact (GolemBase golem)
	{
	}
	
	public boolean patrol (GolemBase golem)
	{
		return false;
	}
	
	public boolean follow (GolemBase golem)
	{
		return false;
	}

	public boolean attack (GolemBase golem, Entity entity, float f)
	{
		return false;
	}

	public boolean protect (GolemBase golem)
	{
		return false;
	}

	public void onWork (GolemBase golem)
	{
	}

	public void undoAI (GolemBase golem, boolean flag)
	{
	}
}
