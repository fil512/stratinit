package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.kenstevens.stratinit.type.Constants;

/*
 * States:
 * CREATED
 * SCHEDULED (once enough players)
 * MAPPED (after X hours)
 * STARTED (after X more hours)
 */

@Entity
public class Game extends GameUpdatable implements Serializable {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
	"MM/dd/yyyy H:mm zzz");

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="game_id_seq", sequenceName="game_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="game_id_seq")
	private Integer id;
	@Column(nullable=false)
    private String name;
	private int size;
    private Boolean enabled = true;
    private Date mapped;
    private Date created;
    private Date startTime;
    private Date ends;
    private int duration = 10; // days
    private int islands;
    private int players = 0;
    private int noAlliancesVote = 0;
    private boolean noAlliances = false;
    private boolean blitz = false;

    public Game() {}

    public Game(String name) {
    	this.name = name;
    }

    public Game(String name, int size) {
    	this(name);
    	this.size = size;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Date getExpectedMapTime() {
		if (mapped != null) {
			return mapped;
		}
		if (startTime == null) {
			return null;
		}
		if (isBlitz()) {
			return startTime;
		}
		return new Date(startTime.getTime() - Constants.getMappedToStartedMillis());
    }

	public void setCreated() {
		created = new Date();
    }

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

    public Integer getId() {
        return id;
    }
    public void setId(Integer gameId) {
        this.id = gameId;
    }
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean isEnabled() {
		return enabled;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date started) {
		this.startTime = started;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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


	@Override
	public int getUpdatePeriodMilliseconds() {
		return Constants.TECH_UPDATE_INTERVAL_SECONDS * 1000;
	}

	@Override
	public String toString() {
		return"#"+id+" "+name;
	}

	@Override
	public Object getKey() {
		return id;
	}

	public void setBlitz(boolean blitz) {
		this.blitz = blitz;
	}

	public boolean isBlitz() {
		return blitz;
	}

	@Override
	public Game getGame() {
		return this;
	}

	public boolean hasEnded() {
		Date now = new Date();
		return hasStarted() && ends != null && now.after(ends);
	}

	public boolean hasStarted() {
		return startTime != null && (new Date()).after(startTime) && isMapped();
	}

	public String getCreatedString() {
		if (created == null) {
			return "";
		}
		return FORMAT.format(created);
	}

	public String getStartTimeString() {
		if (startTime == null) {
			return "";
		}
		return FORMAT.format(startTime);
	}

	public String getEndsString() {
		if (ends == null) {
			return "";
		}
		return FORMAT.format(ends);
	}

	public boolean isFullyBooked() {
		return players >= Constants.MAX_PLAYERS_PER_GAME || isMapped() && players >= islands;
	}

	public int addPlayer(boolean noAlliances) {
		if (isFullyBooked()) {
			return -1;
		}
		int retval = players;
		++players;
		return retval;
	}

	public void setMapped(Date mapped) {
		this.mapped = mapped;
	}

	public Date getMapped() {
		return mapped;
	}

	public void setMapped() {
		setMapped(new Date());
	}

	public boolean isMapped() {
		return mapped != null;
	}

	public String getExpectedMapTimeString() {
		Date expectedMapTime = getExpectedMapTime();
		if (expectedMapTime == null) {
			return "";
		}
		return FORMAT.format(expectedMapTime);
	}

	public String getPlayersString() {
		String retval = "";
		if (islands > 0) {
			retval = "" + players + "/"
				+ islands;
		} else if (startTime == null) {
			retval = "" + players + "/"
			+ Constants.MIN_PLAYERS_TO_SCHEDULE;
		} else {
			retval = "" + players + "/"
			+ Constants.MAX_PLAYERS_PER_GAME;
		}
		retval += " ("+noAlliancesVote+")";
		return retval;
	}

	public void setEnds(Date ends) {
		this.ends = ends;
	}

	@Override
	public boolean isKeyUnique() {
		return false;
	}

	public int getNoAlliancesVote() {
		return noAlliancesVote;
	}

	public void setNoAlliancesVote(int noAlliancesVote) {
		this.noAlliancesVote = noAlliancesVote;
	}

	public boolean isNoAlliances() {
		return noAlliances;
	}

	public void setNoAlliances(boolean noAlliances) {
		this.noAlliances = noAlliances;
	}
}
