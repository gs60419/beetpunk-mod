package net.gs60419.beetpunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Tracks how many temple cores have been activated across the world.
 * Each core records the count at the moment it was activated as its GLOW level,
 * so earlier cores glow dimmer and later ones glow brighter.
 */
public class TempleActivationData extends SavedData {

    private static final Identifier DATA_ID = Identifier.fromNamespaceAndPath(Beetpunk.MOD_ID, "temple_activation");
    private static final Codec<TempleActivationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("Count", 0).forGetter(TempleActivationData::getCount)
    ).apply(instance, TempleActivationData::new));
    private static final SavedDataType<TempleActivationData> TYPE = new SavedDataType<>(
            DATA_ID,
            TempleActivationData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
    );
    private int activatedCount = 0;

    public static TempleActivationData get(ServerLevel level) {
        return level.getServer().overworld()
                .getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public TempleActivationData() {
    }

    private TempleActivationData(int activatedCount) {
        this.activatedCount = activatedCount;
    }

    /** Increments the counter and returns the new value (1–13). */
    public int increment() {
        activatedCount = Math.min(activatedCount + 1, 13);
        setDirty();
        return activatedCount;
    }

    public int getCount() {
        return activatedCount;
    }
}
