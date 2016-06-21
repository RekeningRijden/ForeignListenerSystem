/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package belgie;

import integration.ApiConnection;
import integration.domain.DeleteStolenRequest;
import integration.domain.Position;
import integration.domain.StolenRequest;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Marijn
 */
@Singleton
@Startup
@Stateless
public class StolenCarsListener implements MessageListener {

    private ApiConnection apiConnection = new ApiConnection("118c7fe4b4fa71b9d4a37f39666411f2");

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @PostConstruct
    public void init() {
        try {
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Start listening " + getComputerName());
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.24.130:61616");
            RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
            policy.setInitialRedeliveryDelay(30000);
            policy.setMaximumRedeliveries(10);
            connection = connectionFactory.createConnection();
            connection.setClientID("Portugal-" + getComputerName());
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Topic topic = session.createTopic("topicBelgiumStolenCars");
            consumer = session.createDurableSubscriber(topic, "Portugal-" + getComputerName());
            consumer.setMessageListener(this);
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Started listening " + getComputerName());
        } catch (JMSException ex) {
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    public void closeConnection() {
        try {
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Received message: " + textMessage.getText());
            JSONObject json = new JSONObject(textMessage.getText());
            double lati = (double) json.get("lastLocationLatitude");
            double longi = (double) json.get("lastLocationLongitude");
            boolean isStolen = (boolean) json.get("isStolen");
            String licenceplate = (String) json.get("licenceplate");
            if (isStolen) {
                StolenRequest stolenRequest = new StolenRequest();
                stolenRequest.setLicencePlate(licenceplate);
                stolenRequest.setCarIdentifier(licenceplate);
                Position position = new Position();
                position.setLatitude(lati);
                position.setLongitude(longi);
                position.setDate(new Date());
                stolenRequest.setLastPosition(position);
                if (apiConnection.addStolenCar(stolenRequest)) {
                    message.acknowledge();
                    Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Stolen car successful added");
                } else {
                    Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Stolen car add failed");
                }
            } else {
                DeleteStolenRequest deleteStolenRequest = new DeleteStolenRequest();
                deleteStolenRequest.setCarIdentifier(licenceplate);
                if (apiConnection.removeStolenCar(deleteStolenRequest)) {
                    message.acknowledge();
                    Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Stolen car successful deleted");
                } else {
                    Logger.getLogger(StolenCarsListener.class.getName()).log(Level.INFO, "Stolen car delete failed");
                }
            }
        } catch (JMSException ex) {
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(StolenCarsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            return env.get("HOSTNAME");
        } else {
            return "Unknown Computer";
        }
    }
}
