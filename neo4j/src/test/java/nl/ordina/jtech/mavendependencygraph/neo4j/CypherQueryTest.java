package nl.ordina.jtech.mavendependencygraph.neo4j;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by pieter on 27/05/16.
 */
public class CypherQueryTest {

    @Test
    public void name() throws Exception {
        Stream<CypherQuery> stream = Arrays.asList(new CypherQuery("a"), new CypherQuery("b")).stream();

        String tmp  = stream.collect(Collectors.mapping(new Function<CypherQuery, String>() {
            @Override
            public String apply(CypherQuery cypherQuery) {
                return cypherQuery.toString();
            }
        }, Collectors.joining("----")) );

        System.out.println("tmp = " + tmp);
    }
}