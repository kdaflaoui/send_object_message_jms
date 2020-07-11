package kdevelop.consumer;

import kdevelop.entities.Product;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.Collections;

public class JMSQueueSubscriber implements MessageListener {

    String url = "tcp://localhost:61616";
    String distinationQueue = "kdevelop.queue";
    ConnectionFactory connectionFactory = null;
    Connection connection=null;
    Session session = null;
    Queue queue = null;
    MessageConsumer messageConsumer = null;

    public static void main(String[] args) throws Exception {
        JMSQueueSubscriber jmsQueueSubscriber = new JMSQueueSubscriber();
        jmsQueueSubscriber.initializeConnection();
    }

    //inisialiser la connection
    public void initializeConnection() throws Exception{
        connectionFactory = new ActiveMQConnectionFactory(url);
        ((ActiveMQConnectionFactory) connectionFactory).setTrustedPackages(Collections.singletonList("kdevelop"));
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue(distinationQueue);
        messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener(this);
        connection.start();
    }


    @Override
    public synchronized void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Product product = (Product) objectMessage.getObject();
            if(product != null){
                System.out.println(product.toString());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception{
        try {
            connection.close();
        }finally {
            if(connection !=null){
                try{
                    connection.close();
                }catch (JMSException  e){
                    System.out.println("La connection ne peut pas etre fermée : "+ e);
                }

            }
        }
    }
}

