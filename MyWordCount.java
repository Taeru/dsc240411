import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;



public class MyWordCount{
	public static class MyMapper
		extends Mapper<Object, Text, Text, IntWritable>{
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException{
			
		}
	}
	
	public static class MyReducer
		extends Reducer<Text, IntWritable, Text, IntWritable>{

		protected void reduce(Text key, Iterable<IntWritable> values,
			Context context)
			throws IOException, InterruptedException{
			
		}
	}

	public static void main(String[] args) throws Exception{

	}
}	
