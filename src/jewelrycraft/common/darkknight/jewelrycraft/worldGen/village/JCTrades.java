package common.darkknight.jewelrycraft.worldGen.village;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import common.darkknight.jewelrycraft.block.BlockList;
import common.darkknight.jewelrycraft.item.ItemList;
import common.darkknight.jewelrycraft.util.JewelryNBT;
import common.darkknight.jewelrycraft.util.JewelrycraftUtil;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class JCTrades implements IVillageTradeHandler
{

    public JCTrades()
    {
        super();
    }

    @Override
    public void manipulateTradesForVillager (EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        if (villager.getProfession() == 3000)
        {
            ItemStack ingredient = null;
            ItemStack ingredient2 = null;
            ItemStack result;

            int type = random.nextInt(12);
            switch(type)
            {
                case 0:
                {
                    result = JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size()));
                    result.stackSize = 1 + random.nextInt(16);
                    ingredient = new ItemStack(Item.emerald, 8 + random.nextInt(8));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 8 + random.nextInt(8));
                    break;
                }
                case 1:
                {
                    result = new ItemStack(ItemList.molds, 1, random.nextInt(2)); 
                    ingredient = new ItemStack(Item.emerald, 1 + random.nextInt(2));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 1 + random.nextInt(2));
                    break;
                }
                case 2:
                {
                    result = new ItemStack(ItemList.thiefGloves); 
                    ingredient = new ItemStack(Item.emerald, 16 + random.nextInt(8));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 16 + random.nextInt(8));
                    break;
                }
                case 3:
                {
                    result = new ItemStack(BlockList.displayer, 1 + random.nextInt(6)); 
                    ingredient = new ItemStack(Item.emerald, 8 + random.nextInt(32));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Block.blockEmerald, 2 + random.nextInt(6));
                    break;
                }
                case 4:
                {
                    result = new ItemStack(BlockList.jewelCraftingTable); 
                    ingredient = new ItemStack(Item.emerald, 8 + random.nextInt(17));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 8 + random.nextInt(17));
                    break;
                }
                case 5:
                {
                    result = new ItemStack(BlockList.shadowOre, 1 + random.nextInt(16)); 
                    ingredient = new ItemStack(Item.emerald, 8 + random.nextInt(17));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 8 + random.nextInt(17));
                    break;
                }
                case 6:
                {
                    result = new ItemStack(BlockList.molder); 
                    ingredient = new ItemStack(Item.emerald, 1 + random.nextInt(2));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 1 + random.nextInt(3));
                    break;
                }
                case 7:
                {
                    result = new ItemStack(BlockList.smelter); 
                    ingredient = new ItemStack(Item.emerald, 3 + random.nextInt(9));
                    if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 4 + random.nextInt(2));
                    break;
                }
                case 8:
                {
                    int end = random.nextInt(JewelrycraftUtil.modifiers.size());
                    result = JewelrycraftUtil.modifiers.get(end);
                    if(result.getMaxStackSize() > 1) result.stackSize = 1 + random.nextInt(16);
                    if(JewelrycraftUtil.modifiers.size() - 1 - end >= 3)
                    {
                        ingredient = new ItemStack(Item.emerald, 3 + random.nextInt(9));
                        if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 4 + random.nextInt(2));
                    }
                    else
                    {
                        ingredient = new ItemStack(Item.emerald, 32 + random.nextInt(33));
                        ingredient2 = new ItemStack(Block.blockEmerald, 8 + random.nextInt(16));                        
                    }
                    break;
                }
                case 9:
                {
                    int end = random.nextInt(JewelrycraftUtil.jewel.size());
                    result = JewelrycraftUtil.jewel.get(end);
                    result.stackSize = 1 + random.nextInt(3);
                    if(JewelrycraftUtil.modifiers.size() - 1 - end >= 1)
                    {
                        ingredient = new ItemStack(Item.emerald, 6 + random.nextInt(32));
                        if(random.nextBoolean()) ingredient2 = new ItemStack(Item.emerald, 2 + random.nextInt(16));
                    }
                    else
                    {
                        ingredient = new ItemStack(Block.blockEmerald, 16 + random.nextInt(32));
                        ingredient2 = new ItemStack(Block.blockEmerald, 8 + random.nextInt(48));                        
                    }
                    break;
                }
                default: 
                {
                    result = new ItemStack(ItemList.ring, 1, 0);
                    JewelryNBT.addMetal(result, JewelrycraftUtil.metal.get(random.nextInt(JewelrycraftUtil.metal.size())));
                    JewelryNBT.addModifier(result, JewelrycraftUtil.modifiers.get(random.nextInt(JewelrycraftUtil.modifiers.size())));
                    JewelryNBT.addJewel(result, JewelrycraftUtil.jewel.get(random.nextInt(JewelrycraftUtil.jewel.size())));
                    if(JewelryNBT.isModifierEffectType(result)) JewelryNBT.addMode(result, "Activated");
                    if(JewelryNBT.isJewelX(result, new ItemStack(Item.netherStar)) && JewelryNBT.isModifierX(result, new ItemStack(Item.book))) 
                        JewelryNBT.addMode(result, "Disenchant");
                    ingredient = new ItemStack(Item.emerald, 16 + random.nextInt(20));
                    ingredient2 = new ItemStack(Block.blockEmerald, 5 + random.nextInt(5));
                }
            }

            recipeList.addToListWithCheck(new MerchantRecipe(ingredient, ingredient2, result));
        }
    }
}