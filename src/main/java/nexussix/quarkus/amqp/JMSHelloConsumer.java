package nexussix.quarkus.amqp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

//Connects to an AMQ Broker over AMQP and recieves messages from the "hello" queue  

@ApplicationScoped
public class JMSHelloConsumer implements Runnable {

    @Inject
    ConnectionFactory connectionFactory;

    private final ExecutorService scheduler = Executors.newSingleThreadExecutor();

    private volatile String lastPrice;

    public String getLastPrice() {
        return lastPrice;
    }

    void onStart(@Observes StartupEvent ev) {
        System.out.println("On Start");
        scheduler.submit(this);
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        System.out.println("run");
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("hello"));
            System.out.println("Creating Consumer");
            while (true) {   
                System.out.println("Consumer listening");             
                Message message = consumer.receive();
                if (message == null) return;
                System.out.println("Message recieved ->" + message.getJMSMessageID());
                lastPrice = message.getBody(String.class);
                System.out.println("Message content ->" + lastPrice);
            }
        } catch (JMSException e) {
            System.out.println(e.getStackTrace());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}