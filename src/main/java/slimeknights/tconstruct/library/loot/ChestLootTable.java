package slimeknights.tconstruct.library.loot;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;

public class ChestLootTable {
  private final List<ItemStack> listOfItems;

  public ChestLootTable(List<ItemStack> listOfItemsIn) {
    this.listOfItems = listOfItemsIn;
  }

  public void fillInventory(IInventory inventory, Random rand, LootContext context) {
    List<ItemStack> list = listOfItems;
    List<Integer> list1 = this.getEmptySlotsRandomized(inventory, rand, list);
    this.shuffleItems(list, list1.size(), rand);;

    for(ItemStack itemstack : list) {
      if(list1.isEmpty()) {
        return;
      }

      if(itemstack.isEmpty()) {
        inventory.setInventorySlotContents(list1.remove(list1.size() - 1).intValue(), ItemStack.EMPTY);
      }
      else {
        inventory.setInventorySlotContents(list1.remove(list1.size() - 1).intValue(), itemstack);
      }
    }
  }

  /**
   * shuffles items by changing their order and splitting stacks
   */
  private void shuffleItems(List<ItemStack> stacks, int emptySlotsSize, Random rand) {
    List<ItemStack> list = Lists.<ItemStack>newArrayList();
    Iterator<ItemStack> iterator = stacks.iterator();

    while(iterator.hasNext()) {
      ItemStack itemstack = iterator.next();

      if(itemstack.isEmpty()) {
        iterator.remove();
      }
      else if(itemstack.getCount() > 1) {
        list.add(itemstack);
        iterator.remove();
      }
    }

    emptySlotsSize = emptySlotsSize - stacks.size();

    while(emptySlotsSize > 0 && !list.isEmpty()) {
      ItemStack itemstack2 = list.remove(MathHelper.getInt(rand, 0, list.size() - 1));
      int i = MathHelper.getInt(rand, 1, itemstack2.getCount() / 2);
      ItemStack itemstack1 = itemstack2.splitStack(i);

      if(itemstack2.getCount() > 1 && rand.nextBoolean()) {
        list.add(itemstack2);
      }
      else {
        stacks.add(itemstack2);
      }

      if(itemstack1.getCount() > 1 && rand.nextBoolean()) {
        list.add(itemstack1);
      }
      else {
        stacks.add(itemstack1);
      }
    }

    stacks.addAll(list);
    Collections.shuffle(stacks, rand);
  }

  private List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand, List<ItemStack> listIn) {
    List<Integer> list = Lists.<Integer>newArrayList();
    Iterator<ItemStack> iterator = listIn.iterator();

    for(int i = 0; i < inventory.getSizeInventory(); ++i) {
      if(iterator.hasNext()) {
        if(!iterator.next().isEmpty()) {
          if(inventory.getStackInSlot(i).isEmpty()) {
            list.add(Integer.valueOf(i));
          }
        }
      }
    }

    Collections.shuffle(list, rand);
    return list;
  }

}
