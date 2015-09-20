package com.tj;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

/**
 * @Author: jie.tang
 * @Date: 2015-09-20 下午1:41
 * @Desc:
 */
public class CanalUtils {

    public static void printEntry(CanalEntry.Entry entry,String tableName){
        CanalEntry.EntryType entryType = entry.getEntryType();
        if (entryType == CanalEntry.EntryType.ROWDATA) {
            try {
                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                CanalEntry.EventType eventType = rowChange.getEventType();
                List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();

                if(eventType.equals(CanalEntry.EventType.INSERT)){
                    System.out.println("==========insert begin===========");
                    for (int i = 0; i < rowDatasList.size(); i++) {
                        printColumn(rowDatasList.get(i).getAfterColumnsList());
                    }
                    System.out.println("==========insert end===========");
                }else if (eventType.equals(CanalEntry.EventType.DELETE)) {
                    System.out.println("==========delete begin===========");
                    for (int i = 0; i < rowDatasList.size(); i++) {
                        printColumn(rowDatasList.get(i).getBeforeColumnsList());
                    }
                    System.out.println("==========delete end===========");
                }else if (eventType.equals(CanalEntry.EventType.UPDATE)) {
                    System.out.println("==========update begin===========");
                    for (int i = 0; i < rowDatasList.size(); i++) {
                        printColumn(rowDatasList.get(i).getBeforeColumnsList());
                    }
                    System.out.println("==========update before===========");
                    for (int i = 0; i < rowDatasList.size(); i++) {
                        printColumn(rowDatasList.get(i).getAfterColumnsList());
                    }
                    System.out.println("==========update end===========");
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

    }

    public static void printColumn(List<CanalEntry.Column> columnList){
        for (int i = 0; i < columnList.size(); i++) {
            CanalEntry.Column column = columnList.get(i);
            System.out.println(column.getName()+","+column.getValue());
        }
    }


}
