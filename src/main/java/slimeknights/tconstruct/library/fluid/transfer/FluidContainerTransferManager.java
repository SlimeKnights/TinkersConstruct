package slimeknights.tconstruct.library.fluid.transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Logic for filling and emptying fluid containers that are not fluid handlers */
@Log4j2
public class FluidContainerTransferManager extends SimpleJsonResourceReloadListener {
  /** Map of all modifier types that are expected to load in datapacks */
  public static final GenericLoaderRegistry<IFluidContainerTransfer> TRANSFER_LOADERS = new GenericLoaderRegistry<>();
  /** Folder for saving the logic */
  public static final String FOLDER = "tinkering/materials/fluid_transfer";
  /** GSON instance */
  public static final Gson GSON = (new GsonBuilder())
    .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
    .registerTypeHierarchyAdapter(IFluidContainerTransfer.class, TRANSFER_LOADERS)
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create();
  /** Singleton instance of the manager */
  public static final FluidContainerTransferManager INSTANCE = new FluidContainerTransferManager();

  /** List of loaded transfer logic */
  @Setter
  private List<IFluidContainerTransfer> transfers = Collections.emptyList();

  private FluidContainerTransferManager() {
    super(GSON, FOLDER);
  }

  /** For internal use only */
  @Deprecated
  public void init() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, AddReloadListenerEvent.class, e -> e.addListener(this));
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, OnDatapackSyncEvent.class, e -> JsonUtils.syncPackets(e, new FluidContainerTransferPacket(this.transfers)));
  }

  /** Loads transfer from JSON */
  @Nullable
  private IFluidContainerTransfer loadFluidTransfer(ResourceLocation key, JsonElement json) {
    try {
      return TRANSFER_LOADERS.deserialize(GsonHelper.convertToJsonObject(json, "fluid_transfer"));
    } catch (JsonSyntaxException e) {
      log.error("Failed to load fluid container transfer info from {}", key, e);
      return null;
    }
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager manager, ProfilerFiller profiler) {
    long time = System.nanoTime();
    this.transfers = splashList.entrySet().stream()
                               .map(entry -> loadFluidTransfer(entry.getKey(), entry.getValue().getAsJsonObject()))
                               .filter(Objects::nonNull)
                               .toList();
    log.info("Loaded {} dynamic modifiers in {} ms", transfers.size(), (System.nanoTime() - time) / 1000000f);
  }

  /** Gets the transfer for the given item and fluid, or null if its not a valid item and fluid */
  @Nullable
  public IFluidContainerTransfer getTransfer(ItemStack stack, FluidStack fluid) {
    for (IFluidContainerTransfer transfer : transfers) {
      if (transfer.matches(stack, fluid)) {
        return transfer;
      }
    }
    return null;
  }
}
