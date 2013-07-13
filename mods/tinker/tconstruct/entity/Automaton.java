package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.entity.ai.GAIAttackTarget;
import mods.tinker.tconstruct.entity.ai.GAIFindTarget;
import mods.tinker.tconstruct.entity.ai.GAIFollowOwner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class Automaton extends GolemBase
{
    int state;

    /*TaskBase currentTask;
    HashMap<String, TaskBase> taskList = new HashMap<String, TaskBase>();*/

    public Automaton(World world)
    {
        super(world);
        this.setSize(0.9F, 2.42F);
        //this.texture = "/mods/tinker/textures/mob/crystalguardamber.png";
        this.tasks.addTask(1, new GAIAttackTarget(this));
        this.tasks.addTask(2, new GAIFindTarget(this));
        this.tasks.addTask(3, new GAIFollowOwner(this));

        /*taskList.put("wait", new TaskWait(this));
        TaskBase task = new TaskClearcut(this);
        taskList.put("clearcut", task);
        currentTask = task;*/
    }

    @Override
    public void initCreature ()
    {
        baseAttack = 5;
        maxHealth = 50;
    }

    /*@Override
    public void updateAITick() 
    {
    	if (!currentTask.update())
    	{
    		currentTask.finishTask();
    	}
    }*/

    protected String getHurtSound ()
    {
        return "mob.irongolem.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound ()
    {
        return "mob.irongolem.death";
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound (int par1, int par2, int par3, int par4)
    {
        this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
    }

    public boolean interact (EntityPlayer par1EntityPlayer)
    {
        this.setCurrentItemOrArmor(0, par1EntityPlayer.getCurrentEquippedItem().copy());
        return true;
    }
}
