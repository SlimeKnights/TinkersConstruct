package mods.tinker.tconstruct.entity;

import mods.tinker.tconstruct.entity.ai.*;
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
        this.tasks.addTask(3, new GAIFollowOwner(this));
        this.tasks.addTask(1, new GAIFellTree(this));
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
        ItemStack stack = this.getHeldItem();
        if (stack == null)
        {
            this.setCurrentItemOrArmor(0, player.getCurrentEquippedItem().copy());
            player.destroyCurrentEquippedItem();
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
