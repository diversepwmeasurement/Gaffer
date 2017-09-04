/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.spark.serialisation.kryo.impl.datasketches.frequencies;

import com.yahoo.sketches.frequencies.LongsSketch;

public class LongsSketchKryoSerializerTest extends KryoSerializationTest<LongsSketch> {

    @Override
    public Class<LongsSketch> getTestClass() {
        return LongsSketch.class;
    }

    @Override
    public LongsSketch getTestObject() {
        final LongsSketch sketch = new LongsSketch(32);
        sketch.update(1L);
        sketch.update(2L);
        sketch.update(3L);
        return sketch;
    }
}
