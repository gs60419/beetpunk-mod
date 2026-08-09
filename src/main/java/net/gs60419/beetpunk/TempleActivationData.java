package net.gs60419.beetpunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Codec.INT.optionalFieldOf("Count", 0).forGetter(TempleActivationData::getCount),
            Codec.STRING.listOf().optionalFieldOf("Lv4Temples", List.of()).forGetter(TempleActivationData::lv4TemplePaths),
            Codec.STRING.listOf().optionalFieldOf("Milestones", List.of()).forGetter(TempleActivationData::milestoneIds)
    ).apply(instance, TempleActivationData::new));
    private static final SavedDataType<TempleActivationData> TYPE = new SavedDataType<>(
            DATA_ID,
            TempleActivationData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
    );
    private int activatedCount = 0;
    private final Set<String> lv4TemplePaths = new HashSet<>();
    private final Set<String> milestones = new HashSet<>();

    public static TempleActivationData get(ServerLevel level) {
        return level.getServer().overworld()
                .getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public TempleActivationData() {
    }

    private TempleActivationData(int activatedCount, List<String> lv4TemplePaths, List<String> milestones) {
        this.activatedCount = activatedCount;
        this.lv4TemplePaths.addAll(lv4TemplePaths);
        this.milestones.addAll(milestones);
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

    public boolean markTempleLv4(BeetTempleTier tier) {
        boolean added = lv4TemplePaths.add(tier.path());
        if (added) {
            setDirty();
        }
        return added;
    }

    public int lv4TempleCount() {
        return lv4TemplePaths.size();
    }

    public boolean markMilestone(String id) {
        boolean added = milestones.add(id);
        if (added) {
            setDirty();
        }
        return added;
    }

    private List<String> lv4TemplePaths() {
        return List.copyOf(lv4TemplePaths);
    }

    private List<String> milestoneIds() {
        return List.copyOf(milestones);
    }
}
