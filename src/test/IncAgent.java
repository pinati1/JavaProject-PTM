package test;

import test.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {

    private String sub,pub;
    private double x=0;

    public IncAgent(String[] subs,String[] pubs){

        this.sub = subs[0];
        this.pub = pubs[0];

        TopicManagerSingleton.get().getTopic(sub).subscribe(this);

    }

    @Override
    public String getName(){return "IncAgent";}
    @Override

    public void reset(){x=0;}

    @Override
    public void callback(String topic ,Message msg){
        if(this.sub.equals(topic)) {
            this.x=msg.asDouble;
        }
        if(!Double.isNaN(x)){
            TopicManagerSingleton.get().getTopic(pub)
                    .publish(new Message(x+1));
        }
    }

    @Override
    public void close() {
        TopicManagerSingleton.get().getTopic(sub)
                .unsubscribe(this);
    }
}
