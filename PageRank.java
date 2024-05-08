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
				tempAdjacency = new double[len][len];
				stringAdjacency = new String[len];
			}

			stringAdjacency[counter] = line;
			counter++;
		}

		for(int i =0; i<counter; i++){
			String[] StringtempAdjacency = stringAdjacency[i].split(":");
			int onesInThisRow = 0;
			stringAdjacency[i] = new String("");
			
			for(int j=0; j< StringtempAdjacency.length; j++){
				if(StringtempAdjacency[j].equals("1"))
					onesInThisRow++;
				tempAdjacency[i][j] = Double.parseDouble(StringtempAdjacency[j]);
				tempAdjacency[i][j] = tempAdjacency[i][j]/counter;
			}

			for(int j=0; j<  StringtempAdjacency.length; j++){
				tempAdjacency[i][j] = tempAdjacency[i][j]/(double)onesInThisRow;
				stringAdjacency[i] = new String(
					stringAdjacency[i].concat(tempAdjacency[i][j]+":"));
			}
		}

		for(int i = 0; i< counter; i++){
			out.writeBytes((i+1) + "\t" + stringAdjacency[i]);
			if(i != (counter-1))
				out.writeBytes("\n");
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
                                tempAdjacency = new double[len][len];
                                stringAdjacency = new String[len];
                        }

                        stringAdjacency[counter] = line;
                        counter++;
                }
		
		
                for(int i =0; i<counter; i++){
                        String[] StringtempAdjacency = stringAdjacency[i].split(":");
                        for(int j=0; j< StringtempAdjacency.length; j++){
                                tempAdjacency[i][j] = Double.parseDouble(StringtempAdjacency[j]);
                        }
                }
	

		double[] sum = new double[counter];
                for(int i =0; i<counter; i++){
                        for(int j=0; j< counter; j++){
                        	sum[i] += tempAdjacency[i][j];
			}
                        
                }
                for(int i = 0; i< counter; i++)
                        out.writeBytes((i+1) + "\t" +sum[i] + "\n");
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
			
			String[] line = value.toString().split("\\s+");

			IntWritable keyNode = new IntWritable(Integer.parseInt(line[0]));

			context.write(keyNode, new Text(line[1]));

			String[] inLinks = line[1].split(":");
			for(int i =0; i<inLinks.length;i++)
				context.write(new IntWritable(i+1), new Text(inLinks[i]));
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
				if(nodeDistance.toString().contains(":"))
					adjList = nodeDistance.toString().split(":");
				else
					sum += Double.parseDouble(nodeDistance.toString());
			}
			int count = 0;
			for(String outlinks : adjList){
				if(!outlinks.equals("0.0"))
					count++;
			}
			
			String [] newAdjList = new String[5];
			
			for(int i=0; i< adjList.length; i++){
				if(adjList[i].equals("0.0"))
					newAdjList[i] = "0.0";
				else{
					double newRank = sum/count;
					newAdjList[i] = new String(newRank + "");

				}
			}

			String finalAdjList = new String("");
			for(String aList : newAdjList)
				finalAdjList = new String(finalAdjList.concat(aList.concat(":")));
				
			for(int i = 0; i< adjList.length; i++){
				if((Double.parseDouble(adjList[i])
				  - Double.parseDouble(newAdjList[i])) > 0.0001){
					counters++;
					break;
				}
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
			Configuration conf = new Configuration();
			Job job = Job.getInstance(conf);
				
			FileSystem fs = FileSystem.get(conf);
			Path tmppath = new Path(outputPath+roundNumber);
			if(fs.exists(tmppath))
				fs.delete(tmppath, true);

			counters = 0;
			myMapReduceTask(job, inputPath, outputPath+roundNumber);
			inputPath = outputPath + roundNumber + "/part-r-00000";
			roundNumber++;	
			
		}while(counters >0);
		lastCalculate(outputPath+(roundNumber-1) +"/part-r-00000",finalPath); 
	}
}	
