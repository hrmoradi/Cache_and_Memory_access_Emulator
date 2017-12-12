package Arch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.math.*;

public class CacheBuilder {

    private Hashtable<String,List<CacheCell>> cache = new Hashtable<String,List<CacheCell>>();
    public  String level;
    private double lineBit;
    private double indexBit;
    private double tagBit;
    private String writePolicy;
    private String allocPolicy;
    private int way;
    private int hit =0;
    private int miss=0;
    private int access=0;
    public CacheBuilder NextLevel;

    public int GetMiss(){return (miss);}
    public int GetHit(){return (hit);}
    public static void Main() {
    }

    public CacheBuilder(String setLevel,int line,int way,int size,String setWritePolicy,String setAllocPolicy){
        this.level=setLevel;
        this.lineBit = Math.log( (double)line)/Math.log(2);
        size = size*1024;//32k to 32*1024
        int indexCount = size/(way*line);
        this.indexBit = Math.log( (double)indexCount)/Math.log(2);
        this.tagBit = 32 - indexBit-lineBit;
        this.writePolicy=setWritePolicy;
        this.allocPolicy=setAllocPolicy;
        this.way = way;
        System.out.println("               initialization: this.level: "+this.level+" indexcount: " +indexCount+" size:"+size+" line: "+line+" this.linebit:"+this.lineBit+" this.indexBit:"+this.indexBit+" this.tagBit:"+this.tagBit+" this.way:"+this.way);
    }

