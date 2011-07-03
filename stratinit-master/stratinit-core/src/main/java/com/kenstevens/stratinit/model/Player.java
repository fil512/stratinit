package com.kenstevens.stratinit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;


@Entity
public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="player_id_seq", sequenceName="player_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="player_id_seq")
	private Integer id;
	@Column(unique=true)
	private String username;
	private String password;
	private String email;
	private boolean enabled = true;
	private int wins;
	private int played;
	private Date created;
	private boolean emailGameMail = true;

	public Player() {}

    public Player(String username) {
    	this.username = username;
    	this.created = new Date();
    }

	public Player(String username, int id) {
		this(username);
		this.id = id;
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
		Player other = (Player) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getWins() {
		return wins;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getPlayed() {
		return played;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void addPlayed() {
		++played;
	}

	public void addWins() {
		++wins;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setEmailGameMail(boolean emailGameMail) {
		this.emailGameMail = emailGameMail;
	}

	public boolean isEmailGameMail() {
		return emailGameMail;
	}

	public void copyFrom(Player player) {
		this.email = player.email;
		this.emailGameMail = player.emailGameMail;
		this.enabled = player.enabled;
		this.password = player.password;
		this.played = player.played;
		this.wins = player.wins;
	}
}
