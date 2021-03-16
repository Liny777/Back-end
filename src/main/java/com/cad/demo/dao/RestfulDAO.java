package com.cad.demo.dao;

import com.cad.demo.entity.KneighbourParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;


@Repository
public class RestfulDAO {

    @Autowired
    private RestTemplate restTemplate;
//    @Before
//    public void init() {
//        restTemplate = new RestTemplate();
//    }
    public String getAllpath(String id1,String id2,int maxdepth){
        Long id_1 = Long.parseLong(id1);
        Long id_2 = Long.parseLong(id2);
        String url = "http://114.67.200.39:44640/graphs/hugegraph/traversers/paths?source={id1}&target={id2}&max_depth={maxdepth}";
        Map<String, Object> params = new HashMap<>();
        params.put("id1", id_1);
        params.put("id2", id_2 );
        params.put("maxdepth",maxdepth);
        return restTemplate.getForObject(url, String.class, params);
    }
    public String getAllEdgeLabel(){
        String url = "http://114.67.200.39:44640/graphs/hugegraph/schema/edgelabels";
          return restTemplate.getForObject(url,String.class);
    }
    public String getKNeighbor(KneighbourParams request){
        String url = "http://114.67.200.39:44640/graphs/hugegraph/traversers/kneighbor?source={id}&max_depth={maxdepth}";
        Long id = Long.parseLong(request.getSource());
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("maxdepth",request.getMax_depth());
//        params.put("maxdepth",3);
        return restTemplate.getForObject(url, String.class, params);
//        http://localhost:8080/graphs/{graph}/traversers/kneighbor?source=“1:marko”&max_depth=2
    }
    public  String getShortestPath(int maxdepth, String id1,String id2){
        Long id_1 = Long.parseLong(id1);
        Long id_2 = Long.parseLong(id2);
        String url = "http://114.67.200.39:44640/graphs/hugegraph/traversers/shortestpath?source={id1}&target={id2}&max_depth={maxdepth}";
        Map<String, Object> params = new HashMap<>();
        params.put("id1", id_1);
        params.put("id2", id_2 );
        params.put("maxdepth",maxdepth);
        return restTemplate.getForObject(url, String.class, params);
    }
//    public String getKNeighbor(KneighbourParams request){
    // 看看是不是因为请求的参数是需要全部填写，找一个也是要post，然后参数少点的试试
//        String url = "http://114.67.200.39:44640/graphs/hugegraph/traversers/kneighbor";
//        Long id_1 = Long.parseLong(request.getSource());
////        KneighbourRequest request1 = new KneighbourRequest();
////        request1.setWith_vertex(true);
////        request1.setWith_path(true);
////        request1.setMax_depth(request.getMax_depth());
////        request1.setSource(id_1);
//        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
////        String depth = request.getMax_depth()
////        Map<String, Object> params = new HashMap<>();
//        map.add("source", request.getSource());
//        map.add("max_depth",Integer.toString(request.getMax_depth()));
//        map.add("with_path","true");
//        map.add("with_vertex","true");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<>(map, headers);
//        return restTemplate.postForObject(url,request1,String.class);
//    }
//    public String FindAllPath(Long id1, Long id2){
//
//        String url = "http://114.67.200.39:44640/graphs/hugegraph/traversers/paths?source={id1}&target={id2}&max_depth=5";
//        Map<String, Object> params = new HashMap<>();
//        params.put("id1", 383437495502635008L);
//        params.put("id2", 383438441418850304L );
//        String s = restTemplate.getForObject(url, String.class, params);
//        System.out.println(s);
//        return s;
//    }
}
