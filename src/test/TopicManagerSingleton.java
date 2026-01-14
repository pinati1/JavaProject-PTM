package test;



import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {


  public static TopicManager get(){return TopicManager.instance;}


    public static class TopicManager{

        private static final TopicManagerSingleton.TopicManager instance = new TopicManagerSingleton.TopicManager();
        private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>() ;

        public static TopicManagerSingleton.TopicManager get(){
            return TopicManagerSingleton.TopicManager.instance;
        }


        private TopicManager(){}


        public Topic getTopic(String name){
            return topics.computeIfAbsent(name,Topic::new);
        }

        public Collection<Topic> getTopics(){

            return topics.values();
        }
        public void clear(){
            topics.clear();
        }




    }



}