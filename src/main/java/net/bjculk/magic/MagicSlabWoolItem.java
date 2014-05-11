/*    */ package net.bjculk.magic;
/*    */ 
/*    */ import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemDye;
/*    */ import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
/*    */ 
/*    */ public class MagicSlabWoolItem extends ItemBlock
/*    */ {
/*  8 */   public static final String[] blockNames = { "white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray", "lightgray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
/*    */ 

public MagicSlabWoolItem(int par1)
{
    super(par1);
    this.setMaxDamage(0);
    this.setHasSubtypes(true);
    
}

@SideOnly(Side.CLIENT)

/**
 * Gets an icon index based on an item's damage value
 */
public Icon getIconFromDamage(int par1)
{
    return Block.cloth.getIcon(2, BlockColored.getBlockFromDye(par1));
}

/**
 * Returns the metadata of the block which this Item (ItemBlock) can place
 */
public int getMetadata(int par1)
{
    return par1;
}

/**
 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
 * different names based on their damage or NBT.
 */
public String getUnlocalizedName(ItemStack par1ItemStack)
{
    return super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[BlockColored.getBlockFromDye(par1ItemStack.getItemDamage())];
}

/*    */ }

/* Location:           /Users/benadam/Desktop/infiblocks.zip
 * Qualified Name:     inficraft.infiblocks.magicslabs.MagicSlabWoolItem
 * JD-Core Version:    0.6.2
 */