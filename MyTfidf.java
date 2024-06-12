import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;



public class Tfidf{
	static int D=0;
	public static class MyMapper
		extends Mapper<Object, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException{
			String doc_name = ((FileSplit)context.getInputSplit()).getPath().getName();
			String line = value.toString();
			String match = "[^\uAC00-\uD7A3xfea-zA-Z\\s]";
			line = line.replaceAll(match, "");
			
			StringTokenizer st = new StringTokenizer(line);
			while(st.hasMoreTokens()){
				word.set(st.nextToken().toLowerCase() + "@" + doc_name);
				context.write(word, one);
			}
		}
	}
	
	public static class MyReducer
		extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable sumWritable = new IntWritable();
			
		protected void reduce(Text key, Iterable<IntWritable> values,
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

	public static class SecondMapper
		extends Mapper<LongWritable, Text, Text, Text>{
		protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException{

			String line = value.toString();
			String front = line.split("\t")[0];

			String count = line.split("\t")[1];
			String word = front.split("@")[0];
			String doc_name = front.split("@")[1];
			

			context.write(new Text(word), new Text(doc_name + "=" + count));			
			
			
		}
	}

	public static class SecondReducer
		extends Reducer<Text, Text, Text, Text>{
		
		protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException{
			Map<String, Integer> doc_and_count = new HashMap<String, Integer>();

			
			int d = 0;			
			
			for(Text value: values){
				String doc_name = value.toString().split("=")[0];
				Integer count = Integer.parseInt(value.toString().split("=")[1]);
				doc_and_count.put(doc_name, count);
				d++;
			}
			double idf = Math.log((double)D/d);
			
			for(String doc_name: doc_and_count.keySet()){
				double tf = doc_and_count.get(doc_name);
				double tfidf = tf*idf;
				
				context.write(new Text(key + "@" + doc_name), new Text(Double.toString(tfidf)));
			}

		}
	}

	public static void main(String[] args) throws Exception{
		String path = System.getProperty("user.dir") + args[0];
		File f = new File(path);
		File[] files = f.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile())
				D++;
		}	

		Configuration conf = new Configuration();

		Path firstJobPath = new Path("/firstJob");
		
		Job job = Job.getInstance(conf, "FirstJob");
		job.setJarByClass(Tfidf.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, firstJobPath);
		job.waitForCompletion(true);
	
		Configuration conf2 = new Configuration();
		Job job2 = Job.getInstance(conf2, "secondJob");
		job2.setJarByClass(Tfidf.class);
                job2.setMapperClass(SecondMapper.class);
                job2.setReducerClass(SecondReducer.class);
                job2.setOutputKeyClass(Text.class);
                job2.setOutputValueClass(Text.class);
                job2.setInputFormatClass(TextInputFormat.class);
                job2.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job2, firstJobPath);
                FileOutputFormat.setOutputPath(job2, new Path(args[1]));
                job2.waitForCompletion(true);
		
	}
}	
