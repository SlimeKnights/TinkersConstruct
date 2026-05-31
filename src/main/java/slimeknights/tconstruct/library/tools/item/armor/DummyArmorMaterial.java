package slimeknights.tconstruct.library.tools.item.armor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.util.List;
import java.util.Map;

/** Armor material that returns 0 except for name, since we bypass all the usages */
@RequiredArgsConstructor
@Getter
public class DummyArmorMaterial implements IdAwareObject {
  private final ResourceLocation id;
  private final SoundEvent equipSound;

  public String getName() {
    return id.toString();
  }

  public Holder<ArmorMaterial> getMaterialHolder() {
    return Holder.direct(new ArmorMaterial(Map.of(), 0, Holder.direct(equipSound), () -> Ingredient.EMPTY, List.of(new Layer(id)), 0, 0));
  }
}
