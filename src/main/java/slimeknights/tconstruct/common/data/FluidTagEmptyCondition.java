package slimeknights.tconstruct.common.data;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

/** Condition that checks when a fluid tag is empty. Same as {@link net.minecraftforge.common.crafting.conditions.TagEmptyCondition} but for fluids instead of items */
@RequiredArgsConstructor
public class FluidTagEmptyCondition implements ICondition {
  private static final ResourceLocation NAME = TConstruct.getResource("fluid_tag_empty");
  public static final Serializer SERIALIZER = new Serializer();
  private final ResourceLocation name;

  public FluidTagEmptyCondition(String domain, String name) {
    this(new ResourceLocation(domain, name));
  }

  @Override
  public ResourceLocation getID() {
    return NAME;
  }

  @Override
  public boolean test() {
    Tag<Fluid> tag = SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY).getTag(name);
    return tag == null || tag.getValues().isEmpty();
  }

  @Override
  public String toString()
  {
    return "fluid_tag_empty(\"" + name + "\")";
  }

  private static class Serializer implements IConditionSerializer<FluidTagEmptyCondition> {
    @Override
    public void write(JsonObject json, FluidTagEmptyCondition value) {
      json.addProperty("tag", value.name.toString());
    }

    @Override
    public FluidTagEmptyCondition read(JsonObject json) {
      return new FluidTagEmptyCondition(JsonHelper.getResourceLocation(json, "tag"));
    }

    @Override
    public ResourceLocation getID()
    {
      return NAME;
    }
  }
}
