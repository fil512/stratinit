package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.Constants;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result<T> implements Serializable {
	private static final long serialVersionUID = 2L;
	private boolean success;
	private T value;
	private List<String> messages = new ArrayList<String>();
	private List<SIBattleLog> silogs = new ArrayList<SIBattleLog>();
	private boolean moveSuccess = true;
	private int commandPoints = Constants.UNASSIGNED;

	public static Result<None> trueInstance() {
		return new Result<None>(true);
	}

	public static final Result<None> falseInstance() {
		return new Result<None>(false);
	}

	public Result() {
	}

	public Result(String message, boolean success, T value) {
		setMessage(message);
		this.success = success;
		this.value = value;
	}

	public Result(T value) {
		this("", true, value);
	}

	public Result(boolean success) {
		this("", success, null);
	}

	public Result(List<String> messages) {
		this("", true, null);
		this.messages = messages;
	}

	public Result(List<String> messages, boolean success) {
		this("", success, null);
		this.messages = messages;
	}

	public Result(String message, boolean success) {
		this(message, success, null);
	}

	// Only use this constructor for <None> or failed results
	public Result(Result<? extends Object> result) {
		this(result.getMessages(), result.success, null, result.silogs, result.moveSuccess);
	}

	public Result(List<String> messages, boolean success, T value) {
		this.messages = messages;
		this.success = success;
		this.value = value;
	}

	public Result(SIBattleLog battleLog, boolean success) {
		this(battleLog.messages, success);
		this.silogs.add(battleLog);
	}

	public Result(SIBattleLog battleLog) {
		this(battleLog, true);
	}

	public Result(List<String> messages, boolean success, T value,
			List<SIBattleLog> battleLogs) {
		this(messages, success, value);
		this.silogs = battleLogs;
	}

	public Result(List<String> messages, boolean success, T value,
			List<SIBattleLog> battleLogs, boolean moveSuccess) {
		this(messages, success, value, battleLogs);
		this.moveSuccess = moveSuccess;
	}

	@Override
	public String toString() {
		return getMessage();
	}

	private String getMessage() {
		return StringUtils.join(messages.iterator(), "\n");
	}

	public final void setMessage(String message) {
		if (message != null && !message.isEmpty()) {
			this.messages.add(message);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void and(Result<None> secondResult) {
		addMessages(secondResult.messages);
		this.success &= secondResult.success;
		this.silogs.addAll(secondResult.getBattleLogs());
	}

	public void or(Result<None> secondResult) {
		addMessages(secondResult.messages);
		this.success |= secondResult.success;
		this.silogs.addAll(secondResult.getBattleLogs());
	}

	public void addMessages(List<String> messages) {
		if (messages != null) {
			this.messages.addAll(messages);
		}
	}

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<SIBattleLog> getBattleLogs() {
		return silogs;
	}

	public static <T> Result<T> make(T retval) {
		return new Result<T>(retval);
	}

	public void setMoveSuccess(boolean moveSuccess) {
		this.moveSuccess = moveSuccess;
	}

	public boolean isMoveSuccess() {
		return moveSuccess;
	}

	public void setCommandPoints(int commandPoints) {
		this.commandPoints = commandPoints;
	}

	public int getCommandPoints() {
		return commandPoints;
	}

}
