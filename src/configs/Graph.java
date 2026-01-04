package configs;

import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph extends ArrayList<Node>{

    public boolean hasCycles() {
        for(Node n : this)
        {
            if(n.hasCycles()){
                return true;
            }
        }
        return false;
    }
    public void createFromTopics() {
        this.clear();
       TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        Map<String, Node> nodeMap = new HashMap<>();

        for (Topic t : tm.getTopics()) {
            String tName = "T" + t.name;
            Node tNode = nodeMap.computeIfAbsent(tName, Node::new);

            for (Agent s : t.getSubs()) {
                String aName = "A" + s.getName();
                Node aNode = nodeMap.computeIfAbsent(aName, Node::new);
                tNode.addEdge(aNode);
            }

            for (Agent p : t.getPubs()) {
                String aName = "A" + p.getName();
                Node aNode = nodeMap.computeIfAbsent(aName, Node::new);
                aNode.addEdge(tNode);
            }
        }

        this.addAll(nodeMap.values());
    }


}
