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

package gaffer.operation.impl.get;

import gaffer.operation.data.EdgeSeed;
import gaffer.operation.data.ElementSeed;
import gaffer.operation.data.EntitySeed;
import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import gaffer.operation.GetOperation;
import gaffer.operation.OperationTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class GetElementsSeedTest implements OperationTest {
    private static final JSONSerialiser serialiser = new JSONSerialiser();

    @Test
    public void shouldSetSeedMatchingTypeToEquals() {
        // Given
        final ElementSeed elementSeed1 = new EntitySeed("identifier");

        // When
        final GetElementsSeed op = new GetElementsSeed(Collections.singletonList(elementSeed1));

        // Then
        assertEquals(GetOperation.SeedMatchingType.EQUAL, op.getSeedMatching());
    }

    @Test
    @Override
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final ElementSeed elementSeed1 = new EntitySeed("identifier");
        final ElementSeed elementSeed2 = new EdgeSeed("source2", "destination2", true);
        final GetElementsSeed op = new GetElementsSeed(Arrays.asList(elementSeed1, elementSeed2));

        // When
        byte[] json = serialiser.serialise(op, true);
        final GetElementsSeed deserialisedOp = serialiser.deserialise(json, GetElementsSeed.class);

        // Then
        final Iterator itr = deserialisedOp.getSeeds().iterator();
        assertEquals(elementSeed1, itr.next());
        assertEquals(elementSeed2, itr.next());
        assertFalse(itr.hasNext());
    }
}
