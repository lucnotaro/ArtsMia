package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<ArtObject,DefaultWeightedEdge> graph;
	private List<ArtObject> allNodes;
	private ArtsmiaDAO dao;
	private Map<Integer,ArtObject> idMap;
	
	public Model() {
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.allNodes=new ArrayList<>();
		this.dao=new ArtsmiaDAO();
		this.idMap=new HashMap<>();
	}
	
	public void buildGraph(){
		this.loadNodes();
		Graphs.addAllVertices(this.graph,this.allNodes);
		
// database piccolo:		
/*		for(ArtObject a1:this.allNodes) {
			for(ArtObject a2:this.allNodes) {
				int peso=this.dao.getWeigh(a1.getId(),a2.getId());
				Graphs.addEdgeWithVertices(this.graph,a1,a2,peso);
			}
		} */
		
// database grande:
		List<EdgeModel> allEdges=this.dao.getAllWeight(idMap);
		for(EdgeModel e:allEdges) {
			Graphs.addEdgeWithVertices(this.graph,e.getSource(),e.getTarget(),e.getPeso());
		}
		System.out.println(this.graph.vertexSet().size());
		System.out.println(this.graph.edgeSet().size());
		
	}
	
    public void loadNodes(){
		if(this.allNodes.isEmpty())
			this.allNodes=dao.listObjects();
		if(this.idMap.isEmpty())
			for(ArtObject a:this.allNodes)
				this.idMap.put(a.getId(),a);
	}
    
    public Boolean isIdInGraph(Integer objID) {
    	if(this.idMap.get(objID)!=null)
    		return true;
    	return false;
    }
    
    public Integer calcolaConnessa(Integer objID) {
    	DepthFirstIterator<ArtObject,DefaultWeightedEdge> iterator=new DepthFirstIterator<>(this.graph,this.idMap.get(objID));
    	List<ArtObject> compConnessa=new ArrayList<>();
    	while(iterator.hasNext()) {
    		compConnessa.add(iterator.next());
//    		ConnectivityInspector<ArtObject,DefaultWeightedEdge> inspector= new ConnectivityInspector<>(this.graph);
//    		Set<ArtObject> setConnesso=inspector.connectedSetOf(this.idMap.get(objID)));
    	}
//    	return setConnesso;
    	return compConnessa.size();
    }
}
