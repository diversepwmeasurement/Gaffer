/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.accumulostore.retriever.impl;

import gaffer.accumulostore.AccumuloStore;
import gaffer.accumulostore.operation.AccumuloTwoSetSeededOperation;
import gaffer.accumulostore.retriever.AccumuloSetRetriever;
import gaffer.accumulostore.retriever.RetrieverException;
import gaffer.accumulostore.utils.BloomFilterUtils;
import gaffer.operation.data.EntitySeed;
import gaffer.store.StoreException;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.hadoop.util.bloom.BloomFilter;

import java.util.Set;

public class AccumuloIDBetweenSetsRetriever extends AccumuloSetRetriever {
    private Iterable<EntitySeed> seedSetA;
    private Iterable<EntitySeed> seedSetB;

    public AccumuloIDBetweenSetsRetriever(final AccumuloStore store, final AccumuloTwoSetSeededOperation<EntitySeed, ?> operation,
                                          final IteratorSetting... iteratorSettings) throws StoreException {
        this(store, operation, false, iteratorSettings);
    }

    public AccumuloIDBetweenSetsRetriever(final AccumuloStore store, final AccumuloTwoSetSeededOperation<EntitySeed, ?> operation,
                                          final boolean readEntriesIntoMemory,
                                          final IteratorSetting... iteratorSettings) throws StoreException {
        super(store, operation, readEntriesIntoMemory, iteratorSettings);
        setSeeds(operation.getSeeds(), operation.getSeedsB());
    }

    private void setSeeds(final Iterable<EntitySeed> setA, final Iterable<EntitySeed> setB) {
        this.seedSetA = setA;
        this.seedSetB = setB;
    }

    @Override
    protected boolean hasSeeds() {
        return seedSetA.iterator().hasNext() && seedSetB.iterator().hasNext();
    }

    @Override
    protected ElementIteratorReadIntoMemory createElementIteratorReadIntoMemory() throws RetrieverException {
        return new ElementIteratorReadIntoMemory();
    }

    @Override
    protected ElementIteratorFromBatches createElementIteratorFromBatches() throws RetrieverException {
        return new ElementIteratorFromBatches();
    }

    private class ElementIteratorReadIntoMemory extends AbstractElementIteratorReadIntoMemory {
        private final Set<Object> verticesA;
        private final Set<Object> verticesB;

        ElementIteratorReadIntoMemory() throws RetrieverException {
            verticesA = extractVertices(seedSetA);
            verticesB = extractVertices(seedSetB);

            // Create Bloom filter, read through set of entities B and add them to Bloom filter
            final BloomFilter filter = BloomFilterUtils.getBloomFilter(store.getProperties().getFalsePositiveRate(), verticesB.size(),
                    store.getProperties().getMaxBloomFilterToPassToAnIterator());
            addToBloomFilter(verticesB, filter);
            addToBloomFilter(verticesA, filter);

            initialise(filter);
        }

        /**
         * @param source
         * @param destination
         * @return True if the source and destination contained in the provided seed sets
         */
        protected boolean checkIfBothEndsInSet(final Object source, final Object destination) {
            return verticesA.contains(source) && verticesB.contains(destination) || verticesB.contains(source) && verticesA.contains(destination);
        }
    }

    private class ElementIteratorFromBatches extends AbstractElementIteratorFromBatches {
        ElementIteratorFromBatches() throws RetrieverException {
            addToBloomFilter(seedSetB, filter, clientSideFilter);
            addToBloomFilter(seedSetA, filter, clientSideFilter);

            idsAIterator = seedSetA.iterator();
            updateScanner();
        }

        protected void updateBloomFilterIfRequired(final EntitySeed seed) throws RetrieverException {
            // no action required.
        }
    }
}