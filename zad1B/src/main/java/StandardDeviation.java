import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardDeviation {
    static Logger log = Logger.getLogger(StandardDeviation.class.getName());

    public static class JsonDataMapper extends Mapper<Object, Text, Text, DoubleWritable> {
        private static JsonParser jsonParser = new JsonParser();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            Gson gson = new Gson();

            FingerSample fingerSample = gson.fromJson(String.valueOf(value), FingerSample.class);
            log.info("SOCHACKI, mapper key".concat(key.toString()));
            log.info("FINGER SAMPLE: ".concat(fingerSample.toString()));

            String newKeyPrefix = fingerSample.side + "-" + fingerSample.series + "-";

            Map<String, Double> fingerValueMap = fingerSample.features2D.getFingerValuesMap();

            for (String fingerKey : fingerValueMap.keySet()) {
                context.write(new Text(newKeyPrefix + fingerKey), new DoubleWritable(fingerValueMap.get(fingerKey)));
            }
        }
    }

    public static class FingerDataReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        public void reduce(Text key, Iterable<DoubleWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            List<Double> fingerValues = new ArrayList<>();
            for (DoubleWritable doubleWritable : values) {
                fingerValues.add(doubleWritable.get());
            }

            Double standardDeviation = StandardDeviationCalculator.calculateStandardDeviation(fingerValues);

            log.log(Level.INFO, "REDUCER_KEY: ".concat(key.toString()).concat(" REDUCER_VALUE: ".concat(standardDeviation.toString())));
            context.write(key, new DoubleWritable(standardDeviation));
        }
    }

    public static void main(String[] args) throws Exception {
        log.info("Standard deviation finger length calculation");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "standard deviation finger length calculation");
        job.setJarByClass(StandardDeviation.class);
        job.setMapperClass(StandardDeviation.JsonDataMapper.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setReducerClass(FingerDataReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
