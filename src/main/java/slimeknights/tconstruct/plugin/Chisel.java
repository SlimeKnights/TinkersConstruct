package slimeknights.tconstruct.plugin;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.block.BlockBrownstone.BrownstoneType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSeared.SearedType;

@Pulse(id = Chisel.PulseId, modsRequired = Chisel.modid, defaultEnable = true)
public class Chisel {
  public static final String modid = "chisel";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void init(FMLInitializationEvent event) {
    // skip cobble since its a bit out of place
    for(SearedType type : SearedType.values()) {
      if(type != SearedType.COBBLE) {
        addChiselVariation(TinkerSmeltery.searedBlock, type.getMeta(), "seared_block");
      }
    }


    for(BrownstoneType type : BrownstoneType.values()) {
      // skip rough since it is a smelting recipe difference
      if(type != BrownstoneType.ROUGH) {
        // chisel adds its own brownstone that is functionally different and cheaper, thus the custom name
        addChiselVariation(TinkerGadgets.brownstone, type.getMeta(), "brownstone_tconstruct");
      }
    }
  }

  protected void addChiselVariation(Block block, int meta, String groupName) {
    if(block != null) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("group", groupName);
      nbt.setTag("stack", new ItemStack(block, 1, meta).writeToNBT(new NBTTagCompound()));
      nbt.setString("block", block.getRegistryName().toString());
      nbt.setInteger("meta", meta);
      FMLInterModComms.sendMessage(modid, "add_variation", nbt);
    }
  }
}