    public void Store(Long address10) {
        if (this.level.equals("Main")) {
            this.hit=this.hit+1;
            System.out.println("               ["+this.level+"] Store "+address10+" : HIT");
            return;
        }

        if (this.writePolicy.equals("WriteThrough") & this.allocPolicy.equals("NoWriteAllocate") ) { /////////////////////////////////////////////// write thorugh - no write allocate

            this.access = this.access + 1;
            if (this.cache.containsKey(this.Index(address10))) {
                List<CacheCell> cacheRow = this.cache.get(Index(address10));
                for (CacheCell E : cacheRow) {
                    if (E.tag.equals(Tag(address10))) {
                        // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                        this.hit = this.hit + 1;
                        System.out.println("               ["+this.level+"] Store "+address10+" : HIT   wt-nw");
                                NextLevel.Store(address10);
                                E.LRU = Main.counter;
                                E.addressInCell=address10;
                                 ////1.
                                return;


                    }

                }
                this.miss = this.miss + 1;
                System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wt-nw");
                NextLevel.Store(address10);
                return;
            }
            this.miss = this.miss + 1;
            System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wt-nw");
            NextLevel.Store(address10);
            return;

        }



        if (this.writePolicy.equals("WriteThrough") & this.allocPolicy.equals("WriteAllocate") ){ ///////////////////////////////////////////////////////////////////////////////// write thorough - write allocate
            this.access = this.access + 1;
            if (this.cache.containsKey(Index(address10))) {
                List<CacheCell> cacheRow = this.cache.get(Index(address10));
                for (CacheCell E : cacheRow) {
                    if (E.tag.equals(Tag(address10))) {
                        // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                        this.hit = this.hit + 1;
                        System.out.println("               ["+this.level+"] Store "+address10+" : HIT   wt-w");
                        //for (CacheCell E2 : cacheRow) {
                         //   if (E2.tag.equals(Tag(address10))) {
                                E.LRU = Main.counter;
                                NextLevel.Store(address10); //////2.
                                return;
                        //    }
                        //}

                    }
                }
                this.miss = this.miss + 1;
                System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wt-w");
                NextLevel.Store(address10);
                Long thinkLoaded = NextLevel.Load(address10);
                PutInThisLevel(thinkLoaded);

                return;
            }
            this.miss = this.miss + 1;
            System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wt-w");
            NextLevel.Store(address10); ////////////////////////////// with professor
            Long thinkLoaded = NextLevel.Load(address10);
            PutInThisLevel(thinkLoaded);

            return;
        }




        if (this.writePolicy.equals("WriteBack") & this.allocPolicy.equals("NoWriteAllocate") ) {
            ///////////////////////////////////////////////write back - no write allocate
                this.access = this.access + 1;
                if (this.cache.containsKey(Index(address10))) {
                    List<CacheCell> cacheRow = this.cache.get(Index(address10));
                    for (CacheCell E : cacheRow) {
                        if (E.tag.equals(Tag(address10))) {
                            // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                            this.hit = this.hit + 1;
                            System.out.println("               ["+this.level+"] Store "+address10+" : HIT   wb-nw");
                            //for (CacheCell E2 : cacheRow) {
                                //if (E2.tag.equals(Tag(address10))) {
                                    E.LRU = Main.counter;
                                    E.dirty = 1;
                                    E.addressInCell=address10;
                                    return;
                               // }
                            //}


                        }
                    }
                    System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wb-nw");
                    this.miss = this.miss + 1;
                    NextLevel.Store(address10);
                    return;
                }
                System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wb-nw");
                this.miss = this.miss + 1;
                NextLevel.Store(address10);
                return;
            }



        if (this.writePolicy.equals("WriteBack") & this.allocPolicy.equals("WriteAllocate") ) { //////////////////////////////////////////////////////////////////////////////// write back - write allocate
            this.access = this.access + 1;
            if (this.cache.containsKey(Index(address10))) {
                List<CacheCell> cacheRow = this.cache.get(Index(address10));
                for (CacheCell E : cacheRow) {
                    if (E.tag.equals(Tag(address10))) {
                        // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                        this.hit = this.hit + 1;
                        System.out.println("               [" + this.level + "] Store " + address10 + " : HIT   wb-w");
                        //for (CacheCell E2 : cacheRow) {
                        //    if (E2.tag.equals(Tag(address10))) {
                                E.LRU = Main.counter;
                                E.dirty = 1;
                                E.addressInCell = address10;
                                return;////////////3.
                         //   }
                        //}


                    }
                }

                this.miss = this.miss + 1;
                System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wb-w");
                Long thinkLoaded = NextLevel.Load(address10);
                PutInThisLevel(thinkLoaded);
                cacheRow = this.cache.get(Index(address10)); //defined List<CacheCell>
                for (CacheCell E : cacheRow) {
                    if (E.tag.equals(Tag(address10))) {
                        // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                        //this.hit = this.hit + 1; //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<===================================

                        //for (CacheCell E2 : cacheRow) {
                        //if (E2.tag.equals(Tag(address10))) {
                        E.LRU = Main.counter;
                        E.dirty = 1;
                        E.addressInCell=address10;
                        return;
                        // }
                        //}

                    }

                }

            }

            this.miss = this.miss + 1;
            System.out.println("               ["+this.level+"] Store "+address10+" : MISS   wb-w");
            Long thinkLoaded = NextLevel.Load(address10);
            PutInThisLevel(thinkLoaded);
            List<CacheCell> cacheRow = this.cache.get(Index(address10));
            for (CacheCell E : cacheRow) {
                if (E.tag.equals(Tag(address10))) {
                    // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                    //this.hit = this.hit + 1; //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<===================================

                    //for (CacheCell E2 : cacheRow) {
                        //if (E2.tag.equals(Tag(address10))) {
                            E.LRU = Main.counter;
                            E.dirty = 1;
                            E.addressInCell=address10;
                            return;
                       // }
                    //}

                }

            }

        }
    }




    public Long Load(Long address10){

        if (this.level.equals("Main")){
            System.out.println("               ["+this.level+"] read "+address10+" : HIT");
            this.hit=this.hit+1;
            return address10;
        }
        //System.out.println("               load "+this.level);
        this.access=this.access+1;
        //System.out.println("   "+this.Tag(address10));
        if (this.cache.containsKey(this.Index(address10))){
            List<CacheCell> cacheRow = this.cache.get(this.Index(address10));
            for (CacheCell E :   cacheRow){
                //System.out.println("   "+this.Tag(address10));
                //System.out.println("   "+E.tag);
                if (E.tag.equals(Tag(address10))){
                    //System.out.println("               load "+this.level+" Hit");
                    // Do Nothing, We had the data and just gave it to CPU |||||||||||||||| update LRU !!!!
                    this.hit=this.hit+1;

                    //for (CacheCell E2 :   cacheRow) {
                      //  if (E2.tag.equals(Tag(address10))) {
                            //System.out.println("               load "+this.level+" Hit not updated "+E2.LRU);
                            E.LRU=Main.counter;
                            System.out.println("               ["+this.level+"] read "+address10+"="+E.addressInCell+" : HIT"+" update tag: "+E.LRU);

                        return(E.addressInCell);
                        //}
//                    }


                }
            }
            System.out.println("               ["+this.level+"] read "+address10+" : MISS");
            this.miss=this.miss+1;
            address10 =NextLevel.Load(address10);
            this.PutInThisLevel(address10);
            return (address10);

        }
        System.out.println("               ["+this.level+"] read "+address10+" : MISS");
        this.miss=this.miss+1;
        address10 =NextLevel.Load(address10);
        this.PutInThisLevel(address10);
        return (address10);
    }



