package test;

import test.TopicManagerSingleton.TopicManager;

public class PlusAgent implements Agent {

    private double x= 0,y=0;

    private String inputTopic1,inputTopic2,outputTopic;

    public PlusAgent(String[] subs,String[] pubs){

        this.inputTopic1 = subs[0];
        this.inputTopic2 = subs[1];
        this.outputTopic = pubs[0];

        TopicManagerSingleton.get().getTopic(inputTopic1)
                .subscribe(this);

        TopicManagerSingleton.get().getTopic(inputTopic2)
                .subscribe(this);
    }
    @Override
    public String getName(){
            return "PlusAgent";
    }

    @Override
    public void reset(){
        this.x=0;
        this.y = 0;
    }
    @Override
    public void callback(String topic,Message msg){
        if(topic.equals(inputTopic1)){
            x= msg.asDouble;
        } else if (topic.equals(inputTopic2)) {
            y= msg.asDouble;
        }

        if(!Double.isNaN(x)&&!Double.isNaN(y)){
            TopicManagerSingleton.get().getTopic(outputTopic)
                    .publish(new Message(x+y));
        }

    }
    @Override
    public void close(){
        TopicManagerSingleton.get().getTopic(inputTopic1)
                .unsubscribe(this);
        TopicManagerSingleton.get().getTopic(inputTopic2)
                .unsubscribe(this);
    }
}
