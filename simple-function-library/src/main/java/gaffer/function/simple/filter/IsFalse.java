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
package gaffer.function.simple.filter;

import gaffer.function.SingleInputFilterFunction;
import gaffer.function.annotation.Inputs;

/**
 * An <code>IsFalse</code> is a {@link gaffer.function.SingleInputFilterFunction} that checks that the input boolean is
 * false.
 */
@Inputs(Boolean.class)
public class IsFalse extends SingleInputFilterFunction {
    public IsFalse statelessClone() {
        return new IsFalse();
    }

    @Override
    protected boolean filter(final Object input) {
        return null != input && Boolean.FALSE.equals(input);
    }
}
