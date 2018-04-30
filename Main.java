/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hien.assign2;

/**
 *
 * @author hien
 */
public class Main {
    public static void main(String[] args) {
        Query graph_query = new Query();
        graph_query.initialize();
        graph_query.travelsalQuery();
        graph_query.cypherQuery();
   
    }
}
