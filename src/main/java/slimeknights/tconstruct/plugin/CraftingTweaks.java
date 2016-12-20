package slimeknights.tconstruct.plugin;

import com.google.common.eventbus.Subscribe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;

@Pulse(id = CraftingTweaks.PulseId, modsRequired = CraftingTweaks.modid, defaultEnable = true)
public class CraftingTweaks {

  public static final String modid = "craftingtweaks";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void init(FMLInitializationEvent event) {
    NBTTagCompound tagCompound = new NBTTagCompound();
    tagCompound.setString("ContainerClass", ContainerCraftingStation.class.getName());
    tagCompound.setString("AlignToGrid", "left");
    FMLInterModComms.sendMessage(modid, "RegisterProvider", tagCompound);
  }

}
