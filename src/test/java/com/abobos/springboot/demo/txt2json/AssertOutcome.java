package com.abobos.springboot.demo.txt2json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;

public class AssertOutcome {
    public static void assertOutcome(final OutcomeLineItem first, final String name,
                                     final String transportation,

                                     final Double topSpeed) {

        assertThat(first.getName(), is(name));
        assertThat(first.getTransport(), is(transportation));
        assertThat(first.getTopSpeed(), is(topSpeed));
    }
}
