package slimeknights.tconstruct.tools.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;
import java.util.Collections;

/** Armor model that wraps another armor model */
public class ArmorModelWrapper<T extends LivingEntity> extends HumanoidModel<T> {

  /** Creates a model part containing all elements of the given model */
  private static ModelPart copyOf(HumanoidModel<?> base) {
    return new ModelPart(Collections.emptyList(), ImmutableMap.of(
      "head", base.head, "hat", base.hat, "body", base.body,
      "right_arm", base.rightArm, "left_arm", base.leftArm,
      "right_leg", base.rightLeg, "left_leg", base.leftLeg));
  }

  public ArmorModelWrapper(HumanoidModel<T> base) {
    super(copyOf(base), base::renderType);
  }

  /* Helpers */

  /** Buffer from the render living event, stored as we lose access to it later */
  @Nullable
  protected static MultiBufferSource buffer;

  /** Iniitalizes the wrapper */
  public static void init() {
    // register listeners to set and clear the buffer
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Pre.class, event -> buffer = event.getMultiBufferSource());
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Post.class, event -> buffer = null);
  }
}
