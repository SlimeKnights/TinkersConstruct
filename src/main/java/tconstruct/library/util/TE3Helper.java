package tconstruct.library.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TE3Helper
{

    public static void addInductionSmelterRecipe (int energy, ItemStack input1, ItemStack input2, ItemStack output1, ItemStack output2, int chance)
    {
        NBTTagCompound data = new NBTTagCompound();

        data.setInteger("energy", energy);

        NBTTagCompound input1Compound = new NBTTagCompound();
        input1.writeToNBT(input1Compound);
        data.setCompoundTag("primaryInput", input1Compound);

        NBTTagCompound input2Compound = new NBTTagCompound();
        input2.writeToNBT(input2Compound);
        data.setCompoundTag("secondaryInput", input2Compound);

        NBTTagCompound output1Compound = new NBTTagCompound();
        output1.writeToNBT(output1Compound);
        data.setCompoundTag("primaryOutput", output1Compound);

        if (output2 != null)
        {
            NBTTagCompound output2Compound = new NBTTagCompound();
            output2.writeToNBT(output2Compound);
            data.setCompoundTag("secondaryOutput", output2Compound);

            data.setInteger("secondaryChance", chance);
        }

        FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", data);
    }

    public static void addPulveriserRecipe (int energy, ItemStack input, ItemStack output, ItemStack bonus, int chance)
    {
        NBTTagCompound data = new NBTTagCompound();

        data.setInteger("energy", energy);

        NBTTagCompound inputCompound = new NBTTagCompound();
        input.writeToNBT(inputCompound);
        data.setCompoundTag("input", inputCompound);

        NBTTagCompound outputCompound = new NBTTagCompound();
        output.writeToNBT(outputCompound);
        data.setCompoundTag("primaryOutput", outputCompound);

        if (bonus != null)
        {
            NBTTagCompound outputCompound2 = new NBTTagCompound();
            bonus.writeToNBT(outputCompound2);
            data.setCompoundTag("secondaryOutput", outputCompound2);

            data.setInteger("secondaryChance", chance);
        }

        FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", data);
    }
}
