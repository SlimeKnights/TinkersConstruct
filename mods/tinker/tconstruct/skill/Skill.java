package mods.tinker.tconstruct.skill;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/* Base skill
 * 
 */

public abstract class Skill
{
    public int skillID;
    boolean active = true;

    public abstract String getTextureFile (int guiscale);

    public abstract String getSkillName ();

    public abstract void activate (Entity entity, World world);

    public void rightClickActivate (Entity entity, World world)
    {
    };

    public int chargeTime () //Ticks
    {
        return 0;
    }

    public boolean canEntityUseSkill (Entity entity)
    {
        return true;
    }

    public int getSkillCost ()
    {
        return 0;
    }

    public void setActive (boolean flag)
    {
        this.active = flag;
    }

    public boolean getActive ()
    {
        return active;
    }

    public Skill copy () throws InstantiationException, IllegalAccessException
    {
        Skill skill = this.getClass().newInstance();
        skill.setSkillID(this.skillID);
        return skill;
    }

    public void setSkillID (int i)
    {
        skillID = i;
    }

    public int getSkillID ()
    {
        return skillID;
    }

    /* Save/Load */
    public void saveToNBT (NBTTagCompound tag)
    {

    }

    public void readFromNBT (NBTTagCompound tag)
    {

    }
}
