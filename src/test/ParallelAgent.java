package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private Agent agent;

    public ParallelAgent(Agent agent, int capacity){
        this.agent = agent;
    }
    @Override
    public String getName(){return this.agent.getName();}


    @Override
    public void callback(String topic, Message msg) {this.agent.callback(topic,msg);}

    @Override
    public void close(){this.agent.close();}

    @Override
    public  void reset(){this.agent.reset();}
}
