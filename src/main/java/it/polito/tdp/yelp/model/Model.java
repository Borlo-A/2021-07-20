package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private Graph<User, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private Map<String, User> idMap;
	public List<User> vertici;
	double max;
	
	Simulator sim;
	int giorni;
	List<Integer> interviste;
	
	public Model()
	{
		dao= new YelpDao();
		idMap = new HashMap<String, User>();
		dao.getAllUsers(idMap);
		sim = new Simulator(this.grafo, vertici);
	}
	
	public void creaGrafo(Integer n, Integer anno)
	{
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
// Aggiungere i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getUsersByReview(idMap, n));
		vertici = new ArrayList<User>(this.dao.getUsersByReview(idMap, n));
		
// Aggiungere gli archi
		List<Review> reviews = new ArrayList<Review>(this.dao.getReviewsByYear(anno));
		
		for(int i=0; i<reviews.size(); i++)
		{
			if(vertici.contains(idMap.get(reviews.get(i).getUserId())))
			{
				for(int j=i+1; j<reviews.size(); j++)
				{
					if(vertici.contains(idMap.get(reviews.get(j).getUserId())))
					{
						if(reviews.get(i).getBusinessId().compareTo(reviews.get(j).getBusinessId())==0 && reviews.get(i).getUserId().compareTo(reviews.get(j).getUserId())!=0)
						{	
							if(idMap.get(reviews.get(i).getUserId())!=null && idMap.get(reviews.get(j).getUserId())!=null)
							{
								User u1 = idMap.get(reviews.get(i).getUserId());
								User u2 = idMap.get(reviews.get(j).getUserId());
								DefaultWeightedEdge edge = this.grafo.getEdge(u1, u2);
								
								if(edge==null)
									Graphs.addEdgeWithVertices(this.grafo, u1, u2, 1);
								else 
								{
									double pesoVecchio = this.grafo.getEdgeWeight(edge);
									double pesoNuovo = pesoVecchio+1;
									this.grafo.setEdgeWeight(edge, pesoNuovo);
								}
							}					
						}
					}
				}
			}
		}
		
	}
	
	public List<User> getUtenteSimile(User utente)
	{
		List<User> simili = new ArrayList<User>(Graphs.neighborListOf(this.grafo, utente));
		List<User> similiCopia = new ArrayList<User>();
		max=1;
		
		for(User u : simili)
		{
			DefaultWeightedEdge edge = this.grafo.getEdge(u, utente);
			if(this.grafo.getEdgeWeight(edge)>max)
			{
				max=this.grafo.getEdgeWeight(edge);
			}
		}
		for(User u : simili)
		{
			DefaultWeightedEdge edge = this.grafo.getEdge(u, utente);
			if(this.grafo.getEdgeWeight(edge)==max)
			{
				similiCopia.add(u);
			}
		}
		setGradoMax(max); 
		return similiCopia;
	}
	
	public void simula(int nIntervistatori, int nUtenti)
	{
		sim = new Simulator(this.grafo, vertici);
		sim.init(nIntervistatori, nUtenti);
		sim.run();
		giorni = sim.getGiorni();
		interviste = new ArrayList<Integer>(sim.getInterviste());
	}
	
	
	public int getGiorni() {
		return giorni;
	}

	public List<Integer> getInterviste() {
		return interviste;
	}

	public double getGradoMax()
	{
	 return max;
	}
	
	public void setGradoMax(double m)
	{
	  this.max=m;
	} 
	
	public Integer getNumeroVertici()
	{
		return this.grafo.vertexSet().size();
	}
	
	public Integer getNumeroArchi()
	{
		return this.grafo.edgeSet().size();
	}
	
	public List<User> getVertici()
	{
		return vertici;
	}
}
