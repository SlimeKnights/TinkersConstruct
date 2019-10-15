package slimeknights.tconstruct.debug;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.tconstruct.shared.TinkerFluids;

public class TestIMC {
  public static void testAll() {
    TinkerDebug.log.warn("IMC testing enabled, this will add unexpected recipes to the registries");
    alloy();
    blacklistMelting();
    addDryingRecipe();
    addHeadDrop();
  }

  public static void integrateSmeltery() {
    // normal test, make sure to disable normal iron and gold during testing
    NBTTagCompound tag = new NBTTagCompound();
    FluidRegistry.registerFluid(TinkerFluids.iron);
    tag.setString("fluid", "iron");
    tag.setString("ore", "Iron");

    // send the NBT to TCon
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);

    // test with alloy and tool forge
    tag = new NBTTagCompound();
    FluidRegistry.registerFluid(TinkerFluids.gold);
    tag.setString("fluid", "gold");
    tag.setString("ore", "Gold");
    tag.setBoolean("toolforge", true);

    // output
    NBTTagList tagList = new NBTTagList();
    NBTTagCompound fluid = new NBTTagCompound();
    fluid.setString("FluidName", "gold");
    fluid.setInteger("Amount", 144); // 144 = 1 ingot
    tagList.appendTag(fluid);

    // first alloy fluid
    fluid = new NBTTagCompound();
    fluid.setString("FluidName", "iron");
    fluid.setInteger("Amount", 108); // 3/4 ingot
    tagList.appendTag(fluid);

    // second alloy fluid
    fluid = new NBTTagCompound();
    fluid.setString("FluidName", "water");
    fluid.setInteger("Amount", 1000); // 1 bucket
    tagList.appendTag(fluid);
    tag.setTag("alloy", tagList);

    // send the NBT to TCon
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
  }

  private static void alloy() {
    NBTTagList tagList = new NBTTagList();

    // output
    NBTTagCompound fluid = new NBTTagCompound();
    fluid.setString("FluidName", "manyullyn");
    fluid.setInteger("Amount", 144); // 144 = 1 ingot
    tagList.appendTag(fluid);

    // first alloy fluid
    fluid = new NBTTagCompound();
    fluid.setString("FluidName", "iron");
    fluid.setInteger("Amount", 108); // 3/4 ingot
    tagList.appendTag(fluid);

    // second alloy fluid
    fluid = new NBTTagCompound();
    fluid.setString("FluidName", "gold");
    fluid.setInteger("Amount", 36); // 1/4 ingot
    tagList.appendTag(fluid);

    NBTTagCompound message = new NBTTagCompound();
    message.setTag("alloy", tagList);
    FMLInterModComms.sendMessage("tconstruct", "alloy", message);
  }

  private static void blacklistMelting() {
    FMLInterModComms.sendMessage("tconstruct", "blacklistMelting", new ItemStack(Items.IRON_CHESTPLATE));
    OreDictionary.registerOre("ironHelmet", Items.IRON_HELMET);
    FMLInterModComms.sendMessage("tconstruct", "blacklistMelting", "ironHelmet");
  }

  private static void addDryingRecipe() {
    NBTTagCompound tagCompound = new NBTTagCompound();
    tagCompound.setTag("input", new ItemStack(Blocks.GOLD_ORE).writeToNBT(new NBTTagCompound()));
    tagCompound.setTag("output", new ItemStack(Blocks.GOLD_BLOCK).writeToNBT(new NBTTagCompound()));
    tagCompound.setInteger("time", 60*3);
    FMLInterModComms.sendMessage("tconstruct", "addDryingRecipe", tagCompound);

    tagCompound = new NBTTagCompound();
    tagCompound.setString("input", "oreIron");
    tagCompound.setTag("output", new ItemStack(Blocks.IRON_BLOCK).writeToNBT(new NBTTagCompound()));
    tagCompound.setInteger("time", 40*3);
    FMLInterModComms.sendMessage("tconstruct", "addDryingRecipe", tagCompound);
  }

  private static void addHeadDrop() {
    NBTTagCompound tagCompound = new NBTTagCompound();
    tagCompound.setTag("head", new ItemStack(Blocks.GOLD_ORE).writeToNBT(new NBTTagCompound()));
    tagCompound.setString("entity", "minecraft:sheep");
    FMLInterModComms.sendMessage("tconstruct", "addHeadDrop", tagCompound);
  }
}
