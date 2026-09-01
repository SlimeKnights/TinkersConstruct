package slimeknights.tconstruct.library.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;

import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.util.typed.TypedMap;

/** Generic */
public abstract class EitherLoadable<A, B> implements Loadable<Either<A, B>> {
    public static final <A, B> EitherLoadable<A, B> create(Loadable<A> leftLoader, Loadable<B> rightLoader) {
        return new EitherLoadable<A, B>() {
            @Override
            public Either<A, B> convert(JsonElement element, String key, TypedMap context) {
                try {
                    return Either.left(leftLoader.convert(element, key, context));
                } catch (JsonSyntaxException e1) {
                    try {
                        return Either.right(rightLoader.convert(element, key, context));
                    } catch (JsonSyntaxException e2) {
                        throw new JsonSyntaxException(
                                "Cannot parse '%s': First error: %s, Second error: %s".formatted(key, e1.toString(),
                                        e2.toString()),
                                e1);
                    }
                }
            }

            @Override
            public JsonElement serialize(Either<A, B> object) {
                if (object.left().isPresent())
                    return leftLoader.serialize(object.left().get());
                else
                    return rightLoader.serialize(object.right().get());
            }

            @Override
            public Either<A, B> decode(FriendlyByteBuf buffer, TypedMap context) {
                if (buffer.readBoolean())
                    return Either.left(leftLoader.decode(buffer, context));
                return Either.right(rightLoader.decode(buffer, context));
            }

            @Override
            public void encode(FriendlyByteBuf buffer, Either<A, B> value) {
                if (value.left().isPresent()) {
                    buffer.writeBoolean(true);
                    leftLoader.encode(buffer, value.left().get());
                } else {
                    buffer.writeBoolean(false);
                    rightLoader.encode(buffer, value.right().get());
                }
            }
        };
    }
}