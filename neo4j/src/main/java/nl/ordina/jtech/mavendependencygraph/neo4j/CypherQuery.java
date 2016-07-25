package nl.ordina.jtech.mavendependencygraph.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Place holder object for Cypher Queries
 */
public class CypherQuery {
    private String query;

    public CypherQuery(String query) {
        this.query = query;
    }

    public static CypherQuery cypher(final String query) {
        return new CypherQuery(query);
    }

    @Override
    public String toString() {
        return query;
    }

    public Result execute(final GraphDatabaseService databaseService) {
        return databaseService.execute(query);
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof CypherQuery && this.query.equals(((CypherQuery) obj).query);
    }

    @Override
    public int hashCode() {
        return query.hashCode();
    }

    public static Collector<CypherQuery, ?, CypherQuery> joining(final CharSequence delimiter) {
        return Collectors.mapping(new Function<CypherQuery, String>() {
            @Override
            public String apply(CypherQuery cypherQuery) {
                return cypherQuery.toString();
            }
        }, Collectors.collectingAndThen(Collectors.joining(delimiter), CypherQuery::new));
    }

    public CypherQuery prepend(String prependQuery) {
        return new CypherQuery(prependQuery + " " + this.query);
    }

}
