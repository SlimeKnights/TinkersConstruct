package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.entity.ai.GAIFellTree;
import mods.tinker.tconstruct.entity.ai.GAIFollowOwner;
import mods.tinker.tconstruct.entity.ai.GAISwim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiniGardy extends GolemBase
{
    public MiniGardy(World world)
    {
        super(world);
        this.texture = "/mods/tinker/textures/mob/googirl.png";
        this.setSize(0.375F, 0.875F);
        this.tasks.addTask(1, new GAISwim(this));
        this.tasks.addTask(8, new GAIFellTree(this));
        this.tasks.addTask(10, new GAIFollowOwner(this));
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
}
