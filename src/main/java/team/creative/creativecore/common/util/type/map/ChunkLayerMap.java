package team.creative.creativecore.common.util.type.map;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import team.creative.creativecore.common.util.type.itr.ComputeNextIterator;
import team.creative.creativecore.common.util.type.itr.FunctionIterator;
import team.creative.creativecore.common.util.type.list.Tuple;

public class ChunkLayerMap<T> implements Iterable<T> {
    
    /** 26.x: chunk layers are the ChunkSectionLayer enum (was RenderType.chunkBufferLayers()). */
    public static final java.util.List<ChunkSectionLayer> LAYERS = java.util.List.of(ChunkSectionLayer.values());
    public static final int LAYERS_COUNT = LAYERS.size();
    private static final Object2IntMap<ChunkSectionLayer> LAYERS_INDEX_MAP;
    
    public static int index(ChunkSectionLayer layer) {
        return LAYERS_INDEX_MAP.getInt(layer);
    }
    
    public static ChunkSectionLayer layer(int index) {
        return LAYERS.get(index);
    }
    
    static {
        LAYERS_INDEX_MAP = new Object2IntArrayMap<>();
        int i = 0;
        for (ChunkSectionLayer layer : LAYERS) {
            LAYERS_INDEX_MAP.put(layer, i);
            i++;
        }
    }
    
    private final T[] content;
    
    public ChunkLayerMap(ChunkLayerMap<T> map) {
        content = Arrays.copyOf(map.content, LAYERS_COUNT);
    }
    
    public ChunkLayerMap(Function<ChunkSectionLayer, T> factory) {
        content = (T[]) new Object[LAYERS_COUNT];
        for (int i = 0; i < content.length; i++)
            content[i] = factory.apply(LAYERS.get(i));
    }
    
    public ChunkLayerMap() {
        content = (T[]) new Object[LAYERS_COUNT];
    }
    
    public T get(ChunkSectionLayer layer) {
        return content[index(layer)];
    }
    
    @Nullable
    public T getByIndex(int i) {
        return content[i];
    }
    
    public T put(ChunkSectionLayer layer, T element) {
        int index = index(layer);
        T result = content[index];
        content[index] = element;
        return result;
    }
    
    public T remove(ChunkSectionLayer layer) {
        int index = index(layer);
        T result = content[index];
        content[index] = null;
        return result;
    }
    
    public void clear() {
        Arrays.fill(content, null);
    }
    
    public Iterable<Tuple<ChunkSectionLayer, T>> tuples() {
        return new ComputeNextIterator<>() {
            
            private int index;
            private final Tuple<ChunkSectionLayer, T> pair = new Tuple<>(null, null);
            
            @Override
            protected Tuple<ChunkSectionLayer, T> computeNext() {
                while (index < content.length && content[index] == null)
                    index++;
                if (index >= content.length)
                    return end();
                pair.key = LAYERS.get(index);
                pair.value = content[index];
                index++;
                return pair;
            }
        };
    }
    
    public boolean containsKey(ChunkSectionLayer layer) {
        return get(layer) != null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ComputeNextIterator<T>() {
            
            private int index;
            
            @Override
            protected T computeNext() {
                while (index < content.length && content[index] == null)
                    index++;
                if (index >= content.length)
                    return end();
                T result = content[index];
                index++;
                return result;
            }
        };
    }
    
    public int size() {
        int size = 0;
        for (int i = 0; i < content.length; i++)
            if (content[i] != null)
                size++;
        return size;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < content.length; i++)
            if (content[i] != null)
                return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "[" + String.join(",", () -> new FunctionIterator<>(this, Object::toString)) + "]";
    }
    
}
