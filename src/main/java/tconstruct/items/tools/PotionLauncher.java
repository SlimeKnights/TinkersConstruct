package tconstruct.items.tools;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.tools.entity.LaunchedPotion;

public class PotionLauncher extends Item
{
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    public static final String[] textureNames = new String[] { "potionlauncher" };

    public PotionLauncher()
    {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxDamage(3);
    }

    @Override
    public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        boolean loaded = tags.getBoolean("Loaded");
        if (!loaded)
        {
            int slotID = getInventorySlotContainItem(Items.potionitem, player.inventory);
            ItemStack potion = player.inventory.getStackInSlot(slotID);

            NBTTagCompound potionTag = new NBTTagCompound();
            potion.writeToNBT(potionTag);
            tags.setTag("LoadedPotion", potionTag);
            tags.setBoolean("Loaded", true);

            if (!player.capabilities.isCreativeMode)
            {
                potion.stackSize--;
                if (potion.stackSize < 1)
                    player.inventory.setInventorySlotContents(slotID, null);
            }

            world.playSoundEffect(player.posX, player.posY, player.posZ, "tinker:launcher_clank", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.15F + 1.0F);
        }
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int time)
    {

    }

    @Override
    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int slotID = getInventorySlotContainItem(Items.potionitem, player.inventory);
        if (!tags.getBoolean("Loaded") && slotID >= 0)
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration (ItemStack stack)
    {
        return 30;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction (ItemStack stack)
    {
        if (!stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Loaded"))
            return EnumAction.bow;
        else
            return EnumAction.none;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister par1IconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = par1IconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        return icons[0];
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tabs, List list)
    {
        ItemStack stack = new ItemStack(b, 1, 0);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tags = new NBTTagCompound();
        compound.setTag("InfiTool", tags);

        tags.setBoolean("Loaded", false);

        stack.setTagCompound(compound);

        list.add(stack);
    }

    @Override
    public boolean onEntitySwing (EntityLivingBase player, ItemStack stack)
    {
        if (stack != null && stack.hasTagCompound())
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (tags.getBoolean("Loaded"))
            {
                NBTTagCompound potionTag = tags.getCompoundTag("LoadedPotion");
                ItemStack potion = ItemStack.loadItemStackFromNBT(potionTag);// findPotion(player);InventoryLogic
                if (potion != null)
                {
                    World world = player.worldObj;
                    world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

                    if (!world.isRemote)
                    {
                        world.spawnEntityInWorld(new LaunchedPotion(world, player, potion));
                    }
                    tags.removeTag("LoadedPotion");
                    tags.setBoolean("Loaded", false);
                }
                return true;
            }
        }
        return false;
    }

    ItemStack findPotion (EntityLivingBase living)
    {
        if (living instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) living;
            int potionSlot = getInventorySlotContainItem(Items.potionitem, player.inventory);
            if (potionSlot >= 0)
            {
                return player.inventory.getStackInSlot(potionSlot);
            }
        }
        return null;
    }

    int getInventorySlotContainItem (Item item, InventoryPlayer inventory)
    {
        for (int j = 0; j < inventory.mainInventory.length; ++j)
        {
            if (inventory.mainInventory[j] != null && inventory.mainInventory[j].getItem() == item)
            {
                return j;
            }
        }

        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        list.add(StatCollector.translateToLocal("potionlauncher.tooltip"));
    }
}
