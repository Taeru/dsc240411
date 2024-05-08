import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;



public class PageRank{
	private static int roundNumber = 0;
	private static int counters;
	public static void preprocess(String inputFileName, String outputFileName)
	throws IOException{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		Path inFile = new Path(inputFileName);
		Path outFile = new Path(outputFileName);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(fs.open(inFile)));
		FSDataOutputStream out = fs.create(outFile);

		double [][] tempAdjacency = null;
		String [] stringAdjacency = null;
		int counter = 0;	

		try{
		String line;

		while((line = in.readLine()) != null){
			line = (line.split("\\s+"))[1];
			int len = line.split(":").length;
			if(tempAdjacency == null || stringAdjacency == null){
				



			}



		}

		for(int i =0; i<counter; i++){
			String[] StringtempAdjacency = stringAdjacency[i].split(":");
			int onesInThisRow = 0;
			stringAdjacency[i] = new String("");
			
			for(int j=0; j< StringtempAdjacency.length; j++){
				if(StringtempAdjacency[j].equals("1"))
		


			}

			for(int j=0; j<  StringtempAdjacency.length; j++){
		



			}
		}

		for(int i = 0; i< counter; i++){
	


		}
		}catch(Exception e){
		}finally{
		in.close();
		out.close();
		}
	}
	public static void lastCalculate(String inputFileName, String outputFileName)
        throws IOException{
                Configuration conf = new Configuration();
                FileSystem fs = FileSystem.get(conf);

                Path inFile = new Path(inputFileName);
                Path outFile = new Path(outputFileName);

                BufferedReader in = new BufferedReader(new InputStreamReader(fs.open(inFile)));
                FSDataOutputStream out = fs.create(outFile);

                double [][] tempAdjacency = null;
                String [] stringAdjacency = null;
                int counter = 0;

                try{
                String line;

                while((line = in.readLine()) != null){
                        line = (line.split("\\s+"))[1];
                        int len = line.split(":").length;
                        if(tempAdjacency == null || stringAdjacency == null){
        
        

                        }



                }
		
		
                for(int i =0; i<counter; i++){
                       

 
			for(int j=0; j< StringtempAdjacency.length; j++){


                        
                        }
                }
	



                for(int i =0; i<counter; i++){
                        for(int j=0; j< counter; j++){



			}
                        
                }
                for(int i = 0; i< counter; i++)
                        out.writeBytes(i + "\t" +sum[i] + "\n");
                }catch(Exception e){
                }finally{
                in.close();
                out.close();
                }
        }


	public static class MyMapper
		extends Mapper<Object, Text, IntWritable, Text>{
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException{
			




			for(int i =0; i<inLinks.length;i++)
			


			}
		}
	
	public static class MyReducer
		extends Reducer<IntWritable, Text, IntWritable, Text>{
		protected void reduce(IntWritable key, Iterable<Text> nodeDistances,
			Context context)
			throws IOException, InterruptedException{
			double sum = 0.0;
			String[] adjList = new String[5];
			for (Text nodeDistance : nodeDistances){
			




			}
			int count = 0;
			for(String outlinks : adjList){


			}
			
			String [] newAdjList = new String[5];
			
			for(int i=0; i< adjList.length; i++){
			





			}

			String finalAdjList = new String("");
			for(String aList : newAdjList)
			


	
			for(int i = 0; i< adjList.length; i++){




			}
			context.write(key, new Text(finalAdjList));
		}
	}

	public static void myMapReduceTask(Job job, String inputPath, String outputPath)
		throws Exception{
		Configuration conf = new Configuration();
		
		job.setJarByClass(PageRank.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);		

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job,new Path(inputPath));
		FileOutputFormat.setOutputPath(job,new Path(outputPath));

		job.waitForCompletion(true);
	}

	public static void main(String[] args) throws Exception	{
		
		String inputPath = args[0];

		String preprocessPath = "/input/preprocess.txt";
		String finalPath = args[1];
		String outputPath ="/outputs/output";
		preprocess(inputPath, preprocessPath);
		inputPath = preprocessPath;
		do{
		



		
	
		}while(counters >0);
		lastCalculate(outputPath+(roundNumber-1) +"/part-r-00000",finalPath); 
	}
}	
