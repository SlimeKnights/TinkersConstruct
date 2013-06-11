package mods.tinker.tconstruct.entity;

import java.util.*;

import mods.tinker.tconstruct.entity.ai.*;
import net.minecraft.world.World;

public class Automaton extends GolemBase
{
	int state;
	TaskBase currentTask;
	HashMap<String, TaskBase> taskList = new HashMap<String, TaskBase>();

	public Automaton(World world)
	{
		super(world);
        this.texture = "/mods/tinker/textures/mob/automaton.png";
    	
    	taskList.put("wait", new TaskWait(this));
    	TaskBase task = new TaskClearcut(this);
    	taskList.put("clearcut", task);
    	currentTask = task;
	}

	@Override
    public void initCreature ()
    {
    	baseAttack = 5;
    	maxHealth = 30;
    }
	
	@Override
    public void updateAITick() 
    {
    	if (!currentTask.update())
    	{
    		currentTask.finishTask();
    	}
    }
	
	@Override
	protected boolean isAIEnabled()
    {
        return true;
    }
}
