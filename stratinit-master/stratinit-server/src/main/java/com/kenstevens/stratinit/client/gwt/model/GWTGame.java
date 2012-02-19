package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;
import java.util.Date;


public class GWTGame implements Serializable, GWTEntity<Integer> {

	private static final long serialVersionUID = 1L;
	private Integer id;
    private String name;
	private int size;
    private Boolean enabled;
    private int players = 0;
    private int islands;
    private Date created;
    private Date started;
    private Date ends;
    private boolean mapped;

    public GWTGame() {}

    public GWTGame(String name) {
    	this.name = name;
    }


	public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public int getPlayers() {
		return players;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getStarted() {
		return started;
	}

	public void setEnds(Date ends) {
		this.ends = ends;
	}

	public Date getEnds() {
		return ends;
	}

	public void setIslands(int islands) {
		this.islands = islands;
	}

	public int getIslands() {
		return islands;
	}

	public StratInitListGridRecord<Integer, GWTGame> getListGridRecord() {
		return new GameListGridRecord(this);
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setMapped(boolean mapped) {
		this.mapped = mapped;
	}

	public boolean isMapped() {
		return mapped;
	}


}
