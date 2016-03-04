/*
 * Copyright 2016 Crown Copyright
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

package gaffer.tuple.function;

import gaffer.function2.Validator;
import gaffer.tuple.Tuple;
import gaffer.tuple.function.context.FunctionContext;
import gaffer.tuple.view.TupleView;

import java.util.ArrayList;
import java.util.List;

public class TupleValidator<R> extends Validator<Tuple<R>> {
    private List<FunctionContext<Validator, R>> validators;

    public TupleValidator() { }

    public TupleValidator(final List<FunctionContext<Validator, R>> validators) {
        setValidators(validators);
    }

    public void setValidators(final List<FunctionContext<Validator, R>> validators) {
        this.validators = validators;
    }

    public void addValidator(final FunctionContext<Validator, R> validator) {
        if (validators == null) {
            validators = new ArrayList<FunctionContext<Validator, R>>();
        }
        validators.add(validator);
    }

    public void addValidator(final TupleView<R> selection, final Validator validator, final TupleView<R> projection) {
        FunctionContext<Validator, R> context = new FunctionContext<Validator, R>(selection, validator, projection);
        addValidator(context);
    }

    public boolean validate(final Tuple<R> input) {
        if (validators != null) {
            for (FunctionContext<Validator, R> validator : validators) {
                boolean valid = validator.getFunction().validate(validator.selectFrom(input));
                if (!valid) {
                    return false;
                }
            }
        }
        return true;
    }

    public TupleValidator<R> copy() {
        TupleValidator<R> copy = new TupleValidator<R>();
        for (FunctionContext<Validator, R> validator : this.validators) {
            copy.addValidator(validator.copy());
        }
        return copy;
    }
}
