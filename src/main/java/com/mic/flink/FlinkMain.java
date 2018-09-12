package com.mic.flink;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.hadoop.mapreduce.HadoopInputFormat;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FlinkMain {
    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(FlinkMain.class);

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        EsInputFormat<Text, LinkedMapWritable> kvEsInputFormat = new EsInputFormat<> ();

        HadoopInputFormat<Text, LinkedMapWritable> hadoopInputFormat =
                new HadoopInputFormat<>(kvEsInputFormat, Text.class, LinkedMapWritable.class);

        Configuration configuration = hadoopInputFormat.getConfiguration();
        configuration.set("es.resource", "flink-1/flink_t");
        configuration.set("es.query", "?q=*");
        configuration.set("es.nodes", "localhost");
        configuration.set("es.port", "9200");

        DataSet<Tuple2<Text, LinkedMapWritable>> input = env.createInput(hadoopInputFormat);

        List<Tuple2<Text, LinkedMapWritable>> collect = input.collect();
        collect.forEach(e -> logger.info(e.toString()));
    }
}
