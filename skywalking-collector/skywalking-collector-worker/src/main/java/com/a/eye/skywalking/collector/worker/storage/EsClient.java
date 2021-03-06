package com.a.eye.skywalking.collector.worker.storage;

import com.a.eye.skywalking.collector.worker.config.EsConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pengys5
 */
public enum EsClient {
    INSTANCE;

    private Logger logger = LogManager.getFormatterLogger(EsClient.class);

    private Client client;

    public void boot() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", EsConfig.Es.Cluster.name)
                .put("client.transport.sniff", EsConfig.Es.Cluster.Transport.sniffer)
                .build();

        client = new PreBuiltTransportClient(settings);

        List<AddressPairs> pairsList = parseClusterNodes(EsConfig.Es.Cluster.nodes);
        for (AddressPairs pairs : pairsList) {
            ((PreBuiltTransportClient) client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(pairs.ip), pairs.port));
        }
    }

    public Client getClient() {
        return client;
    }

    public void indexRefresh(String... indexName) {
        RefreshResponse response = client.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
        if (response.getShardFailures().length == response.getTotalShards()) {
            logger.error("All elasticsearch shard index refresh failure, reason: %s", response.getShardFailures());
        } else if (response.getShardFailures().length > 0) {
            logger.error("In parts of elasticsearch shard index refresh failure, reason: %s", response.getShardFailures());
        }
        logger.info("elasticsearch index refresh success");
    }

    private List<AddressPairs> parseClusterNodes(String nodes) {
        List<AddressPairs> pairsList = new ArrayList<>();
        logger.info("es nodes: %s", nodes);
        String[] nodesSplit = nodes.split(",");
        for (int i = 0; i < nodesSplit.length; i++) {
            String node = nodesSplit[i];
            String ip = node.split(":")[0];
            String port = node.split(":")[1];
            pairsList.add(new AddressPairs(ip, Integer.valueOf(port)));
        }

        return pairsList;
    }

    class AddressPairs {
        private String ip;
        private Integer port;

        public AddressPairs(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
