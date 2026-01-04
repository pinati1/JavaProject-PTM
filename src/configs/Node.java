package configs;

import java.util.ArrayList;
import java.util.List;


public class Node {

    private         String name;
    private         List<Node> edges;
    private Message msg;

    public Node(String name){
        this.name = name;
        this.edges = new ArrayList<>();
        this.msg  =null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEdges(List<Node> edges) {
        if(edges!=null)
          this.edges = edges;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public Message getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }
    public void addEdge(Node edge){
        if(edge!=null)
            this.edges.add(edge);
    }
    public boolean hasCycles(){
        return hasCycles(new ArrayList<Node>());
    }
    private  boolean hasCycles(ArrayList<Node> visited){
        if(visited.contains(this))
            return true;
        visited.add(this);

        for(Node n : this.edges) {
            if(n.hasCycles(new ArrayList<>(visited))) {
                return true;
            }
        }
        return false;
    }


}