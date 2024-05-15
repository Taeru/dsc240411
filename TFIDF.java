import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;



public class TFIDF{
	private static int roundNumber = 0;
	private static int counters;

	public static class MyMapper1
		extends Mapper<Object, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException{
			String line = value.toString();
			String DocID = line.substring(0, value.toString().indexOf("\f"));
			String value_raw = line.substring(value.toString().indexOf("\f")+1);
			String match = "[^\uAC00-\uD7A3xfea-zA-Z\\s]";
			StringTokenizer st = new StringTokenizer(line);
			StringBuilder docValueList = new StringBuilder();
			while(st.hasMoreToken()){
				word.set(st.nextToken().toLowerCase());
				docValueList.append(word + ":" + DocID+ " ");
				context.wrtie(new Text(docValueList.toString()),one);
			}
		}
	public static class MyReducer1
		extends Reducer<Text, IntWritable, Text, IntWritable>{
		
		private IntWritable sumWritable = new IntWritable();
		protected void reduce(IntWritable key, Iterable<Text> nodeDistances,
			Context context)
			throws IOException, InterruptedException{
			
			int sum = 0;
			for(IntWritable val : values){
				sum += val.get();
			}
			sumWritable.set(sum);
			context.write(key, sumWritable);

			
		}
	}
	public static void myMapReduceTask1(Job job, String inputPath, String outputPath)
		throws Exception{
		Configuration conf = new Configuration();
		
		job.setJarByClass(TFIDF.class);
		job.setMapperClass(MyMapper1.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);		

		job.setReducerClass(MyReducer1.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job,new Path(inputPath));
		FileOutputFormat.setOutputPath(job,new Path(outputPath));

		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception	{
		
		String inputPath = args[0];
		String outputPath ="/outputs/output";

		String finalPath = args[1];
		inputPath = preprocessPath;
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		outputPath = finalPath;
		myMapReduceTask1(job, inputPath, outputPath);


	}
}	
