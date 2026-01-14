package configs;

import java.util.function.BinaryOperator;
import graph.*;

public class BinOpAgent implements Agent {

    private final String name;
    private final String input1;
    private final String input2;
    private final String output;
    private final BinaryOperator<Double> operator;

    private Double val1 = 0.0;
    private Double val2 = 0.0;

    public BinOpAgent(String name, String input1, String input2,
                      String output, BinaryOperator<Double> operator) {
        this.name = name;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.operator = operator;


      TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        tm.getTopic(input1).subscribe(this);
        tm.getTopic(input2).subscribe(this);
        tm.getTopic(output).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        this.val1 = 0.0;
        this.val2 = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        Double value = msg.asDouble;

        if (Double.isNaN(value)) {
            return;
        }

        if (topic.equals(input1)) {
            val1 = value;
        } else if (topic.equals(input2)) {
            val2 = value;
        }

        Double result = operator.apply(val1, val2);
        TopicManagerSingleton.get().getTopic(output)
                .publish(new Message(result)); // [cite: 145]
    }

    @Override
    public void close() {


    }

}
