package nl.ordina.jtech.mavendependencygraph.neo4j;

import org.junit.Test;

/**
 * Class: DependencyGraphConverterTest
 */
public class DependencyGraphConverterTest {


    @Test
    public void testConvert() throws Exception {
        String s = DependencyGraphConverter.inCypher(GraphCreator.getGraph());
        System.out.println("s = " + s);
    }
}