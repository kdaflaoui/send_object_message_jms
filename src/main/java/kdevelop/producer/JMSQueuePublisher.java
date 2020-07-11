package kdevelop.producer;

import kdevelop.entities.Product;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSQueuePublisher  {
    String url = "tcp://localhost:61616";
    String distinationQueue = "kdevelop.queue";
    ConnectionFactory connectionFactory = null;
    Connection connection=null;
    Session session = null;
    Queue queue = null;
    MessageProducer messageProducer = null;

    public static void main(String[] args) throws Exception {
        JMSQueuePublisher jmsQueuePublisher = new JMSQueuePublisher();
        jmsQueuePublisher.initializeConnection();
        jmsQueuePublisher.sendMessageOnQueue();
    }

    //inisialiser la connection
    public void initializeConnection() throws Exception{
        connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue(distinationQueue);
        messageProducer = session.createProducer(queue);
        connection.start();
    }

    //Produire le message
    public void sendMessageOnQueue() throws JMSException {
        //String[] value = s.split("\\s+");
        Product product = new Product();
        product.setId(1);
        product.setNameProduct("Samsung");
        product.setPrice(500.00);
        product.setQuantity(10);
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(product);
        messageProducer.send(objectMessage);
        System.out.println("The message has been sent");
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
