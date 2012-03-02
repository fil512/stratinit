package com.kenstevens.stratinit.control;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.kenstevens.stratinit.ui.shell.CommandListControl;

public class CommandListControlImpl implements CommandListControl {
	private final org.eclipse.swt.widgets.List list;
	private final Label lines;

	public CommandListControlImpl(List commandList, Label lines) {
		this.list = commandList;
		this.lines = lines;
	}

	public void addItem(final String text) {
		final Display display = Display.getDefault();

		if (list.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				if (list == null || text == null || text.isEmpty()) {
					return;
				}
				if (list.isDisposed())
					return;
				list.add(text, 0);
				list.getParent().layout();
				setLines();
			}
		});
	}

	public void removeLast() {
		final Display display = Display.getDefault();

		if (list.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				if (list.isDisposed())
					return;
				list.remove(list.getItemCount()-1);
				list.getParent().layout();
				setLines();
			}
		});

	}

	public void removeAll() {
		final Display display = Display.getDefault();

		if (list.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				if (list.isDisposed())
					return;
				list.removeAll();
				list.getParent().layout();
			}
		});
	}

	private void setLines() {
		lines.setText(""+list.getItemCount());
	}
}
