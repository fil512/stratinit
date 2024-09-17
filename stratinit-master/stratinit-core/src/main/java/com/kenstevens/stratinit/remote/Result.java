package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
	private RunModeEnum runMode;
	transient private Exception exception;

	public Result() {
	}

	public static Result<None> trueInstance() {
		return new Result<None>(true);
	}

	public static final Result<None> falseInstance() {
		return new Result<>(false);
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

    // Only use this constructor when you're changing the type of the result
    public Result(Result<? extends Object> result, T value) {
        this(result.getMessages(), result.success, value, result.silogs, result.moveSuccess);
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

	public static <T> Result<T> falseInstance(Class<T> resultClass, Exception e) {
		Result<T> retval = new Result<>(false);
		retval.setMessage(e.getMessage());
		retval.setException(e);
		return retval;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("success", success)
				.append("value", value)
				.append("messages", messages)
				.append("silogs", silogs)
				.append("moveSuccess", moveSuccess)
				.append("commandPoints", commandPoints)
				.append("runMode", runMode)
				.append("exception", exception)
				.toString();
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

	public boolean isMoveSuccess() {
		return moveSuccess;
	}

	public void setMoveSuccess(boolean moveSuccess) {
		this.moveSuccess = moveSuccess;
	}

	public int getCommandPoints() {
		return commandPoints;
	}

	public void setCommandPoints(int commandPoints) {
		this.commandPoints = commandPoints;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
