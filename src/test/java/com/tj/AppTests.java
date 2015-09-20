package com.tj;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class AppTests {


    @Before
    public void setup() {

    }

    @Test
    public void simple() throws Exception {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.0.14",11111),"example","","");
        int batchSize = 100;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();

            int totalEntryCount = 1200;
            while (emptyCount < totalEntryCount) {
                Message message = connector.getWithoutAck(batchSize);
                long messageId = message.getId();
                int entrySize = message.getEntries().size();


                for (int i = 0; i < entrySize; i++) {
                    CanalEntry.Entry entry = message.getEntries().get(i);
                    CanalUtils.printEntry(entry,"");
                }


                connector.ack(messageId);
            }
        } catch (CanalClientException e) {
            e.printStackTrace();
        }finally {
            connector.disconnect();
        }
    }
}
