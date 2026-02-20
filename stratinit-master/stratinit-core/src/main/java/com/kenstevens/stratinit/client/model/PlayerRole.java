package com.kenstevens.stratinit.client.model;

import jakarta.persistence.*;

@Entity
public class PlayerRole {
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	@Id
	@SequenceGenerator(name="role_id_seq", sequenceName="role_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="role_id_seq")
	private Integer playerRoleId;
	@ManyToOne
	private Player player;
	private String roleName;
	public Integer getPlayerRoleId() {
		return playerRoleId;
	}
	public void setPlayerRoleId(Integer playerRoleId) {
		this.playerRoleId = playerRoleId;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public boolean isAdmin() {
		return roleName.equals(ROLE_ADMIN);
	}


}
