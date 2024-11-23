package btw.community.arminias.foodspoil;

public interface AgingEntityExtension {
    void foodspoilmod$setSpawnedAt(long spawnedAt);
    long foodspoilmod$getSpawnedAt();
    boolean foodspoilmod$isOld();
    boolean foodspoilmod$isDying();
}
