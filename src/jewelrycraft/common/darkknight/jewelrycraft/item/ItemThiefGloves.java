package common.darkknight.jewelrycraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class ItemThiefGloves extends Item
{
    public Random rand;
    
    public ItemThiefGloves(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);
        this.setMaxDamage(10);
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer par2EntityPlayer, EntityLivingBase par3EntityLivingBase)
    {
        if (par3EntityLivingBase instanceof EntityVillager)
        {
            EntityVillager villager = (EntityVillager) par3EntityLivingBase;
            int wealth = (Integer) ReflectionHelper.getPrivateValue(EntityVillager.class, villager, "wealth", "field_70956_bz");
            MerchantRecipeList buyingList = (MerchantRecipeList) ReflectionHelper.getPrivateValue(EntityVillager.class, villager, "buyingList", "field_70963_i");
            if (buyingList != null)
            {
                Iterator<?> iterator = buyingList.iterator();
                while (iterator.hasNext())
                {
                    MerchantRecipe recipe = (MerchantRecipe) iterator.next();
                    int toolUses = (Integer) ReflectionHelper.getPrivateValue(MerchantRecipe.class, recipe, "toolUses", "field_77400_d");
                    int quantity;
                    if (recipe.getItemToSell().isStackable())
                        quantity = recipe.getItemToSell().stackSize * (7 - toolUses);
                    else
                        quantity = 1;
                    ItemStack s = new ItemStack(recipe.getItemToSell().itemID, quantity, recipe.getItemToSell().getItemDamage());
                    s.setTagCompound(recipe.getItemToSell().getTagCompound());
                    if (par2EntityPlayer.inventory.addItemStackToInventory(s))
                        ;
                    else
                        villager.entityDropItem(s, 0);
                    par2EntityPlayer.addChatMessage("Villager #" + villager.getProfession() + ": Hmmm... I seem to have lost my " + s.getDisplayName() + "!");
                    stack.damageItem(1, par2EntityPlayer);
                }
                buyingList.clear();
                ReflectionHelper.setPrivateValue(EntityVillager.class, villager, 300, "timeUntilReset", "field_70961_j");
                ReflectionHelper.setPrivateValue(EntityVillager.class, villager, true, "needsInitilization", "field_70959_by");
            }
            
            villager.dropItem(Item.emerald.itemID, wealth);
            ReflectionHelper.setPrivateValue(EntityVillager.class, villager, 0, "wealth", "field_70956_bz");
            return true;
        }
        else
        {
            return super.itemInteractionForEntity(stack, par2EntityPlayer, par3EntityLivingBase);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, @SuppressWarnings("rawtypes") List list, boolean par4)
    {
        if (!shouldAddAdditionalInfo())
            list.add(EnumChatFormatting.GRAY + additionalInfoInstructions());
        else
        {
            list.add(EnumChatFormatting.GRAY + "Right click with the gloves,");
            list.add(EnumChatFormatting.GRAY + "while sneaking, on a villager");
            list.add(EnumChatFormatting.GRAY + "to steal his stuff.");
        }
    }
    
    public static boolean shouldAddAdditionalInfo()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                return true;
            }
        }
        return false;
    }
    
    public static String additionalInfoInstructions()
    {
        String message = "\247oPress \247b<SHIFT>\2477\247o for more information.";
        return message;
    }
    
}
