package nexussix.quarkus.amqp;


import org.apache.camel.builder.RouteBuilder;

// Creates a Camel Route that connects to an AMQ Broker over AMQP and listens to messages on the hello queue

public class CamelRoute extends RouteBuilder{
    @Override
    public void configure() throws Exception {
                                        
        from("amqp:topic:prices")
        .to("log:?level=INFO&showBody=true");        
    }

    
}