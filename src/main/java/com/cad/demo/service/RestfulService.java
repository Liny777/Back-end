package com.cad.demo.service;

import com.baidu.hugegraph.structure.gremlin.ResultSet;
import com.cad.demo.dao.HugegraphDAO;
import com.cad.demo.dao.RestfulDAO;
import com.cad.demo.entity.HugeGraphPath;
import com.cad.demo.entity.HugegraphNeighbor;
import com.cad.demo.entity.KneighbourParams;
import com.cad.demo.entity.KneighbourRequest;
import com.cad.demo.entity.vo.PathBetweenTwoNode;
import com.cad.demo.entity.vo.edgedata;
import com.cad.demo.entity.vo.nodedata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RestfulService {



        @Autowired
        private RestfulDAO restfulDAO;
        @Autowired
        private HugegraphService hugegraphService;


    public String[] getNode(String s) {
        String[] NodeArray = s.split(",");
        for (int i=0; i<NodeArray.length;i++) {
            if(i==0) {
                int len = NodeArray[0].length();
                NodeArray[0] = NodeArray[0].substring(14, len);
            }
            if(i==NodeArray.length-1) {
                int len1 = NodeArray[i].length() - 2;
                NodeArray[i] = NodeArray[i].substring(0, len1);
            }
//            System.out.println(NodeArray[i]);
        }
        return NodeArray;
    }
        public String[] getEdge(String s) { // {"path": [
            String[] EdgeArray = s.split(",");
            for (int i=0; i<EdgeArray.length;i++) {
                if(i==0) {
                    int len = EdgeArray[0].length();
                    EdgeArray[0] = EdgeArray[0].substring(10, len);
                }
                if(i==EdgeArray.length-1) {
                    int len1 = EdgeArray[i].length() - 2;
                    EdgeArray[i] = EdgeArray[i].substring(0, len1);
                }
//                System.out.println(EdgeArray[i]);
            }
            return EdgeArray;
    }

    public HugegraphNeighbor getNeighbour(KneighbourParams request) {
            HugegraphNeighbor ne = new HugegraphNeighbor();
            List<Object> path = new ArrayList<>();
            List<Object> datas = new ArrayList<>();
            String s = restfulDAO.getKNeighbor(request);
            String sid = request.getSource();
            String slabel = hugegraphService.getLabel(sid).toString();
            ne.setSid(sid); // 设置源节点
            String[] node = getNode(s);
            // 将得到的邻居节点字符串进行整理后存入sourceArray
            nodedata s_1 = new nodedata();
            s_1.setId(sid);
            s_1.setLabel(slabel);
            String sname = hugegraphService.getName(sid).toString();
            s_1.setName(sname);
            datas.add(s_1);
            // 得到起始点到邻居节点的最短路径
            for (int j=0;j<node.length;j++){
                if(node[j].equals(sid) == false){
                    // 添加数据节点
                    nodedata data = new nodedata();
                    data.setId(node[j]);
                    data.setLabel(hugegraphService.getLabel(node[j]).toString());
                    data.setName(hugegraphService.getName(node[j]).toString());
                    datas.add(data);
                    // 添加边
                    String s1 = restfulDAO.getShortestPath(request.getMax_depth(),sid,node[j]);
                    String[] edge = getEdge(s1);
                    // k=1开始是因为k=0是源节点
                    for (int k=1; k<edge.length; k++) {
                        edgedata link = new edgedata();
                        link.setDescription(hugegraphService.getEdgeDescriptionById(sid,node[j]).toString());
                        link.setHeadId(sid);
                        String relation = hugegraphService.StartEndFindEdgeLabel(sid,node[j]).toString();
                        int length_relation = relation.length()-1;
                        link.setRelation(relation.substring(1,length_relation));
                        link.setTailId(node[j]);
                        link.setHeadName(sname);
                        link.setTailName(hugegraphService.getName(node[j]).toString());
                        link.setHeadLabel(slabel);
                        link.setTailLabel(hugegraphService.getLabel(node[j]).toString());
                        path.add(link);
                    }
                }

            }
            ne.setNode(datas);
            ne.setPath(path);
            return ne;
        }

        public HugeGraphPath getAllpath(String id1,String id2,int maxdepth){
            HugeGraphPath p = new HugeGraphPath();
            if(id1 != null && id1.length() != 0 && id2 != null && id2.length() != 0){
                String s = restfulDAO.getAllpath(id1,id2,maxdepth);
                System.out.println("I get string s!");
                p.setSId(id1);
                p.setEId(id2);
                String headName = hugegraphService.getName(id1).toString();
                String headLabel = hugegraphService.getLabel(id1).toString();
                p.setSLabel(headLabel);
                String tailLabel = hugegraphService.getLabel(id2).toString();
                p.setELabel(tailLabel);
                p.setSName(headName);
                String tailName = hugegraphService.getName(id2).toString();
                p.setEName(tailName);
                System.out.println(s);

                String[] sourceArray = s.split("object");
                //sourceArray 里存了所有路径，里面是多条路
                if(sourceArray.length==0){

                }else{
                    List<Object> path = new ArrayList<>();
                    // i=0 是 "0{"paths":[" 所以i 从1开始
                    for (int i = 1; i < sourceArray.length; i++) {
                        String[] sourceArray1 = sourceArray[i].split(",");
                        //sourceArray1 里是一条路径，里面是多段路
                        List<Object> someList = new ArrayList<>();
                        //一条路径一个someList
                        for (int j = 1; j < sourceArray1.length-2; j++){
                            PathBetweenTwoNode small_path = new PathBetweenTwoNode();
                            System.out.println("i: "+i +" j: "+j+" sourceArray1[j]: "+sourceArray1[j]+" length: "+sourceArray1.length);
//                    //一段路（两个顶点之间）一个small_path
                            if(j==1){
                                small_path.setHeadId(id1);
                                // ..........................................
//                                small_path.setHeadDes(hugegraphService.getProperties(id1).toString());
                                small_path.setTailId(sourceArray1[j]);
                                // ..........................................
//                                small_path.setTailDes(hugegraphService.getProperties(sourceArray1[j]).toString());
                                small_path.setHeadName(headName);
                                small_path.setHeadLabel(headLabel);
                                small_path.setTailName(hugegraphService.getName(sourceArray1[j]).toString());
                                //-----------------------------------------------
                                small_path.setTailLabel(hugegraphService.getLabel(sourceArray1[j]).toString());
                                //-----------------------------------------------
                                String local_relation = hugegraphService.StartEndFindEdgeLabel(id1,sourceArray1[j]).toString();
                                int length_relation = local_relation.length()-1;
                                small_path.setRelation(local_relation.substring(1,length_relation));
                                // -------------------------------------------------
                                small_path.setDescription(hugegraphService.getEdgeDescriptionById(id1,sourceArray1[j]).toString());
                                //-----------------------------------------------
                                System.out.println("small_path.getHeadName(): " + small_path.getHeadName());
                                System.out.println("small_path.getHeadId(): " + small_path.getHeadId());
                                System.out.println("small_path.getRelation(): " + small_path.getRelation());
                                System.out.println("small_path.getTailName(): " + small_path.getTailName());
                            }else{
                                small_path.setHeadId(sourceArray1[j-1]);
                                // ..........................................
//                                small_path.setHeadDes(hugegraphService.getProperties(sourceArray1[j-1]).toString());
                                small_path.setTailId(sourceArray1[j]);
                                // ..........................................
//                                small_path.setTailDes(hugegraphService.getProperties(sourceArray1[j]).toString());
                                small_path.setHeadName(hugegraphService.getName(sourceArray1[j-1]).toString());
                                small_path.setTailName(hugegraphService.getName(sourceArray1[j]).toString());
                                //-----------------------------------------------
                                small_path.setHeadLabel(hugegraphService.getLabel(sourceArray1[j-1]).toString());
                                small_path.setTailLabel(hugegraphService.getLabel(sourceArray1[j]).toString());
                                //-----------------------------------------------
                                String local_relation = hugegraphService.StartEndFindEdgeLabel(sourceArray1[j-1],sourceArray1[j]).toString();
                                int length_relation = local_relation.length()-1;
                                small_path.setRelation(local_relation.substring(1,length_relation));
                                // -------------------------------------------------
                                small_path.setDescription(hugegraphService.getEdgeDescriptionById(sourceArray1[j-1],sourceArray1[j]).toString());
                                //-----------------------------------------------
                            }

                            someList.add(small_path); // 把每一段路都存进来，一个someList一条完整的路

                            //最后一段路
                            if ( sourceArray1[sourceArray1.length-1].equals("{\"") && j == sourceArray1.length-3){ //-3是把最后一个括号，首先length比起数组index多了1，所以减去1，后面减2直接定位到倒数第二个，就是除去末尾节点那个
                                PathBetweenTwoNode small_path1 = new PathBetweenTwoNode();
                                small_path1.setHeadId(sourceArray1[j]);
                                // ..........................................
//                                small_path1.setHeadDes(hugegraphService.getProperties(sourceArray1[j]).toString());
                                small_path1.setTailId(id2);
                                // ..........................................
//                                small_path1.setTailDes(hugegraphService.getProperties(id2).toString());
                                small_path1.setHeadName(hugegraphService.getName(sourceArray1[j]).toString());
                                //-----------------------------------------------
                                small_path1.setHeadLabel(hugegraphService.getLabel(sourceArray1[j]).toString());
                                //-------------------------------------------
                                small_path1.setTailName(tailName);
                                small_path1.setTailLabel(tailLabel);
                                //-----------------------------------------------
                                String local_relation = hugegraphService.StartEndFindEdgeLabel(sourceArray1[j],id2).toString();
                                int length_relation = local_relation.length()-1;
                                small_path1.setRelation(local_relation.substring(1,length_relation));
                                // -------------------------------------------------
                                small_path1.setDescription(hugegraphService.getEdgeDescriptionById(sourceArray1[j],id2).toString());
                                //-----------------------------------------------
                                someList.add(small_path1);
                            }
                            if(sourceArray1[sourceArray1.length-1].equals("{\"") == false){
                                PathBetweenTwoNode small_path1 = new PathBetweenTwoNode();
                                small_path1.setHeadId(sourceArray1[j]);
                                // ..........................................
//                                small_path1.setHeadDes(hugegraphService.getProperties(sourceArray1[j]).toString());
                                small_path1.setTailId(sourceArray1[j+1]);
                                // ..........................................
//                                small_path1.setTailDes(hugegraphService.getProperties(sourceArray1[j+1]).toString());
                                small_path1.setHeadName(hugegraphService.getName(sourceArray1[j]).toString());
                                small_path1.setTailName(hugegraphService.getName(sourceArray1[j+1]).toString());
                                //----------------------------------------------
                                small_path1.setHeadLabel(hugegraphService.getLabel(sourceArray1[j]).toString());
                                small_path1.setTailLabel(hugegraphService.getLabel(sourceArray1[j+1]).toString());
                                //-----------------------------------------------
                                String local_relation = hugegraphService.StartEndFindEdgeLabel(sourceArray1[j],sourceArray1[j+1]).toString();
                                int length_relation = local_relation.length()-1;
                                small_path1.setRelation(local_relation.substring(1,length_relation));
                                small_path1.setDescription(hugegraphService.getEdgeDescriptionById(sourceArray1[j],sourceArray1[j+1]).toString());
                                //-----------------------------------------------
                                someList.add(small_path1);

                                PathBetweenTwoNode small_path2 = new PathBetweenTwoNode();
                                small_path2.setHeadId(sourceArray1[j+1]);
                                // ..........................................
//                                small_path2.setHeadDes(hugegraphService.getProperties(sourceArray1[j+1]).toString());
                                small_path2.setTailId(id2);
                                // ..........................................
//                                small_path2.setTailDes(hugegraphService.getProperties(id2).toString());
                                small_path2.setHeadName(hugegraphService.getName(sourceArray1[j+1]).toString());
                                //----------------------------------------------
                                small_path2.setHeadLabel(hugegraphService.getLabel(sourceArray1[j+1]).toString());
                                small_path2.setTailName(tailName);
                                small_path2.setTailLabel(tailLabel);
                                //-----------------------------------------------
                                String local_relation1 = hugegraphService.StartEndFindEdgeLabel(sourceArray1[j+1],id2).toString();
                                int length_relation1 = local_relation1.length()-1;
                                small_path2.setRelation(local_relation1.substring(1,length_relation1));
                                small_path2.setDescription(hugegraphService.getEdgeDescriptionById(sourceArray1[j+1],id2).toString());
                                //-----------------------------------------------
                                someList.add(small_path2);
                            }


                            // example
                            //// i: 1 j: 0 sourceArray1[j]: s":[383437495502635008 length: 6
                            ////i: 1 j: 1 sourceArray1[j]: 383437494080765952 length: 6
                            ////i: 1 j: 2 sourceArray1[j]: 383434603748130816 length: 6
                            ////i: 1 j: 3 sourceArray1[j]: 383438751331778560 length: 6
                            ////i: 1 j: 4 sourceArray1[j]: 383438441418850304]} length: 6
                            ////i: 1 j: 5 sourceArray1[j]: {" length: 6
                        }
                        if(sourceArray1.length==3){
                            // 直达路径，两个顶点中间只有一条线
                            PathBetweenTwoNode small_path = new PathBetweenTwoNode();
                            small_path.setHeadId(id1);
                            // ..........................................
//                            small_path.setHeadDes(hugegraphService.getProperties(id1).toString());
                            small_path.setTailId(id2);
                            // ..........................................
//                            small_path.setTailDes(hugegraphService.getProperties(id2).toString());
                            small_path.setHeadName(headName);
                            small_path.setTailName(tailName);
                            small_path.setHeadLabel(headLabel);
                            small_path.setTailLabel(tailLabel);
                            //-----------------------------------------------
                            String local_relation = hugegraphService.StartEndFindEdgeLabel(id1,id2).toString();
                            int length_relation = local_relation.length()-1;
                            small_path.setRelation(local_relation.substring(1,length_relation));
                            small_path.setDescription(hugegraphService.getEdgeDescriptionById(id1,id2).toString());

                            //-----------------------------------------------
                            someList.add(small_path);
                        }
                        path.add(someList);

                    }
                    p.setPathNode(path);
                }
                return p;
            }else{
                return p;
            }
        }

        public String getAllEdgeLabel(){
            String s = restfulDAO.getAllEdgeLabel();
            String[] sourceArray = s.split("name");
            for (int i = 0; i < sourceArray.length; i++) {
                System.out.println(sourceArray[i]);
            }
            System.out.println(s);
            return s;
        }


}
