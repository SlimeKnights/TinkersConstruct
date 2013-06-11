package mods.tinker.tconstruct.entity.ai;

import mods.tinker.tconstruct.entity.GolemBase;

public class TaskWait extends TaskBase
{
	public TaskWait(GolemBase golem)
	{
		super(golem);
	}

	@Override
	public boolean update ()
	{
		return true;
	}
}
