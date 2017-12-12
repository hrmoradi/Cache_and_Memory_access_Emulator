package Arch;

import java.io.*;
import java.util.LinkedList;


public class Main {
    public static int counter =0;

    public static void main(String[] args) {

        System.out.print("     *****Hamidreza Moradi assignment*****     \n");
        System.out.print("     Main CLASS \n");
        System.out.print("          Enter Config file name: ");

        InputStreamReader inputCommand = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputCommand);
        String command=null;
        LinkedList<LevelDetail> Levels = new LinkedList<LevelDetail>();
        int levelCount = 0;
        try {
            command = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("\n          Error     MainAPP:     try:     command = reader.readLine()" + e.getMessage());
        }
        //command="c:\\mem3.conf.txt";
        System.out.println("          config file address is: "+command);

        try {
            File file = new File(command);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            int lineNumber = 0;

            String level="";
            String lines="";
            String way = "";
            String size="";
            String hitTime="";
            String writePolicy="";
            String allocationPolicy="";

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber = lineNumber + 1;
                //System.out.println("          "+line);
                if (line.contains("Level")) {
                    level = line.split(":")[1];
                    //System.out.println("          -"+level);
                    levelCount=levelCount+1;
                }
                if (line.contains("Line")) {
                    lines = line.split(":")[1];
                }
                if (line.contains("Way")) {
                    way = line.split(":")[1];
                }
                if (line.contains("Size")) {
                    size = line.split(":")[1];
                    size = size.split("K")[0];
                    if (way.equals("Full")){
                        way=Integer.toString(Integer.parseInt(size)/Integer.parseInt(lines));
                    }
                }
                if (line.contains("HitTime")) {
                    hitTime = line.split(":")[1];
                    if (level.equals("Main")){
                        //System.out.println("          =Main");
                        LevelDetail temp = new LevelDetail();
                        temp.level=level;
                        temp.lines="-1";
                        temp.way="-1";
                        temp.size="-1";
                        temp.hitTime=hitTime;
                        temp.writePolicy="-1";
                        temp.allocationPolicy="-1";
                        Levels.add(temp);
                    }
                }
                if (line.contains("WritePolicy")) {
                    writePolicy = line.split(":")[1];
                }
                if (line.contains("AllocationPolicy")) {
                    allocationPolicy = line.split(":")[1];

                    LevelDetail temp = new LevelDetail();
                    temp.level=level;
                    temp.lines=lines;
                    temp.way=way;
                    temp.size=size;
                    temp.hitTime=hitTime;
                    temp.writePolicy=writePolicy;
                    temp.allocationPolicy=allocationPolicy;
                    Levels.add(temp);
                }



            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("\n          Error in reading the input file" + e.getMessage());
        }
        for (LevelDetail e : Levels){
            //System.out.println("          LevelDetail e : Levels");
            System.out.println("          "+e.level+" "+e.lines+" "+e.way+" "+e.size+" "+e.hitTime+" "+e.writePolicy+" "+e.allocationPolicy);
        }
        CacheBuilder temp;
        CacheBuilder pointer2next=null;
        System.out.println();
        for (int i=levelCount-1;i>=0;i--){
            //CacheBuilder(String setLevel,int line,int way,int size,String setWritePolicy,String setAllocPolicy)
            System.out.println(("          "+Levels.get(i).level+" "+Integer.parseInt(Levels.get(i).lines)+" "+Integer.parseInt(Levels.get(i).way)+" "+Integer.parseInt(Levels.get(i).size)+" "+Levels.get(i).writePolicy+" "+Levels.get(i).allocationPolicy));
            //System.out.println("          "+Levels.get(i).level);
            temp = new CacheBuilder(Levels.get(i).level,
                    Integer.parseInt(Levels.get(i).lines),
                    Integer.parseInt(Levels.get(i).way),
                    Integer.parseInt(Levels.get(i).size),
                    Levels.get(i).writePolicy,
                    Levels.get(i).allocationPolicy);
            temp.NextLevel=pointer2next;
            pointer2next= temp;
        }
        //System.out.println("          "+pointer2next.level+" "+pointer2next.NextLevel.level+" "+pointer2next.NextLevel.NextLevel.level+" ");
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.print("\n          ==================================================================\n          Enter input file name: ");
        try {
            command = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error     MainAPP:     try:     command = reader.readLine()" + e.getMessage());
        }
        //command="c:\\access3.in.txt";
        System.out.print("          input file address is: "+command+"\n");

        try {
            File file = new File(command);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;

            /**/
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println("          "+line);
                if (line.split(" ")[0].equals("ld")){
                    //System.out.println("          -"+line.split(" ")[0]+" "+Long.parseLong(line.split(" ")[1]));
                    counter = counter +1;
                    pointer2next.Load(Long.parseLong(line.split(" ")[1]));
                }
                if (line.split(" ")[0].equals("st")){
                    //System.out.println("          -"+line.split(" ")[0]+" "+Long.parseLong(line.split(" ")[1]));
                    counter = counter +1;
                    pointer2next.Store(Long.parseLong(line.split(" ")[1]));
                }
            }
            /**/
        }catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error     MainAPP:     try:     command = reader.readLine()" + e.getMessage());
        }
        System.out.print("\n\n     Results:\n");
        int i =0;
        int sum = 0;
        while (pointer2next!=null){
            System.out.println("          Level: "+pointer2next.level );//+" "+ Levels.get(i).level);
            System.out.println("          Access: "+(pointer2next.GetHit()+pointer2next.GetMiss()) );//+" "+ Levels.get(i).level);
            System.out.println("          Hit: "+pointer2next.GetHit());// +" \n           HitTime:"+ Levels.get(i).hitTime);
            System.out.println("          Miss: "+pointer2next.GetMiss());
            sum = ( sum + ( pointer2next.GetHit()+ pointer2next.GetMiss() ) * (Integer.parseInt(Levels.get(i).hitTime) ) );
            i=i+1;
            pointer2next=pointer2next.NextLevel;
        }
        System.out.println("          TotalTime: "+sum );
    }
}




