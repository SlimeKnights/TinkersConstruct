package tconstruct.items.armor;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.items.CraftingItem;
import tconstruct.library.IHealthAccessory;
import tconstruct.util.player.ArmorExtended;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HeartCanister extends CraftingItem implements IHealthAccessory
{

    public HeartCanister(int id)
    {
        super(id, new String[] { "empty", "miniheart.red", "red", "miniheart.yellow", "yellow", "miniheart.green", "green" }, 
                new String[] { "canister_empty", "miniheart_red", "canister_red", "miniheart_yellow", "canister_yellow", "miniheart_green", "canister_green" }, "");
        this.setMaxStackSize(10);
    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        int meta = stack.getItemDamage();
        if (meta == 1 || meta == 3 || meta == 5)
        {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        if (!world.isRemote && meta == 2)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats != null)
            {
                ArmorExtended armor = stats.armor;
                ItemStack slotStack = armor.getStackInSlot(6);
                if (slotStack == null)// || slotStack.getItem() == this)
                {
                    armor.setInventorySlotContents(6, new ItemStack(this, 1, 2));
                    stack.stackSize--;
                }
                else if (slotStack.getItem() == this && slotStack.stackSize < this.maxStackSize)
                {
                    slotStack.stackSize++;
                    stack.stackSize--;
                }
                armor.recalculateHealth(player, stats);
            }
        }
        return stack;
    }
    
    @Override
    public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
    {
        int meta = stack.getItemDamage();
        --stack.stackSize;
        player.heal((meta+1)*10);
        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        return stack;
    }
    
    @Override
    public EnumAction getItemUseAction (ItemStack par1ItemStack)
    {
        return EnumAction.eat;
    }
    
    public int getMaxItemUseDuration (ItemStack par1ItemStack)
    {
        return 32;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int meta = stack.getItemDamage();
        if (meta == 0 || meta % 2 == 1)
            list.add(StatCollector.translateToLocal("item.crafting.tooltip"));
        else
        {
            list.add(StatCollector.translateToLocal("item.accessory.tooltip"));
            list.add(StatCollector.translateToLocal("canister.tooltip"));
        }
        
        switch (meta)
        {
        case 1:
            list.add(StatCollector.translateToLocal("canister.red.tooltip1"));
            list.add(StatCollector.translateToLocal("canister.red.tooltip2"));
            break;
        case 3:
            list.add(StatCollector.translateToLocal("canister.yellow.tooltip1"));
            list.add(StatCollector.translateToLocal("canister.yellow.tooltip2"));
            break;
        case 5:
            list.add(StatCollector.translateToLocal("canister.green.tooltip1"));
            list.add(StatCollector.translateToLocal("canister.green.tooltip2"));
            break;
        }
    }

    @Override
    public boolean canEquipItem (ItemStack item, int slot)
    {
        int type = item.getItemDamage();
        return ((type == 2 && slot == 6) || (type == 4 && slot == 5) || (type == 6 && slot == 4));
    }

    @Override
    public int getHealthBoost (ItemStack item)
    {
        return item.stackSize * 2;
    }

}
