package test.blog;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import blog.DBLOGUtil;
import blog.model.Evidence;
import blog.model.Model;
import blog.model.Queries;
import blog.type.Timestep;

/**
 * Unit tests for DBLOGUtil.
 */
public class TestDBLOGUtil {
  @Test
  public void testSplitEvidenceInTime() {
    Model model = Model.fromString("random Boolean Weather(Timestep t) ~ true;"
        + "random Boolean Dummy ~ Bernoulli(0.5);");
    Evidence evidence = new Evidence(model);
    evidence.addFromString("obs Weather(@15) = true;"
        + "obs Weather(@2) = true;" + "obs Dummy = true;");
    // TODO: test SymbolEvidenceStatements too.

    Map<Timestep, Evidence> splitEvidence = DBLOGUtil
        .splitEvidenceInTime(evidence);
    // System.out.println(splitEvidence);
    assertEquals(splitEvidence.size(), 3);
    // TODO: Make it easy to construct ValueEvidenceStatements, so that we
    // aren't forced to compare string representations here...
    assertEquals("[Dummy = true]", splitEvidence.get(null).toString());
    assertEquals("[Weather(@2) = true]", splitEvidence.get(Timestep.at(2))
        .toString());
    assertEquals("[Weather(@15) = true]", splitEvidence.get(Timestep.at(15))
        .toString());
  }

  @Test
  public void testSplitQueriesInTime() {
    Model model = Model.fromString("random Boolean Weather(Timestep t) ~ true;"
        + "random Boolean Dummy ~ Bernoulli(0.5);");
    Queries queries = new Queries(model);
    queries.addFromString("query Dummy;" + "query Weather(@3);"
        + "query Weather(@13);");

    Map<Timestep, Queries> splitQueries = DBLOGUtil.splitQueriesInTime(queries);
    // System.out.println(splitQueries);
    assertEquals(3, splitQueries.size());
    assertEquals(Arrays.asList(queries.get(0)), splitQueries.get(null));
    assertEquals(Arrays.asList(queries.get(1)),
        splitQueries.get(Timestep.at(3)));
    assertEquals(Arrays.asList(queries.get(2)),
        splitQueries.get(Timestep.at(13)));
  }
}
