package tconstruct.items.tools;

import java.util.List;

import tconstruct.entity.projectile.LaunchedPotion;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PotionLauncher extends Item
{
    @SideOnly(Side.CLIENT)
    private Icon[] icons;
    public static final String[] textureNames = new String[] { "potionlauncher" };

    public PotionLauncher(int par1)
    {
        super(par1);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxDamage(3);
    }

    public ItemStack onEaten (ItemStack stack, World world, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        boolean loaded = tags.getBoolean("Loaded");
        if (!loaded)
        {
            int slotID = getInventorySlotContainItem(Item.potion.itemID, player.inventory);
            ItemStack potion = player.inventory.getStackInSlot(slotID);

            NBTTagCompound potionTag = new NBTTagCompound();
            potion.writeToNBT(potionTag);
            tags.setCompoundTag("LoadedPotion", potionTag);
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

    public void onPlayerStoppedUsing (ItemStack stack, World world, EntityPlayer player, int time)
    {

    }

    public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int slotID = getInventorySlotContainItem(Item.potion.itemID, player.inventory);
        if (!tags.getBoolean("Loaded") && slotID >= 0)
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration (ItemStack stack)
    {
        return 30;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction (ItemStack stack)
    {
        if (!stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Loaded"))
            return EnumAction.bow;
        else
            return EnumAction.none;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = par1IconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        return icons[0];
    }

    @Override
    public void getSubItems (int id, CreativeTabs tabs, List list)
    {
        ItemStack stack = new ItemStack(id, 1, 0);
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tags = new NBTTagCompound();
        compound.setCompoundTag("InfiTool", tags);

        tags.setBoolean("Loaded", false);

        stack.setTagCompound(compound);

        list.add(stack);
    }

    @Override
    public boolean onEntitySwing (EntityLivingBase player, ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Loaded"))
        {
            NBTTagCompound potionTag = tags.getCompoundTag("LoadedPotion");
            ItemStack potion = ItemStack.loadItemStackFromNBT(potionTag);//findPotion(player);InventoryLogic
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
        return false;
    }

    ItemStack findPotion (EntityLivingBase living)
    {
        if (living instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) living;
            int potionSlot = getInventorySlotContainItem(Item.potion.itemID, player.inventory);
            if (potionSlot >= 0)
            {
                return player.inventory.getStackInSlot(potionSlot);
            }
        }
        return null;
    }

    int getInventorySlotContainItem (int itemID, InventoryPlayer inventory)
    {
        for (int j = 0; j < inventory.mainInventory.length; ++j)
        {
            if (inventory.mainInventory[j] != null && inventory.mainInventory[j].itemID == itemID)
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
        list.add("Test Item");
    }
}