    private void PutInThisLevel(Long address10){
        //System.out.println("               PutInThisLevel: "+this.level+" address:"+address10);
        this.access=this.access+1;
        if (this.cache.containsKey(this.Index(address10))) {


            List<CacheCell> cacheRow = this.cache.get(this.Index(address10));

            if (cacheRow.size()<this.way){
                //System.out.println("contain key cacheRow.size()"+cacheRow.size());
                CacheCell thinkLoaded = new CacheCell();
                thinkLoaded.tag=this.Tag(address10);
                thinkLoaded.addressInCell=address10;
                thinkLoaded.LRU=Main.counter;
                //System.out.println("contain key cacheRow.size()"+cacheRow.size()+" "+thinkLoaded.LRU);
                cacheRow.add(thinkLoaded);


            }else if (cacheRow.size()==this.way){
                //System.out.println(" contain key = cacheRow.size()"+cacheRow.size());
                //================================================================== you remove one, should check if write back is !!!
                for (CacheCell E: cacheRow){

                    if (E.LRU==this.Smallest(cacheRow)){
                        //System.out.println("cacheRow.E.address() out"+String.format("%-32s", (E.tag+this.Index(address10))).replace(' ', '0'));
                        if(this.writePolicy.equals("WriteBack")){
                            if(E.dirty==1) {
                                Long Cell2address = E.addressInCell;//tag+this.Index(address10);
                                //Cell2address = String.format("%-32s", Cell2address).replace(' ', '0');
                                //Long addStr2long = Long.parseLong(Cell2address, 2);
                                NextLevel.Store(Cell2address);//addStr2long);
                                E.tag=this.Tag(address10);
                                E.addressInCell=address10;
                                E.LRU=Main.counter;
                                return;
                            }
                            E.tag=this.Tag(address10);
                            E.addressInCell=address10;
                            E.LRU=Main.counter;
                            return;
                        }else{
                            E.tag=this.Tag(address10);
                            E.addressInCell=address10;
                            E.LRU=Main.counter;
                            return;
                        }

                        //System.out.println("cacheRow.E.address() in "+String.format("%-32s", (E.tag+this.Index(address10))).replace(' ', '0'));
                        /*for (CacheCell E2 :   cacheRow) {
                            if (E2.tag.equals(Tag(address10))) {

                                //System.out.println("cacheRow.size()"+cacheRow.size()+" LRU "+E.LRU);

                            }
                        }*/

                    }
                }
            }else{
                System.out.println("level:"+ this.level+" Error, more column: "+cacheRow.size()+" than way:"+this.way+" address: "+address10);
                System.exit(0);
            }
        }else{
            List<CacheCell> LC = new ArrayList<CacheCell>();
            CacheCell thinkLoaded = new CacheCell();
            thinkLoaded.tag=this.Tag(address10);
            thinkLoaded.addressInCell=address10;
            thinkLoaded.LRU=Main.counter;
            //System.out.println("cacheRow.size() LC 1st "+LC.size()+thinkLoaded.LRU);
            LC.add(thinkLoaded);
            this.cache.put(Index(address10),LC);
        }

    }

    private String Binary(Long address10){
        String addressBinary = Long.toBinaryString(address10);
        //System.out.println("               "+this.level+" Binary:"+String.format("%32s", addressBinary).replace(' ', '0'));
        return(String.format("%32s", addressBinary).replace(' ', '0'));
    }
    private String Index(Long address10){
        //System.out.println("               "+this.level+" Index:  "+Binary(address10).substring((int)this.tagBit,(int)(this.tagBit+this.indexBit)));
        return(Binary(address10).substring((int)this.tagBit,(int)(this.tagBit+this.indexBit)));
    }
    private String Tag(Long address10){
        //System.out.println("               "+this.level+" Tag:    "+Binary(address10).substring((int)(0),(int)(this.tagBit)));
        return(Binary(address10).substring((int)(0),(int)(this.tagBit)));
    }
    private int Smallest (List<CacheCell> row){
        int x = Main.counter;
        for (CacheCell E : row)
            if (E.LRU<=x) {
                x=E.LRU;
            }
    return x;
    }
}
