/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.util.ArrayList;
import java.util.List;

public class CyclicBufferTracker<E>
extends AbstractComponentTracker<CyclicBuffer<E>> {
    static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
    static final int DEFAULT_BUFFER_SIZE = 256;
    int bufferSize = 256;

    public CyclicBufferTracker() {
        this.setMaxComponents(64);
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    protected void processPriorToRemoval(CyclicBuffer<E> component) {
        component.clear();
    }

    @Override
    protected CyclicBuffer<E> buildComponent(String key) {
        return new CyclicBuffer(this.bufferSize);
    }

    @Override
    protected boolean isComponentStale(CyclicBuffer<E> eCyclicBuffer) {
        return false;
    }

    List<String> liveKeysAsOrderedList() {
        return new ArrayList<String>(this.liveMap.keySet());
    }

    List<String> lingererKeysAsOrderedList() {
        return new ArrayList<String>(this.lingerersMap.keySet());
    }
}

