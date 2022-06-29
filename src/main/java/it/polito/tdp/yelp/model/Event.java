package it.polito.tdp.yelp.model;

public class Event implements Comparable<Event>{
	
	public enum EventType
	{
		INIZIO_INTERVISTA,
		FINE_INTERVISTA
	}
	
	private EventType type;
	private int intervistatore;
	private User intervistato;
	private int giorno;
	
	public Event(EventType type, int intervistatore, User intervistato, int giorno) {
		super();
		this.type = type;
		this.intervistatore = intervistatore;
		this.intervistato = intervistato;
		this.giorno = giorno;
	}
	
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public int getIntervistatore() {
		return intervistatore;
	}
	public void setIntervistatore(int intervistatore) {
		this.intervistatore = intervistatore;
	}
	public User getIntervistato() {
		return intervistato;
	}
	public void setIntervistato(User intervistato) {
		this.intervistato = intervistato;
	}
	public int getGiorno() {
		return giorno;
	}
	public void setGiorno(int giorno) {
		this.giorno = giorno;
	}

	@Override
	public int compareTo(Event o) {
		return this.giorno - o.getGiorno();
	}
	
	
	
}
