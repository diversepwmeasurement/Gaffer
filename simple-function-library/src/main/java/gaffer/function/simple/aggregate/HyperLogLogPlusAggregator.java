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
package gaffer.function.simple.aggregate;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import gaffer.function.SingleInputAggregateFunction;
import gaffer.function.annotation.Inputs;
import gaffer.function.annotation.Outputs;

/**
 * An <code>HyperLogLogPlusAggregator</code> is a {@link gaffer.function.SingleInputAggregateFunction} that takes in
 * {@link com.clearspring.analytics.stream.cardinality.HyperLogLogPlus}s and merges the sketches together.
 */
@Inputs(HyperLogLogPlus.class)
@Outputs(HyperLogLogPlus.class)
public class HyperLogLogPlusAggregator extends SingleInputAggregateFunction {
    private HyperLogLogPlus sketch;

    @Override
    public void init() {
        sketch = new HyperLogLogPlus(5, 5);
    }

    @Override
    public void execute(final Object object) {
        if (object != null) {
            try {
                sketch.addAll((HyperLogLogPlus) object);
            } catch (CardinalityMergeException exception) {
                throw new RuntimeException("An Exception occurred when trying to aggregate the HyperLogLogPlus objects", exception);
            }
        }
    }

    @Override
    public Object[] state() {
        return new Object[]{sketch};
    }

    @Override
    public HyperLogLogPlusAggregator statelessClone() {
        HyperLogLogPlusAggregator clone = new HyperLogLogPlusAggregator();
        clone.init();
        return clone;
    }
}
