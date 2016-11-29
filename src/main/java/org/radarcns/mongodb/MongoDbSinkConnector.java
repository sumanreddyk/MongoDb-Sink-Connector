package org.radarcns.mongodb;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.radarcns.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Francesco Nobilia on 28/11/2016.
 */
public class MongoDbSinkConnector extends SinkConnector {

    private static final Logger log = LoggerFactory.getLogger(MongoDbSinkConnector.class);

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USR = "username";
    public static final String PWD = "password";
    public static final String DB = "database";
    public static final String BATCH_SIZE = "batch.size";
    public static final String COLL_DOUBLE_SINGLETON = "double.singleton";
    public static final String COLL_DOUBLE_ARRAY = "double.array";
    public static final String MUST_HAVE = "must.have";

    Map<String, String> connectorConfig;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        connectorConfig = new HashMap<>();

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        connectorConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        connectorConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());

        connectorConfig.put(HOST,props.get(HOST));
        connectorConfig.put(PORT,props.get(PORT));

        connectorConfig.put(USR,props.get(USR));
        connectorConfig.put(PWD,props.get(PWD));
        connectorConfig.put(DB,props.get(DB));

        connectorConfig.put(COLL_DOUBLE_SINGLETON,props.get(COLL_DOUBLE_SINGLETON));
        connectorConfig.put(COLL_DOUBLE_ARRAY,props.get(COLL_DOUBLE_ARRAY));

        connectorConfig.put(BATCH_SIZE,props.get(BATCH_SIZE));

        Set<String> topicList = Utility.getTopicSet(props.get(TOPICS_CONFIG));
        topicList.stream().forEach(topic -> connectorConfig.put(topic,props.get(topic)));
        connectorConfig.put(TOPICS_CONFIG,props.get(TOPICS_CONFIG));

        connectorConfig.put(MUST_HAVE,Utility.keyListToString(connectorConfig));

        log.info(Utility.convertConfigToString(connectorConfig));
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MongoDbSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            configs.add(connectorConfig);
        }

        log.info("At most {} will be started",maxTasks);

        return configs;
    }

    @Override
    public void stop() {
        log.debug("Stop");
        // Nothing to do since it has no background monitoring.
    }

    @Override
    public ConfigDef config() {
        return null;
    }
}
