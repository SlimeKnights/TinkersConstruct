package slimeknights.tconstruct.library.loot;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.utils.Tags;

public class RandomMaterials extends LootFunction {
  private static final Logger LOGGER = LogManager.getLogger();
  private final List<Material> materials;

  protected RandomMaterials(LootCondition[] conditionsIn, @Nullable List<Material> materialsIn) {
    super(conditionsIn);
    this.materials = materialsIn == null ? Collections.emptyList() : materialsIn;
  }

  @Override
  public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
    Material material;

    if(this.materials.isEmpty()) {
      List<Material> list = Lists.<Material>newArrayList();

      for(Material material1 : TinkerRegistry.getAllMaterials()) {
        if(stack.getItem() instanceof IToolPart) {
          if(material1.isCraftable() && ((IToolPart) stack.getItem()).canUseMaterial(material1)) {
            list.add(material1);
          }
        }
      }

      if(list.isEmpty()) {
        LOGGER.warn("Couldn't find a compatible material for {}", stack);
        return stack;
      }

      material = list.get(rand.nextInt(list.size()));
    }
    else {
      material = this.materials.get(rand.nextInt(this.materials.size()));
    }

    if(stack.getItem() instanceof IToolPart) {
      this.setMaterial(stack, material);
    }
    else {
      LOGGER.error("Invalid Item passed to Random Material! Current Stack: ", stack);
    }

    return stack;
  }

  public void setMaterial(ItemStack stack, Material mat) {
    if(!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }

    stack.getTagCompound().setString(Tags.PART_MATERIAL, mat.identifier);
  }

  public static class Serializer extends LootFunction.Serializer<RandomMaterials> {
    public Serializer() {
        super(Util.getResource("random_material"), RandomMaterials.class);
    }
    
    @Override
    public void serialize(JsonObject object, RandomMaterials functionClazz, JsonSerializationContext serializationContext) {
      if(!functionClazz.materials.isEmpty()) {
        JsonArray jsonarray = new JsonArray();

        for(Material material : functionClazz.materials) {
          String materialName = material.identifier;

          if(materialName == null) {
            throw new IllegalArgumentException("Don't know how to serialize material " + material);
          }

          jsonarray.add(new JsonPrimitive(materialName));
        }

        object.add("materials", jsonarray);
      }
    }

    @Nonnull
    @Override
    public RandomMaterials deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootCondition[] conditionsIn) {
      List<Material> list = Lists.<Material>newArrayList();

      if(object.has("materials")) {
        for(JsonElement jsonelement : JsonUtils.getJsonArray(object, "materials")) {
          String s = JsonUtils.getString(jsonelement, "material");
          Material material = TinkerRegistry.getMaterial(s);

          if(material == null) {
            throw new JsonSyntaxException("Unknown material '" + s + "'");
          }

          list.add(material);
        }
      }

      return new RandomMaterials(conditionsIn, list);
    }
  }
}
