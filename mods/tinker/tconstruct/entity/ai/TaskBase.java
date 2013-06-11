package mods.tinker.tconstruct.entity.ai;

import java.util.List;

import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class TaskBase
{
    public final GolemBase owner;

    public TaskBase(GolemBase golem)
    {
        this.owner = golem;
    }

    public void init() {}

    public void saveTask(NBTTagCompound var1) {}
    public void loadTask(NBTTagCompound var1) {}

    public abstract boolean update();
    public void finishTask() {}
    
    public int getPriority()
    {
    	return 0;
    }
}
