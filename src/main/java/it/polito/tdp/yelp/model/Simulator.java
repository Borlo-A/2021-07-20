package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Event.EventType;

public class Simulator {

// Dati in ingresso
	private Graph<User, DefaultWeightedEdge> grafo;
	private List<User> users;
	private int nIntervistatori;
	private int nUtenti;
		
// Dati in uscita
	private List<Integer> interviste;
	private int giorni;

// Modello del mondo
	private List<User> daIntervistare;
	private int nIntervistati;
	private int intervistatoriOccupati;
	
// Coda degli eventi
	private PriorityQueue<Event> queue;
	
	public Simulator(Graph<User,DefaultWeightedEdge> grafo, List<User> users)
	{
		this.grafo = grafo;
		this.users = users;
	}
	
	public void init(int nIntervistatori, int nUtenti)
	{
		this.queue = new PriorityQueue<Event>();
		this.nIntervistatori = nIntervistatori;
		this.nUtenti = nUtenti;
		
// Inizializzazione degli output
		this.interviste = new ArrayList<Integer>();
		this.giorni = 0;
		for(int i=0; i<nIntervistatori; i++)
			interviste.add(0);
		
// Inizializzazione del mondo
		this.daIntervistare = new ArrayList<User>(this.users);
		this.nIntervistati = 0;
		
// Caricamento iniziale della coda
		int i=0;
		while(this.nIntervistati<this.nUtenti && this.intervistatoriOccupati<this.nIntervistatori)
		{
	// assegnazione
			User uTemp = this.daIntervistare.get((int)Math.random()*this.daIntervistare.size());
			
			queue.add(new Event(EventType.INIZIO_INTERVISTA, i, uTemp, 1));
			this.daIntervistare.remove(uTemp);
			this.nIntervistati++;
			this.intervistatoriOccupati++;
			i++;
		}
	}
	
	public void run()
	{
		while(!this.queue.isEmpty())
		{
			Event e = this.queue.poll();
			this.giorni = e.getGiorno();
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		
		EventType type = e.getType();
		int intervistatore = e.getIntervistatore();
		User intervistato = e.getIntervistato();
		int giorno = e.getGiorno();
		
		switch(type)
		{
		case INIZIO_INTERVISTA:
			this.interviste.set(intervistatore, this.interviste.get(intervistatore)+1);
			if(Math.random()<0.6)
			{
				queue.add(new Event(EventType.FINE_INTERVISTA, intervistatore, intervistato ,giorno+1));
			}
			
			if(Math.random()>=0.8)
			{
				queue.add(new Event(EventType.FINE_INTERVISTA, intervistatore, intervistato, giorno+2));
			}
			
			else
			{
				this.interviste.set(intervistatore, this.interviste.get(intervistatore)-1);
				queue.add(new Event(EventType.INIZIO_INTERVISTA, intervistatore, intervistato, giorno+1));				
			}
				
			break;
			
		case FINE_INTERVISTA:
			this.intervistatoriOccupati--;
			
			if(this.nIntervistati<this.nUtenti)
			{
	// assegno altro utente
				User uTemp; 
				if(getUtenteSimile(intervistato)==null)
				{
					uTemp = this.daIntervistare.get((int)(this.daIntervistare.size()*Math.random()));
					queue.add(new Event(EventType.INIZIO_INTERVISTA, intervistatore, uTemp, giorno));
				}
				else
				{
					uTemp = getUtenteSimile(intervistato);
					queue.add(new Event(EventType.INIZIO_INTERVISTA, intervistatore, uTemp, giorno));
				}
				this.daIntervistare.remove(uTemp);
				this.intervistatoriOccupati++;
				this.nIntervistati++;
			}
			
			else ;
			
			break;
		}
		
	}
	


	public User getUtenteSimile(User utente)
	{
		List<User> simili = new ArrayList<User>(Graphs.neighborListOf(this.grafo, utente));
		List<User> similiDaRestituire = new ArrayList<User>();
		List<User> similiDaIntervistare = new ArrayList<User>();
		
		for(User u : simili)
		{
			if(this.daIntervistare.contains(u))
				similiDaIntervistare.add(u);
		}
		double max = 1;		

		for(User u : similiDaIntervistare)
		{
			DefaultWeightedEdge edge = this.grafo.getEdge(u, utente);
			if(this.grafo.getEdgeWeight(edge)>max)
			{
				max=this.grafo.getEdgeWeight(edge);
			}
		}
		for(User u : similiDaIntervistare)
		{
			DefaultWeightedEdge edge = this.grafo.getEdge(u, utente);
			if(this.grafo.getEdgeWeight(edge)==max)
			{
				similiDaRestituire.add(u);
			}
		}
		
		if(similiDaRestituire.size()>1)
		{
			return similiDaRestituire.get((int)(Math.random()*similiDaRestituire.size()));
		}
		else if(similiDaRestituire.size()==1)
			return similiDaRestituire.get(0); 
		
		return null;
	}

	public List<Integer> getInterviste() {
		return interviste;
	}

	public int getGiorni() {
		return giorni;
	}

}
