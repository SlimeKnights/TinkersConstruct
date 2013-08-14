package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.entity.ai.AIFellTree;
import mods.tinker.tconstruct.entity.ai.AIFollowOwner;
import mods.tinker.tconstruct.entity.ai.AISwim;
import mods.tinker.tconstruct.entity.genetics.Trait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Gardeslime extends GolemBase
{
    Trait strength;
    Trait agility;
    Trait capacity;
    Trait intelligence;    
    
    public Gardeslime(World world)
    {
        super(world);
        this.texture = "/mods/tinker/textures/mob/googirl.png";
        this.setSize(0.375F, 0.875F);
        this.tasks.addTask(1, new AISwim(this));
        //this.tasks.addTask(8, new AIFellTree(this));
        this.tasks.addTask(10, new AIFollowOwner(this));
        
        strength = new Trait(10, 30, 4, true).setName("Strength");
        agility = new Trait(10, 30, 6, true).setName("Agility");
        capacity = new Trait(10, 30, 1, true).setName("Capacity");
        intelligence = new Trait(10, 30, 8, true).setName("Intelligence");
    }

    @Override
    public void setupInventory ()
    {
        inventory = new ItemStack[14];
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize () //Opacity, not size
    {
        return 1.0F;
    }

    public boolean interact (EntityPlayer player)
    {
        if (player.isSneaking())
        {
            if (!worldObj.isRemote)
                player.openGui(TConstruct.instance, TProxyCommon.miniGardyGui, this.worldObj, this.entityId, 0, 0);
            return true;
            //return false;
        }
        else
        {
            ItemStack stack = this.getHeldItem();
            if (stack == null)
            {
                ItemStack playerStack = player.getCurrentEquippedItem();
                if (playerStack != null)
                {
                    this.setCurrentItemOrArmor(0, playerStack.copy());
                    player.destroyCurrentEquippedItem();
                }
            }
            else
            {
                if (player.inventory.addItemStackToInventory(stack.copy()))
                {
                    //this.worldObj.playSoundAtEntity(this, par1Str, par2, par3);
                    this.worldObj.playSoundAtEntity(player, "random.pop", 0.3F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    this.setCurrentItemOrArmor(0, null);
                }
            }
            return true;
        }
    }

    /* Saving */
    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        NBTTagCompound traits = new NBTTagCompound();
        
        strength.saveToNBT(traits);
        agility.saveToNBT(traits);
        capacity.saveToNBT(traits);
        intelligence.saveToNBT(traits);
        
        tags.setCompoundTag("Traits", traits);
    }

    public void readEntityFromNBT (NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        NBTTagCompound traits = tags.getCompoundTag("Traits");
        
        if (traits != null)
        {
            strength = new Trait("Strength", traits);
            agility = new Trait("Agility", traits);
            capacity = new Trait("Capacity", traits);
            intelligence = new Trait("Intelligence", traits);
        }
    }
}
