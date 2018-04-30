/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hien.assign2;
import java.io.File;
import java.util.Map;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
/**
 *
 * @author hien
 */

public class Query {
    private GraphDatabaseService graphDb;
    
    //Setting up the database path
    public void initialize()
    {
        String databaseDirectory = "/Users/hien/NEO4J_HOME/data/databases/graph.db";
        File dbFile = new File(databaseDirectory);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile);
    }
    
    //Query a task using Travelsal API
    public void travelsalQuery(){
        System.out.println("___ Travelsal Query method ___ \n");
    
        try(Transaction tx = graphDb.beginTx())
        {
            ResourceIterator<Node> it = graphDb.findNodes(DynamicLabel.label("TASK"));
            it.next();
            if(it.hasNext()) {
                
                Node node = it.next();
                TraversalDescription traverse = graphDb.traversalDescription()
                        .depthFirst()
                        .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
                for(Path pos : graphDb.traversalDescription()
                        .depthFirst()
                        .evaluator(Evaluators.toDepth(2))
                        .traverse(node))
                {
                    System.out.println(pos);            
                }
            }
            tx.success();
        }
                    
        System.out.println("___ END OF TRAVELSAL QUERY ___");
    }
    
    //Cypher Query makes 4 queries
    public void cypherQuery()
    {  
        System.out.println("____ Start Cypher Query method for 4 queries ____");
        //Get the names of employee with no task assigned
        String[] queries = new String[4];
        queries[0] = "MATCH (e: EMPLOYEE) WHERE not exists((e)-[:WORKS_ON]-()) RETURN e.name";
        
        //Get project manager name, email, project he manages (title | start date | due date | stage | result)
        queries[1] = "MATCH (e: EMPLOYEE)"
                      + "MATCH (p: PROJECT)\n" 
                      + "MATCH (e)-[:MANAGES]->(p)\n" 
                      + "RETURN e.name, e.email, p.title, p.start_date, p.due_date, p.stage, p.result";
        
        // Get the names of projects and their
        // their average task durations, number of tasks and sorted by average task duration
        queries[2] = "MATCH (p: PROJECT)"
                      + "MATCH (t: TASK)\n"
                      + "MATCH (e: EMPLOYEE)\n"
                      + "MATCH (t)-[:BELONGS_TO]->(p)\n"
                      + "MATCH (e)-[:WORKS_ON]->(t)\n"
                      + "RETURN  p.title, t.name, count(e) AS num_engineers\n"
                      + "ORDER BY num_engineers DESC";
        
        //
        queries[3] = "MATCH (p: PROJECT {title: 'Rock onto building early card carry surface.'})"
                      + "SET p.stage = 'complete'\n"
                      + "RETURN p.title, p.stage";
        
        for (int i = 0; i < 4; i++){
            System.out.println("=== QUERY " + i + " ===");
            Result result = graphDb.execute(queries[i]);
            if (!result.hasNext())
                System.out.println("Node is not found!");
            while(result.hasNext()) {
                Map<String, Object> next = result.next();
                System.out.println(next.toString());      
            }
            
        }
        System.out.println("___ DONE WITH ALL QUERIES ___");
    }   
}
